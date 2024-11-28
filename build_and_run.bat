:: Curățare și construire proiect Maven
mvn clean package
IF %ERRORLEVEL% NEQ 0 (
    exit /b 1
)

:: Crearea imaginii Docker
docker build -t cafeteria-plugin .
IF %ERRORLEVEL% NEQ 0 (
    exit /b 1
)

:: Pornirea containerului Docker
docker run -d -p 8080:8080 --name cafeteria-container cafeteria-plugin
IF %ERRORLEVEL% NEQ 0 (
    exit /b 1
)

:: Confirmare rulare container
docker ps | findstr cafeteria-container
docker logs cafeteria-container
