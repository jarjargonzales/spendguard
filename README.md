# SpendGuard

Sistema de gobernanza de gastos corporativos con flujos de aprobación, control de presupuesto y auditoría.

## Estado
**Backend completado** (Spring Boot 3.2, Java 21).

- Esquema de BD con Flyway (8 tablas, secuencias, índices).
- Entidades JPA con optimistic locking y lifecycle callbacks.
- Repositorios (puertos y adaptadores JPA).
- DTOs y mappers (MapStruct).
- Servicios de negocio: ExpenseRequest, PolicyEngine, Approval, Budget, Audit, Notification, Category, Department.
- Seguridad JWT (token provider con departmentId, filtro, user details service, roles).
- Controladores REST: Auth, ExpenseRequest, Approval, Budget, Audit, Category, Department.
- Scheduler de recordatorios (48h) y escalación automática (72h).
- Tests unitarios y de integración: 9 pasando.
- Documentación OpenAPI/Swagger.

**Frontend Angular en desarrollo.**

- Login con JWT y guards por rol.
- Interceptor JWT.
- Navbar + layout.
- Listado de solicitudes + formulario de creación con listas dinámicas.
- Panel de administración (categorías y departamentos).
- Vistas placeholder para aprobaciones y reportes.

## Arquitectura

Arquitectura hexagonal en el backend, con separación por capas:

```
com.spendguard
├── domain/          # Entidades, enums, excepciones
├── application/     # DTOs, mappers, servicios, puertos
├── infrastructure/  # Persistencia JPA, seguridad JWT, scheduler
└── web/             # Controladores REST
```

Frontend Angular con standalone components y Angular Material:

```
spendguard-web/src/app
├── core/            # Guards, interceptores, modelos, servicios HTTP
├── features/        # auth, dashboard, expense-requests, approvals, reports, admin
└── shared/          # Navbar, layout, componentes reutilizables
```

## Ejecución local

### Backend

1. Configurar PostgreSQL y crear base de datos `spendguard`.
2. Ajustar credenciales en `application.yml`.
3. `cd spendguard-api && mvn spring-boot:run`

### Frontend

```bash
cd spendguard-web
npm install --legacy-peer-deps
ng serve
```

Abrir `http://localhost:4200`.

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
   (o deja que Flyway lo haga automáticamente al iniciar la aplicación).

### Múltiples versiones de PostgreSQL
Si conviven PostgreSQL 9.3 (puerto 5432) y PostgreSQL 16 (puerto 5433), ajusta `application.yml`:

   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5433/spendguard
   ```

Asegúrate de que el puerto configurado coincida con la instancia de PostgreSQL donde se encuentra la base de datos `spendguard`.

## Tests

```bash
cd spendguard-api
mvn test
```

- 6 tests unitarios (ExpenseRequestService, PolicyEngineService, BudgetService).
- 2 tests de integración (ApprovalFlow, ExpenseRequest) con H2.


## Documentación de la API
Una vez arrancada la aplicación:

- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- Actuator: `http://localhost:8080/api/actuator`
- Health: `http://localhost:8080/api/actuator/health`

## Stack

**Backend**
- Java 21, Spring Boot 3.2
- Spring Security + JWT (JJWT 0.12)
- Spring Data JPA + Hibernate
- PostgreSQL 16, Flyway
- MapStruct, Lombok
- JUnit 5, Mockito, H2
- SpringDoc OpenAPI

**Frontend**
- Angular 20 (standalone components)
- Angular Material
- RxJS
- TypeScript

## Autor
Jar Gonzales – [Linkedin](https://www.linkedin.com/in/jarjargonzales/)
