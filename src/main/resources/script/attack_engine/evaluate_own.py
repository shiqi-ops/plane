import torch
import torchvision
import torchvision.transforms as transforms
import torchattacks
import matplotlib.pyplot as plt
import argparse
import json
import os

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


def parse_args():

    parser = argparse.ArgumentParser()

    parser.add_argument("--model_path",required=True)

    parser.add_argument("--dataset",required=True)

    parser.add_argument("--attack",default="FGSM")

    parser.add_argument("--eps",type=float,default=0.03)

    return parser.parse_args()


# =========================
# 加载用户模型
# =========================
def load_user_model(path):

    model = torchvision.models.resnet18()

    model.fc = torch.nn.Linear(model.fc.in_features,4)

    model.load_state_dict(torch.load(path,map_location=device))

    model = model.to(device)

    model.eval()

    return model


# =========================
# 数据集
# =========================
def load_dataset(name):

    transform = transforms.Compose([

        transforms.Resize((224,224)),

        transforms.ToTensor()

    ])

    dataset = torchvision.datasets.ImageFolder(

        root=f"../datasets/{name}",

        transform=transform

    )

    loader = torch.utils.data.DataLoader(

        dataset,

        batch_size=32,

        shuffle=False

    )

    return loader,len(dataset)


# =========================
# 获取攻击
# =========================
def get_attack(model,name,eps):

    if name=="FGSM":
        return torchattacks.FGSM(model,eps)

    if name=="PGD":
        return torchattacks.PGD(model,eps)

    if name=="BIM":
        return torchattacks.BIM(model,eps)

    if name=="MIFGSM":
        return torchattacks.MIFGSM(model,eps)

    if name=="DeepFool":
        return torchattacks.DeepFool(model)

    else:
        raise ValueError("unsupported attack")


# =========================
# 测试原始准确率
# =========================
def test_clean(model,loader):

    correct = 0
    total = 0

    for images,labels in loader:

        images = images.to(device)
        labels = labels.to(device)

        with torch.no_grad():

            outputs = model(images)

        _,pred = torch.max(outputs,1)

        total += labels.size(0)

        correct += (pred==labels).sum().item()

    return correct/total


# =========================
# 攻击测试
# =========================
def test_attack(model,loader,attack):

    correct = 0
    total = 0

    ori_img = None
    adv_img = None

    for images,labels in loader:

        images = images.to(device)
        labels = labels.to(device)

        adv = attack(images,labels)

        outputs = model(adv)

        _,pred = torch.max(outputs,1)

        total += labels.size(0)

        correct += (pred==labels).sum().item()

        if adv_img is None:

            adv_img = adv[0].detach().cpu()

            ori_img = images[0].detach().cpu()

    return correct,total,ori_img,adv_img


# =========================
# 保存对比图
# =========================
def save_compare(ori,adv):

    plt.figure()

    plt.subplot(1,2,1)

    plt.imshow(ori.permute(1,2,0))

    plt.title("Original")

    plt.subplot(1,2,2)

    plt.imshow(adv.permute(1,2,0))

    plt.title("Adversarial")

    plt.savefig("../results/compare.png")

    plt.close()


# =========================
# eps鲁棒性曲线
# =========================
def robustness_curve(model,loader,attack_name):

    eps_list=[0.01,0.02,0.03,0.05]

    acc_list=[]

    for eps in eps_list:

        attack=get_attack(model,attack_name,eps)

        correct,total,_,_=test_attack(model,loader,attack)

        acc_list.append(correct/total)

    plt.figure()

    plt.plot(eps_list,acc_list,marker="o")

    plt.xlabel("eps")

    plt.ylabel("accuracy")

    plt.title("robustness curve")

    plt.savefig("../results/robustness_curve.png")

    plt.close()


# =========================
# 主程序
# =========================
if __name__=="__main__":

    args=parse_args()

    os.makedirs("../results",exist_ok=True)

    model=load_user_model(args.model_path)

    loader,size=load_dataset(args.dataset)

    clean_acc=test_clean(model,loader)

    attack=get_attack(model,args.attack,args.eps)

    correct,total,ori,adv=test_attack(model,loader,attack)

    adv_acc=correct/total

    drop=clean_acc-adv_acc

    success=1-adv_acc

    save_compare(ori,adv)

    robustness_curve(model,loader,args.attack)

    report={

        "model_path":args.model_path,

        "dataset":args.dataset,

        "dataset_size":size,

        "attack":args.attack,

        "clean_accuracy":clean_acc,

        "adv_accuracy":adv_acc,

        "accuracy_drop":drop,

        "attack_success_rate":success

    }

    with open("../results/evaluation_report.json","w") as f:

        json.dump(report,f,indent=4)

    print(json.dumps(report,indent=4))