import os
os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"
from fastapi import FastAPI, UploadFile
import shutil
import uuid
from risk_detector import detect_adversarial_risk

app = FastAPI()
@app.post("/detect")
async def detect(file: UploadFile):

    # 保存临时文件
    temp_file = f"temp_{uuid.uuid4()}.jpg"
    
    with open(temp_file, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    # 调用检测工具
    result = detect_adversarial_risk(temp_file)

    return result
