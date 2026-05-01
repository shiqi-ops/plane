import torch
import torchvision
import torchvision.transforms as transforms
import torchattacks
import matplotlib.pyplot as plt
import argparse
import json
import os
import csv
import pandas as pd
import seaborn as sns
import torchvision.utils as vutils
import time

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


ATTACK_GROUPS = {

    "fast": ["FGSM","RFGSM","FFGSM"],

    "iterative": ["BIM","PGD","PGDL2"],

    "transfer": ["MIFGSM","NIFGSM","DIFGSM"],

    "strong": ["CW","DeepFool","AutoAttack"]
}


def parse_args():

    parser = argparse.ArgumentParser()

    parser.add_argument("--model",required=True)
    parser.add_argument("--dataset",required=True)
    parser.add_argument("--attack_group",default="iterative")
    parser.add_argument("--eps",type=float,default=0.03)

    return parser.parse_args()



def load_model(name):

    if name=="resnet18":

        model = torchvision.models.resnet18()

        # 修复：model.f -> model.fc
        model.fc = torch.nn.Linear(model.fc.in_features,4)

        path = "../models/resnet18_drone.pth"

    elif name=="mobilenet":

        model = torchvision.models.mobilenet_v2()

        model.classifier[1] = torch.nn.Linear(model.last_channel,4)

        path = "../models/mobilenetv2_drone.pth"

    elif name=="efficientnet":

        model = torchvision.models.efficientnet_b0()

        model.classifier[1] = torch.nn.Linear(
            model.classifier[1].in_features,4)

        path = "../models/efficientnet_drone.pth"

    else:

        raise ValueError("unsupported model")

    # ✅【最终修复】load_state → load_state_dict()
    model.load_state_dict(torch.load(path,map_location=device))

    model = model.to(device)

    model.eval()

    return model



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



def get_attack(model,name,eps):

    if name=="FGSM":
        return torchattacks.FGSM(model,eps)

    if name=="RFGSM":
        return torchattacks.RFGSM(model,eps)

    if name=="FFGSM":
        return torchattacks.FFGSM(model,eps)

    if name=="BIM":
        return torchattacks.BIM(model,eps)

    if name=="PGD":
        return torchattacks.PGD(model,eps)

    if name=="PGDL2":
        return torchattacks.PGDL2(model,eps)

    if name=="MIFGSM":
        return torchattacks.MIFGSM(model,eps)

    if name=="NIFGSM":
        return torchattacks.NIFGSM(model,eps)

    if name=="DIFGSM":
        return torchattacks.DIFGSM(model,eps)

    if name=="CW":
        return torchattacks.CW(model)

    if name=="DeepFool":
        return torchattacks.DeepFool(model)

    if name=="AutoAttack":
        return torchattacks.AutoAttack(model)



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



def test_attack(model,loader,attack):

    correct = 0
    total = 0

    adv_samples = None
    ori_samples = None

    start = time.time()

    for images,labels in loader:

        images = images.to(device)
        labels = labels.to(device)

        adv = attack(images,labels)

        outputs = model(adv)

        _,pred = torch.max(outputs,1)

        total += labels.size(0)

        correct += (pred==labels).sum().item()

        if adv_samples is None:

            adv_samples = adv[:8].detach().cpu()
            ori_samples = images[:8].detach().cpu()

    attack_time = time.time() - start

    return correct,total,ori_samples,adv_samples,attack_time



def plot_bar(names,accs):

    plt.figure(figsize=(8,5))

    plt.bar(names,accs)

    plt.ylabel("Accuracy")

    plt.title("Attack Robustness Comparison")

    plt.savefig("../results/attack_bar.png")

    plt.close()



def robustness_curve(model,loader,attack_name):

    eps_list=[0.01,0.02,0.03,0.05]

    acc_list=[]

    for eps in eps_list:

        attack = get_attack(model,attack_name,eps)
        
        # ✅【修复】接收5个返回值
        correct,total,_,_,_ = test_attack(model,loader,attack)

        acc_list.append(correct/total)

    plt.figure()

    plt.plot(eps_list,acc_list,marker="o")

    plt.xlabel("Eps")

    plt.ylabel("Accuracy")

    plt.title("Robustness Curve")

    plt.savefig("../results/robustness_curve.png")

    plt.close()



def save_compare(ori,adv):

    plt.figure()

    plt.subplot(1,2,1)

    plt.imshow(ori[0].permute(1,2,0))

    plt.title("Original")

    plt.subplot(1,2,2)

    plt.imshow(adv[0].permute(1,2,0))

    plt.title("Adversarial")

    plt.savefig("../results/compare.png")

    plt.close()



def save_adv_gallery(adv,attack):

    path=f"../results/adversarial_gallery/{attack}"

    os.makedirs(path,exist_ok=True)

    grid=vutils.make_grid(adv,normalize=True)

    vutils.save_image(grid,f"{path}/samples.png")



def generate_heatmap(model,loader,attacks):

    eps_list=[0.01,0.03,0.05]

    data=[]

    for eps in eps_list:

        row=[]

        for name in attacks:

            attack=get_attack(model,name,eps)

            # ✅【修复】接收5个返回值
            correct,total,_,_,_=test_attack(model,loader,attack)

            row.append(correct/total)

        data.append(row)

    df=pd.DataFrame(data,index=eps_list,columns=attacks)

    plt.figure(figsize=(8,6))

    sns.heatmap(df,annot=True,cmap="coolwarm")

    plt.savefig("../results/attack_heatmap.png")

    plt.close()


# ==============================
# 【核心评测公式】
# ==============================
def calculate_score(clean_acc, adv_list):
    if clean_acc == 0:
        return 0.0, "Poor"
    
    avg_adv = sum(adv_list) / len(adv_list)
    delta_acc = clean_acc - avg_adv
    score = 100 * (1 - delta_acc / clean_acc)

    if score > 90:
        level = "Strong"
    elif score > 70:
        level = "Medium"
    elif score > 50:
        level = "Weak"
    else:
        level = "Poor"
    
    return score, level


def generate_html(report):

    html=f"""
    <html>
    <body>

    <h1>Drone Robustness Report</h1>

    <h2>Model</h2>
    <p>{report["model"]}</p>

    <h2>Dataset</h2>
    <p>{report["dataset"]}</p>

    <h2>Clean Accuracy</h2>
    <p>{report["clean_accuracy"]:.4f}</p>

    <h2>Robust Score</h2>
    <p>{report["robust_score"]:.2f}</p>

    <h2>Robust Level</h2>
    <p>{report["robust_level"]}</p>

    <h2>Bar</h2>
    <img src="attack_bar.png" width="600">

    <h2>Curve</h2>
    <img src="robustness_curve.png" width="600">

    <h2>Heatmap</h2>
    <img src="attack_heatmap.png" width="600">

    </body>
    </html>
    """

    with open("../results/report.html","w") as f:
        f.write(html)

def plot_attack_bubble(results):

    x=[]
    y=[]
    size=[]
    labels=[]

    for r in results:

        x.append(r["query_time"])
        y.append(r["attack_success_rate"])
        size.append(r["eps"]*2000)
        labels.append(r["attack"])

    plt.figure(figsize=(8,6))

    plt.scatter(x,y,s=size,alpha=0.6)

    for i,label in enumerate(labels):

        plt.text(x[i],y[i],label)

    plt.xlabel("Query Time")
    plt.ylabel("Attack Success Rate")
    plt.title("Bubble Chart")

    plt.savefig("../results/attack_bubble.png")

    plt.close()


if __name__=="__main__":

    args=parse_args()

    os.makedirs("../results",exist_ok=True)

    model=load_model(args.model)

    loader,size=load_dataset(args.dataset)

    clean_acc=test_clean(model,loader)

    attacks=ATTACK_GROUPS[args.attack_group]

    results=[]

    names=[]
    accs=[]

    for name in attacks:

        print("running",name)

        attack=get_attack(model,name,args.eps)

        correct,total,ori,adv,attack_time = test_attack(model,loader,attack)
        query_count = attack_time

        adv_acc=correct/total

        drop=clean_acc-adv_acc

        success=1-adv_acc

        names.append(name)

        accs.append(adv_acc)

        save_compare(ori,adv)

        save_adv_gallery(adv,name)

        results.append({

            "attack":name,
            "clean_accuracy":clean_acc,
            "adv_accuracy":adv_acc,
            "accuracy_drop":drop,
            "attack_success_rate":success,
            "query_time":query_count,
            "eps":args.eps
        })


    ranking=sorted(results,key=lambda x:x["adv_accuracy"])

    plot_bar(names,accs)
    robustness_curve(model,loader,names[0])
    generate_heatmap(model,loader,names)
    plot_attack_bubble(results)

    with open("../results/attack_results.csv","w",newline="") as f:
        writer=csv.DictWriter(f,fieldnames=results[0].keys())
        writer.writeheader()
        for r in results:
            writer.writerow(r)

    score,level=calculate_score(clean_acc,accs)

    report={
         "model":args.model,
         "dataset":args.dataset,
         "dataset_size":size,
         "clean_accuracy":clean_acc,
         "attack_results":results,
         "ranking":ranking,
         "robust_score":score,
         "robust_level":level,
         "bubble_data":[
            {
                "attack":r["attack"],
                "query_time":r["query_time"],
                "success_rate":r["attack_success_rate"],
                "eps":r["eps"]
            } for r in results
         ]
    }

    with open("../results/evaluation_report.json","w") as f:
        json.dump(report,f,indent=4)

    generate_html(report)
    print(json.dumps(report,indent=4))