@echo off
cd /d "%~dp0"

if not exist "lib\openpdf-1.3.39.jar" (
    echo Thieu thu vien openpdf-1.3.39.jar trong thu muc lib.
    echo Vui long kiem tra lai de chuc nang xuat PDF hoat dong.
)

echo Đang biên dịch mã nguồn Java...
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "lib/*" -sourcepath src -d bin @sources.txt
if %errorlevel% neq 0 (
    echo Lỗi trong quá trình biên dịch! Vui lòng kiểm tra lại mã nguồn.
    pause
    exit /b %errorlevel%
)

echo Đang khởi chạy ứng dụng...
java -cp "bin;lib/*" GUI.GUI
pause
