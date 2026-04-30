import torch
import torchvision.models as models
import torchvision.transforms as transforms
from torchvision.models import resnet18, ResNet18_Weights
from PIL import Image
import numpy as np
from art.estimators.classification import PyTorchClassifier
from art.defences.preprocessor import FeatureSqueezing

# 解决OMP错误
import os
os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"

def detect_adversarial_risk(image_path):

    # 1️⃣ 加载模型
    model = resnet18(weights=ResNet18_Weights.DEFAULT)
    model.eval()

    # 2️⃣ 图像预处理
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor()
    ])

    image = Image.open(image_path).convert("RGB")
    input_tensor = transform(image).unsqueeze(0)

    # 【修复1】强制转 float32
    input_numpy = input_tensor.numpy().astype(np.float32)

    # 3️⃣ 创建ART分类器（去掉了dtype，兼容所有版本）
    classifier = PyTorchClassifier(
        model=model,
        loss=torch.nn.CrossEntropyLoss(),
        optimizer=torch.optim.Adam(model.parameters(), lr=0.01),
        input_shape=(3, 224, 224),
        nb_classes=1000,
        device_type="cpu"
    )

    # 4️⃣ 原始预测
    preds = classifier.predict(input_numpy)
    original_label = np.argmax(preds)

    # 5️⃣ Feature Squeezing 检测
    fs = FeatureSqueezing(clip_values=(0, 1), bit_depth=5)
    squeezed_input, _ = fs(input_numpy)

    # 【修复2】强制转 float32（最关键！）
    squeezed_input = squeezed_input.astype(np.float32)

    squeezed_preds = classifier.predict(squeezed_input)
    squeezed_label = np.argmax(squeezed_preds)

    # 6️⃣ 计算风险评分
    diff = np.linalg.norm(preds - squeezed_preds)
    risk_score = float(diff)

    # 是否为对抗样本
    is_adversarial = original_label != squeezed_label

    # 风险等级
    if risk_score > 5:
        risk_level = "High"
    elif risk_score > 2:
        risk_level = "Medium"
    else:
        risk_level = "Low"

    return {
        "risk_score": risk_score,
        "risk_level": risk_level,
        "is_adversarial": bool(is_adversarial),
        "original_label": int(original_label),
        "squeezed_label": int(squeezed_label),
        "method": "Feature Squeezing"
    }