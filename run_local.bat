@echo off
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
