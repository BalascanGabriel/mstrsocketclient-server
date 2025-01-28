#!/bin/bash

# Compilare
echo "Compilare..."
javac src/server/*.java src/client/*.java

# Verifică dacă compilarea a avut succes
if [ $? -eq 0 ]; then
    echo "Compilare reușită!"
    
    # Rulează serverul în fundal
    echo "Pornire server..."
    java src.server.ReservationServer &
    
    # Așteaptă puțin să pornească serverul
    sleep 2
    
    # Rulează clientul
    echo "Pornire client..."
    java src.client.ReservationClient
else
    echo "Eroare la compilare!"
fi 