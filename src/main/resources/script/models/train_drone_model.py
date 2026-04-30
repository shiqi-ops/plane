import torch
import torchvision
import torchvision.transforms as transforms
import torch.nn as nn
import torch.optim as optim

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

transform = transforms.Compose([
    transforms.Resize((224,224)),
    transforms.ToTensor()
])

dataset = torchvision.datasets.ImageFolder(
    root="../datasets/drone_dataset",
    transform=transform
)

loader = torch.utils.data.DataLoader(
    dataset,
    batch_size=32,
    shuffle=True
)

model = torchvision.models.efficientnet_b0(pretrained=True)

model.classifier[1] = nn.Linear(model.classifier[1].in_features, len(dataset.classes))

model = model.to(device)

criterion = nn.CrossEntropyLoss()

optimizer = optim.Adam(model.parameters(), lr=0.001)

for epoch in range(5):

    for images, labels in loader:

        images = images.to(device)
        labels = labels.to(device)

        outputs = model(images)

        loss = criterion(outputs, labels)

        optimizer.zero_grad()
        loss.backward()
        optimizer.step()

    print("epoch:",epoch,"loss:",loss.item())

torch.save(model.state_dict(),"efficientnet_drone.pth")

print("model saved")