@echo off
echo Stergere director bin existent...
rmdir /s /q bin 2>nul

echo Creare director pentru clase compilate...
mkdir bin

echo Compilare...
javac -d bin src/server/*.java src/client/*.java

IF %ERRORLEVEL% EQU 0 (
    echo Compilare reusita!
    
    echo Pornire server...
    start "Restaurant Server" java -cp bin server.ReservationServer
    
    echo Asteptare pornire server...
    timeout /t 2 /nobreak
    
    echo Pornire client GUI...
    java -cp bin client.ReservationClientGUI
) ELSE (
    echo Eroare la compilare!
) 