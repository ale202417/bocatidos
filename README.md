# Bocaditos LLC

Spring Boot internal operations app for Bocaditos LLC.

## Stack

- Java 21
- Spring Boot
- Thymeleaf
- Spring Data JPA
- MySQL
- Maven

## Local Run

1. Create the database:

```sql
CREATE DATABASE bocaditos_llc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Set environment variables in PowerShell:

```powershell
$env:BOCADITOS_DB_URL="jdbc:mysql://localhost:3306/bocaditos_llc"
$env:BOCADITOS_DB_USERNAME="root"
$env:BOCADITOS_DB_PASSWORD="your_password"
```

3. Start the app:

```powershell
mvn spring-boot:run
```

4. Open:

- http://localhost:8080

## Health Check

- `GET /actuator/health`

## Deployment Notes

The app is environment-variable driven and can be deployed as a jar or container.

Required environment variables:

- `BOCADITOS_DB_URL`
- `BOCADITOS_DB_USERNAME`
- `BOCADITOS_DB_PASSWORD`

## Important Note

This project does not seed fake business records. Only basic reference categories are initialized when missing.
