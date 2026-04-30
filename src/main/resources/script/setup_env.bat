@echo off
chcp 936 > nul

echo === 开始配置 Python 虚拟环境 ===

if not exist "python_env" (
    echo [1/3] 正在创建虚拟环境...
    python -m venv python_env
) else (
    echo [1/3] 虚拟环境已存在，跳过创建。
)

echo [2/3] 正在激活虚拟环境...
call python_env\Scripts\activate.bat


echo [3/3] 正在升级 pip 并安装依赖库...
python -m pip install --upgrade pip
pip install -r requirements.txt

echo === 环境配置完成！ ===
pause