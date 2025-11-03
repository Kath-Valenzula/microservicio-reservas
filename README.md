# Microservicio de Reservas (DSY2205)

Microservicio REST con **Spring Boot 3.5.7** y **Java 21**. CRUD de reservas de laboratorios (fecha, horaInicio, horaFin, idLab, idUsuario) persistido en **Oracle Autonomous Database** mediante **Wallet TNS**.

## Contexto
Asignatura **DSY2205 - Desarrollo de Microservicios**. Cubre: uso de Git, CRUD completo con Oracle, controladores REST y consultas a tablas reales.

## Tecnologías
- Java 21
- Spring Boot 3.5.7
- Spring Web
- Spring Data JPA
- HikariCP
- Oracle JDBC (ojdbc11) + oraclepki
- Maven

## Configuración
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
````

## Build y ejecución

```powershell
mvn clean package -DskipTests
java "-Doracle.net.tns_admin=C:\microservicios\Wallet_BDDSY2205" -jar target\microservicio-reservas-0.0.1-SNAPSHOT.jar
```

## Endpoints

Base URL: `http://localhost:8083/api/reservas`

* `GET /api/reservas`
* `GET /api/reservas/{id}`
* `POST /api/reservas`
* `PUT /api/reservas/{id}`
* `DELETE /api/reservas/{id}`

## Pruebas rápidas (PowerShell cURL)

```powershell
curl http://localhost:8083/api/reservas
curl http://localhost:8083/api/reservas/1
curl -X POST "http://localhost:8083/api/reservas" ^
  -H "Content-Type: application/json" ^
  -d "{`"fecha`":`"2025-11-06`",`"horaInicio`":`"09:00`",`"horaFin`":`"10:00`",`"idLab`":1,`"idUsuario`":2}"
curl -X PUT "http://localhost:8083/api/reservas/1" ^
  -H "Content-Type: application/json" ^
  -d "{`"fecha`":`"2025-11-07`",`"horaInicio`":`"10:30`",`"horaFin`":`"12:00`",`"idLab`":2,`"idUsuario`":3}"
curl -X DELETE "http://localhost:8083/api/reservas/1"
```
