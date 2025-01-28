@echo off
if not exist "bin" (
    echo Eroare: Serverul nu a fost pornit! Rulați mai întâi start_admin.bat
    pause
    exit /b
)

echo Pornire client...
java -cp bin client.ReservationClientGUI 