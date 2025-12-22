# Microservicio de Reservas (DSY2205)

CRUD de reservas de laboratorios sobre Oracle Autonomous Database. Desarrollado con Spring Boot y Java 21, generado desde Spring Initializr y estructurado en capas (controller, service, repository, entity, DTO).
Arquetipo documentado en `../docs/arquetipo-backend.md`.

## Git (trabajo personal)
- Rama principal `main` con commits frecuentes.

## Tecnologias
- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- HikariCP
- Oracle JDBC (ojdbc11) + oraclepki
- Maven

## Configuracion
Archivo: `src/main/resources/application.properties`
```properties
server.port=8083
spring.datasource.url=jdbc:oracle:thin:@bddsy2205_high?TNS_ADMIN=C:/microservicios/Wallet_BDDSY2205
spring.datasource.username=ADMIN
spring.datasource.password=********
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.hikari.pool-name=HikariPool-Reservas
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.idle-timeout=30000
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
spring.jackson.serialization.indent-output=true
```

## Build y ejecucion
```powershell
mvn clean package -DskipTests
java "-Doracle.net.tns_admin=C:\microservicios\Wallet_BDDSY2205" -jar target\microservicio-reservas-0.0.1-SNAPSHOT.jar
```

## Endpoints
Base URL: `http://localhost:8083/api/reservas`
- `GET /api/reservas`
- `GET /api/reservas/{id}`
- `POST /api/reservas`
- `PUT /api/reservas/{id}`
- `DELETE /api/reservas/{id}`
