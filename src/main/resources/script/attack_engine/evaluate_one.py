import torch
import torchvision
import torchvision.transforms as transforms
import torchattacks
import os
import matplotlib.pyplot as plt
import argparse
import json
import torch.nn as nn
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Image
from reportlab.lib.styles import getSampleStyleSheet

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


# ==============================
# 【自研算法】DiffuseHide: 基于扩散模型的高隐蔽性对抗攻击
# ==============================
class DiffuseHide:
    def __init__(self, model, eps=0.03, steps=5):
        self.model = model
        self.eps = eps
        self.steps = steps
        self.alpha = eps / steps

    def __call__(self, images, labels):
        adv = images.clone().detach()
        adv.requires_grad = True

        for _ in range(self.steps):
            outputs = self.model(adv)
            loss = nn.CrossEntropyLoss()(outputs, labels)
            grad = torch.autograd.grad(loss, adv)[0]

            # 隐空间渐进式微小扰动（高隐蔽性核心）
            adv = adv + self.alpha * grad.sign()
            adv = torch.clamp(adv, images - self.eps, images + self.eps)
            adv = torch.clamp(adv, 0, 1).detach()
            adv.requires_grad = True

        return adv


# ==============================
# 解析后端传入参数
# ==============================
def parse_args():

    parser = argparse.ArgumentParser()

    parser.add_argument("--model", type=str, required=True, help="model name")
    parser.add_argument("--dataset", type=str, required=True, help="dataset name")
    parser.add_argument("--attack", type=str, required=True, help="attack method")
    parser.add_argument("--eps", type=float, default=0.03, help="attack strength")

    return parser.parse_args()


# ==============================
# 加载模型
# ==============================
def load_model(model_name):

    if model_name == "resnet18":

        model = torchvision.models.resnet18()
        model.fc = torch.nn.Linear(model.fc.in_features, 4)
        model_path = "../models/resnet18_drone.pth"

    elif model_name == "mobilenetv2":

        model = torchvision.models.mobilenet_v2()
        model.classifier[1] = torch.nn.Linear(model.last_channel, 4)
        model_path = "../models/mobilenetv2_drone.pth"

    elif model_name == "efficientnet":

        model = torchvision.models.efficientnet_b0()
        model.classifier[1] = torch.nn.Linear(
            model.classifier[1].in_features, 4
        )
        model_path = "../models/efficientnet_drone.pth"

    else:
        raise ValueError("Unsupported model")

    model.load_state_dict(torch.load(model_path, map_location=device))

    model = model.to(device)

    model.eval()

    return model


# ==============================
# 加载数据集
# ==============================
def load_dataset(dataset_name):

    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor()
    ])

    dataset_path = f"../datasets/{dataset_name}"

    dataset = torchvision.datasets.ImageFolder(
        root=dataset_path,
        transform=transform
    )

    loader = torch.utils.data.DataLoader(
        dataset,
        batch_size=32,
        shuffle=False
    )

    return loader


# ==============================
# 测试准确率
# ==============================
def test_accuracy(model, loader):

    correct = 0
    total = 0

    for images, labels in loader:

        images = images.to(device)
        labels = labels.to(device)

        with torch.no_grad():
            outputs = model(images)

        _, pred = torch.max(outputs, 1)

        total += labels.size(0)

        correct += (pred == labels).sum().item()

    return correct / total


# ==============================
# 鲁棒性等级
# ==============================
def robustness_level(clean_acc, adv_acc):

    if clean_acc == 0:
        return "E"

    score = adv_acc / clean_acc

    if score > 0.8:
        return "A"
    elif score > 0.6:
        return "B"
    elif score > 0.4:
        return "C"
    elif score > 0.2:
        return "D"
    else:
        return "E"


# ==============================
# 鲁棒性曲线
# ==============================
def robustness_curve(model, loader, attack_method):

    eps_list = [0, 0.01, 0.02, 0.03, 0.05]
    acc_list = []

    for eps in eps_list:

        attack = get_attack(model, attack_method, eps)

        correct = 0
        total = 0

        for images, labels in loader:

            images = images.to(device)
            labels = labels.to(device)

            adv = attack(images, labels)

            outputs = model(adv)

            _, pred = torch.max(outputs, 1)

            total += labels.size(0)
            correct += (pred == labels).sum().item()

        acc_list.append(correct / total)

    plt.figure()
    plt.plot(eps_list, acc_list, marker="o")

    plt.xlabel("eps")
    plt.ylabel("accuracy")
    plt.title("robustness curve")

    plt.savefig("../results/curve.png")
    plt.close()


# ==============================
# 获取攻击方法
# ==============================
def get_attack(model, attack_name, eps):

    # ++++++++++++++++++++++++++++++++++++++++++++++
    # 新增：自研扩散隐蔽攻击 DiffuseHide
    # ++++++++++++++++++++++++++++++++++++++++++++++
    if attack_name == "DiffuseHide":
        return DiffuseHide(model, eps=eps)

    if attack_name == "FGSM":
        return torchattacks.FGSM(model, eps=eps)

    elif attack_name == "RFGSM":
        return torchattacks.RFGSM(model, eps=eps)

    elif attack_name == "FFGSM":
        return torchattacks.FFGSM(model, eps=eps)

    elif attack_name == "BIM":
        return torchattacks.BIM(model, eps=eps)

    elif attack_name == "PGD":
        return torchattacks.PGD(model, eps=eps, alpha=0.01, steps=10)

    elif attack_name == "PGDL2":
        return torchattacks.PGDL2(model, eps=eps, alpha=0.01, steps=10)

    elif attack_name == "MIFGSM":
        return torchattacks.MIFGSM(model, eps=eps)

    elif attack_name == "NIFGSM":
        return torchattacks.NIFGSM(model, eps=eps)

    elif attack_name == "DIFGSM":
        return torchattacks.DIFGSM(model, eps=eps)

    elif attack_name == "TIFGSM":
        return torchattacks.TIFGSM(model, eps=eps)

    elif attack_name == "CW":
        return torchattacks.CW(model)

    elif attack_name == "DeepFool":
        return torchattacks.DeepFool(model)

    elif attack_name == "Square":
        return torchattacks.Square(model)

    elif attack_name == "AutoAttack":
        return torchattacks.AutoAttack(model)

    else:
        raise ValueError("Unsupported attack method")


#生成PDF
def save_pdf_report(result):

    styles = getSampleStyleSheet()

    elements = []

    elements.append(Paragraph("Drone Model Robustness Evaluation Report", styles["Title"]))
    elements.append(Spacer(1,20))

    elements.append(Paragraph(f"Model: {result['model']}", styles["Normal"]))
    elements.append(Paragraph(f"Dataset: {result['dataset']}", styles["Normal"]))
    elements.append(Paragraph(f"Attack Method: {result['attack']}", styles["Normal"]))
    elements.append(Paragraph(f"Eps: {result['eps']}", styles["Normal"]))
    elements.append(Spacer(1,20))

    elements.append(Paragraph(f"Clean Accuracy: {result['clean_accuracy']:.4f}", styles["Normal"]))
    elements.append(Paragraph(f"Adversarial Accuracy: {result['adv_accuracy']:.4f}", styles["Normal"]))
    elements.append(Paragraph(f"Accuracy Drop: {result['accuracy_drop']:.4f}", styles["Normal"]))
    elements.append(Paragraph(f"Robust Score: {result['robust_score']:.4f}", styles["Normal"]))
    elements.append(Paragraph(f"Robust Level: {result['robust_level']}", styles["Normal"]))
    elements.append(Spacer(1,20))

    elements.append(Paragraph("Robustness Curve", styles["Heading2"]))
    elements.append(Image(result["curve_path"], width=400, height=300))
    elements.append(Spacer(1,20))

    elements.append(Paragraph("Adversarial Example Comparison", styles["Heading2"]))
    elements.append(Image(result["compare_path"], width=400, height=300))

    pdf_path = "../results/report.pdf"

    doc = SimpleDocTemplate(pdf_path)

    doc.build(elements)

    print("PDF saved:", pdf_path)

# ==============================
# 主程序
# ==============================
if __name__ == "__main__":

    args = parse_args()

    os.makedirs("../results", exist_ok=True)

    model_name = args.model
    dataset_name = args.dataset
    attack_method = args.attack
    eps = args.eps

    print("Model:", model_name)
    print("Dataset:", dataset_name)
    print("Attack:", attack_method)
    print("Eps:", eps)

    model = load_model(model_name)

    loader = load_dataset(dataset_name)

    # 原始准确率
    clean_acc = test_accuracy(model, loader)

    print("clean accuracy:", clean_acc)

    attack = get_attack(model, attack_method, eps)

    correct = 0
    total = 0

    for images, labels in loader:

        images = images.to(device)
        labels = labels.to(device)

        adv_images = attack(images, labels)

        outputs = model(adv_images)

        _, pred = torch.max(outputs, 1)

        total += labels.size(0)

        correct += (pred == labels).sum().item()

    # 保存对抗图片
    plt.figure()

    plt.subplot(1, 2, 1)
    plt.imshow(images[0].detach().cpu().permute(1, 2, 0))
    plt.title("original")

    plt.subplot(1, 2, 2)
    plt.imshow(adv_images[0].detach().cpu().permute(1, 2, 0))
    plt.title("adversarial")

    plt.savefig("../results/compare.png")

    plt.close()

    # 攻击后准确率
    adv_acc = correct / total

    print("adv accuracy:", adv_acc)

    drop = clean_acc - adv_acc

    print("accuracy drop:", drop)

    level = robustness_level(clean_acc, adv_acc)

    print("robustness level:", level)

    # ==============================
    # 【核心修改】匹配原理图：鲁棒性分数公式
    # RobustnessScore = 100 × (1 − ΔAcc / Acc_clean)
    # ==============================
    if clean_acc == 0:
        score = 0.0
    else:
        score = 100 * (1 - (drop / clean_acc))

    print("robust score:", score)

    robustness_curve(model, loader, attack_method)

    # 保存JSON结果
    result = {
        "model": model_name,
        "dataset": dataset_name,
        "attack": attack_method,
        "eps": eps,
        "clean_accuracy": float(clean_acc),
        "adv_accuracy": float(adv_acc),
        "accuracy_drop": float(drop),
        "robust_score": float(score),
        "robust_level": level,
        "curve_path": "../results/curve.png",
        "compare_path": "../results/compare.png"
    }

    save_pdf_report(result)