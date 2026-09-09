# SpendGuard

Sistema de gobernanza de gastos corporativos con flujos de aprobación, control de presupuesto y auditoría.

## Estado
- Backend en desarrollo (Spring Boot 3.2, Java 21).
- Esquema de BD en progreso.
- Entidades JPA creadas.
- Repositorios (puertos y adaptadores JPA) creados.
- DTOs y mapper creados.
- Frontend pendiente.

## Ejecución local
1. Configurar PostgreSQL y crear base de datos `spendguard`.
2. Ajustar credenciales en `application.yml`.
3. `cd spendguard-api && mvn spring-boot:run`

## Configuración de la base de datos

### Requisitos
- PostgreSQL 16 (o superior)
- Usuario y base de datos dedicados

### Pasos para crear la base de datos
1. Conéctate a tu servidor PostgreSQL como superusuario.
2. Ejecuta por separado:
   ```sql CREATE USER spendguard WITH PASSWORD 'spendguard';CREATE DATABASE spendguard OWNER spendguard; ```
3. Conéctate a la base `spendguard` y ejecuta el script:
   `spendguard-api/src/main/resources/db/migration/V1__initial_schema.sql`
   (o deja que Flyway lo haga automáticamente al iniciar la app).

### Nota sobre múltiples versiones de PostgreSQL
Si conviven PostgreSQL x.x (puerto 5432) y PostgreSQL 16 (puerto 5433), ajusta `application.yml`:

   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5433/spendguard
   ```

Asegúrate de que el puerto configurado coincida con la instancia de PostgreSQL donde se encuentra la base de datos `spendguard`.


## Autor
Jar Gonzales – [GitHub](https://github.com/jarjargonzales)
