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
import torch.nn as nn

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


# =========================
# 解析命令行参数
# =========================
def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--model_path", required=True)
    parser.add_argument("--dataset", required=True)
    parser.add_argument("--attack", default="FGSM")
    parser.add_argument("--eps", type=float, default=0.03)
    return parser.parse_args()


# =========================
# 【自研算法】DiffuseHide – 高隐蔽性渐进攻击
# =========================
class DiffuseHide:
    def __init__(self, model, eps=0.03, steps=3):
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
            adv = adv + self.alpha * grad.sign()
            adv = torch.clamp(adv, images - self.eps, images + self.eps)
            adv = torch.clamp(adv, 0, 1).detach()
            adv.requires_grad = True

        return adv


# =========================
# 加载自定义模型
# =========================
def load_user_model(path):
    model = torchvision.models.resnet18()
    model.fc = torch.nn.Linear(model.fc.in_features, 4)
    model.load_state_dict(torch.load(path, map_location=device))
    model.to(device)
    model.eval()
    return model


# =========================
# 加载数据集
# =========================
def load_dataset(name):
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor()
    ])
    dataset = torchvision.datasets.ImageFolder(
        root=f"../datasets/{name}",
        transform=transform
    )
    loader = torch.utils.data.DataLoader(
        dataset, batch_size=32, shuffle=False
    )
    return loader, len(dataset)


# =========================
# 获取攻击方法（含自研算法）
# =========================
def get_attack(model, name, eps):
    if name == "FGSM":
        return torchattacks.FGSM(model, eps)
    if name == "PGD":
        return torchattacks.PGD(model, eps)
    if name == "BIM":
        return torchattacks.BIM(model, eps)
    if name == "MIFGSM":
        return torchattacks.MIFGSM(model, eps)
    if name == "DeepFool":
        return torchattacks.DeepFool(model)
    if name == "DiffuseHide":
        return DiffuseHide(model, eps)
    raise ValueError("unsupported attack")


# =========================
# 干净样本准确率
# =========================
def test_clean(model, loader):
    correct = 0
    total = 0
    for images, labels in loader:
        images, labels = images.to(device), labels.to(device)
        with torch.no_grad():
            outputs = model(images)
        pred = outputs.max(1)[1]
        total += labels.size(0)
        correct += pred.eq(labels).sum().item()
    return correct / total


# =========================
# 攻击测试
# =========================
def test_attack(model, loader, attack):
    correct = 0
    total = 0
    ori_sample = None
    adv_sample = None
    start = time.time()

    for images, labels in loader:
        images, labels = images.to(device), labels.to(device)
        adv = attack(images, labels)
        outputs = model(adv)
        pred = outputs.max(1)[1]
        total += labels.size(0)
        correct += pred.eq(labels).sum().item()

        if ori_sample is None:
            ori_sample = images[:8].detach().cpu()
            adv_sample = adv[:8].detach().cpu()

    attack_time = time.time() - start
    return correct, total, ori_sample, adv_sample, attack_time


# =========================
# 保存对比图
# =========================
def save_compare(ori, adv):
    plt.figure(figsize=(8, 4))
    plt.subplot(1, 2, 1)
    plt.imshow(ori[0].permute(1, 2, 0))
    plt.title("Original")
    plt.subplot(1, 2, 2)
    plt.imshow(adv[0].permute(1, 2, 0))
    plt.title("Adversarial")
    plt.tight_layout()
    plt.savefig("../results/compare.png")
    plt.close()


# =========================
# 鲁棒性曲线
# =========================
def robustness_curve(model, loader, attack_name):
    eps_list = [0.01, 0.02, 0.03, 0.05]
    acc_list = []
    for eps in eps_list:
        attack = get_attack(model, attack_name, eps)
        c, t, _, _, _ = test_attack(model, loader, attack)
        acc_list.append(c / t)

    plt.figure()
    plt.plot(eps_list, acc_list, marker='o')
    plt.xlabel("Eps")
    plt.ylabel("Accuracy")
    plt.title("Robustness Curve")
    plt.savefig("../results/robustness_curve.png")
    plt.close()


# =========================
# 生成攻击样本图集
# =========================
def save_adv_gallery(adv, attack_name):
    path = "../results/adversarial_samples"
    os.makedirs(path, exist_ok=True)
    grid = vutils.make_grid(adv, nrow=4, normalize=True)
    plt.imsave(f"{path}/{attack_name}_samples.png", grid.permute(1, 2, 0).numpy())


# =========================
# 【官方新版评测原理】
# RobustnessScore = 100 * (1 - ΔAcc / CleanAcc)
# =========================
def calculate_robust_score(clean_acc, adv_acc):
    if clean_acc == 0:
        return 0.0, "Poor"
    delta = clean_acc - adv_acc
    score = 100 * (1 - delta / clean_acc)

    if score > 90:
        level = "Strong"
    elif score > 70:
        level = "Medium"
    elif score > 50:
        level = "Weak"
    else:
        level = "Poor"
    return score, level


# =========================
# 生成HTML完整报告
# =========================
def generate_html_report(report):
    html = f"""
    <html>
    <head>
        <meta charset="utf-8">
        <title>模型鲁棒性评估报告</title>
    </head>
    <body>
        <h1>模型鲁棒性评估报告</h1>
        <h3>模型路径：{report['model_path']}</h3>
        <h3>数据集：{report['dataset']}（共 {report['dataset_size']} 张）</h3>
        <h3>攻击方法：{report['attack']} | eps={report['eps']}</h3>
        <hr>
        <p><b>原始准确率：</b>{report['clean_accuracy']:.4f}</p>
        <p><b>攻击后准确率：</b>{report['adv_accuracy']:.4f}</p>
        <p><b>准确率下降：</b>{report['accuracy_drop']:.4f}</p>
        <p><b>攻击成功率：</b>{report['attack_success_rate']:.4f}</p>
        <p><b>鲁棒得分：</b>{report['robust_score']:.2f}</p>
        <p><b>鲁棒等级：</b>{report['robust_level']}</p>
        <hr>
        <h3>样本对比</h3>
        <img src="compare.png" width="700">
        <h3>鲁棒性曲线</h3>
        <img src="robustness_curve.png" width="700">
    </body>
    </html>
    """
    with open("../results/report.html", "w", encoding="utf-8") as f:
        f.write(html)


# =========================
# 主程序
# =========================
if __name__ == "__main__":
    args = parse_args()
    os.makedirs("../results", exist_ok=True)

    # 加载模型 & 数据
    model = load_user_model(args.model_path)
    loader, size = load_dataset(args.dataset)

    # 测试干净准确率
    clean_acc = test_clean(model, loader)

    # 构建攻击 & 测试
    attack = get_attack(model, args.attack, args.eps)
    correct, total, ori, adv, attack_time = test_attack(model, loader, attack)
    adv_acc = correct / total
    drop = clean_acc - adv_acc
    success_rate = 1 - adv_acc

    # 计算官方鲁棒评分
    robust_score, robust_level = calculate_robust_score(clean_acc, adv_acc)

    # 保存图表
    save_compare(ori, adv)
    save_adv_gallery(adv, args.attack)
    robustness_curve(model, loader, args.attack)

    # 输出完整报告（与 evaluate(one).py 完全一致）
    report = {
        "model_path": args.model_path,
        "dataset": args.dataset,
        "dataset_size": size,
        "attack": args.attack,
        "eps": args.eps,
        "clean_accuracy": clean_acc,
        "adv_accuracy": adv_acc,
        "accuracy_drop": drop,
        "attack_success_rate": success_rate,
        "attack_time": attack_time,
        "robust_score": robust_score,
        "robust_level": robust_level
    }

    # 保存JSON
    with open("../results/evaluation_report.json", "w", encoding="utf-8") as f:
        json.dump(report, f, indent=4, ensure_ascii=False)

    # 生成HTML可视化报告
    generate_html_report(report)

    # 打印输出
    print(json.dumps(report, indent=4, ensure_ascii=False))