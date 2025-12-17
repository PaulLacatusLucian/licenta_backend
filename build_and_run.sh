#!/bin/bash
set -e  # oprește scriptul dacă apare vreo eroare

# Curățare și construire proiect Maven (skip testele)
mvn clean package -Dmaven.test.skip=true

# Crearea imaginii Docker
docker build -t cafeteria-plugin .

# Oprire și ștergere container vechi dacă există
if [ $(docker ps -aq -f name=cafeteria-container) ]; then
    docker rm -f cafeteria-container
fi

# Pornirea containerului Docker
docker run -d -p 127.0.0.1:8080:8080 --name cafeteria-container cafeteria-plugin

# Confirmare rulare container
docker ps | grep cafeteria-container
docker logs cafeteria-container
