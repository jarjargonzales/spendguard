# SpendGuard

> Expense Governance & Approval Engine — Sistema de control de gastos corporativos con flujos de aprobación jerárquicos, políticas configurables y auditoría completa.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Angular](https://img.shields.io/badge/Angular-17-red)](https://angular.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## Problema real

Toda empresa con mas de 10 empleados enfrenta el mismo caos: **no saben quien aprobo que gasto, cuando, ni por que**. Las facturas se pagan sin revision, los limites de presupuesto se violan en silencio, y cuando llega la auditoria nadie tiene respuestas.

El 43% de las PYMES latinoamericanas pierden control de sus gastos operativos en los primeros 18 meses. No es que no ganen dinero — es que no saben en que se va.

## Que resuelve SpendGuard

SpendGuard no es un simple CRUD de gastos. Es un **motor de gobernanza** que automatiza el control financiero corporativo:

- **Politicas configurables** — Cada departamento tiene sus propias reglas de aprobacion
- **Flujos de aprobacion dinamicos** — Maquina de estados que escala automaticamente al aprobador correcto
- **Auditoria inmutable** — Cada decision queda registrada con timestamp, IP y motivo
- **Control de presupuesto en tiempo real** — Alertas al 80%, bloqueo al 100%
- **Seguridad a nivel de fila** — Cada usuario solo ve lo que le corresponde

## Arquitectura

```
SpendGuard
├── spendguard-api/          # Spring Boot 3 + Spring Security + JPA
│   ├── src/main/java/...
│   │   ├── domain/          # Entidades, enums, excepciones
│   │   ├── application/     # Servicios, DTOs, mappers
│   │   ├── infrastructure/  # Repositorios, config, seguridad
│   │   └── web/             # Controladores REST
│   └── src/test/java/...    # Unit + Integration tests (TestContainers)
├── spendguard-web/          # Angular 17 + Signals + Standalone Components
│   └── src/app/
│       ├── core/            # Guards, interceptores, servicios base
│       ├── features/        # Modulos por feature (requests, approvals, reports)
│       └── shared/          # Componentes reutilizables
└── docker/                  # Docker Compose (PostgreSQL + API + Web)
```

## Stack tecnico

| Capa | Tecnologia |
|------|------------|
| Backend | Spring Boot 3.2, Spring Security (JWT + RBAC), Spring Data JPA, Spring Scheduler |
| Base de datos | PostgreSQL 16 (indices compuestos, constraints, triggers de auditoria) |
| Frontend | Angular 17+, Signals, Standalone Components, Guards de ruta por rol |
| Testing | JUnit 5, Mockito, TestContainers (PostgreSQL real en tests de integracion) |
| DevOps | Docker, Docker Compose, GitHub Actions (CI/CD) |

## Flujo de aprobacion

```
Empleado solicita gasto
        |
        v
Motor de reglas evalua politica (monto + categoria + departamento + presupuesto)
        |
        v
Asigna aprobador(es) segun jerarquia configurada
        |
        v
Aprobacion jerarquica (secuencial o paralela segun reglas)
        |
        v
Auditoria automatica + actualizacion de presupuesto
```

### Ejemplo concreto

> Juan (desarrollador junior) solicita $1,500 para un curso. La politica dice: cursos >$1,000 requieren aprobacion del Tech Lead + el CTO. Si el Tech Lead rechaza, el flujo se detiene. Si aprueba, pasa al CTO. Todo queda registrado en `audit_log` con timestamp, IP y motivo.

## Features tecnicas clave

- **Maquina de estados** — 7 estados controlados: `draft -> submitted -> under_review -> approved -> rejected -> paid -> archived`. Transiciones validadas, no libres.
- **Row-Level Security** — Jefe ve solicitudes de su equipo. Empleado solo ve las suyas. CFO ve todo. Implementado con `@PreAuthorize` + filtros de query.
- **Scheduled Jobs** — Recordatorios a aprobadores sin respuesta en 48h. Escalacion automatica al superior si pasan 72h.
- **Transacciones ACID** — Al aprobar un gasto: genera registro contable, actualiza presupuesto del departamento, notifica a finance — todo atomico.
- **Optimistic Locking** — `@Version` en `expense_requests` para prevenir race conditions cuando dos aprobadores deciden simultaneamente.
- **Auditoria inmutable** — Tabla `audit_log` con INSERT-only. Nunca se actualiza ni elimina. Cumple principios de compliance.

## Reglas de politica (ejemplos)

| Rol | Limite sin aprobacion | Aprobador por defecto |
|-----|----------------------|----------------------|
| Junior | $500 | Tech Lead |
| Senior | $2,000 | Manager |
| Manager | $5,000 | Director |
| Director | $10,000 | CFO |

**Reglas por categoria:**
- Viajes: siempre requieren aprobacion, sin importe
- Software: aprobacion de IT si >$300
- Capacitacion: doble firma si >$1,000

**Reglas por departamento:**
- Presupuesto mensual asignado
- Alerta al 80% de consumo
- Bloqueo automatico al 100%

## Esquema de base de datos

Ver [`database/schema.sql`](database/schema.sql) para el esquema completo con:
- 7 tablas principales con relaciones y constraints
- Triggers de auditoria automatica
- Indices optimizados para consultas frecuentes
- Datos de seed para demostracion

## Estado del proyecto

| Modulo | Estado |
|--------|--------|
| Esquema de base de datos | Completado |
| API REST + autenticacion JWT | En desarrollo |
| Motor de reglas de aprobacion | En desarrollo |
| Maquina de estados | En desarrollo |
| Frontend Angular | Pendiente |
| Tests de integracion | Pendiente |
| Docker + CI/CD | Pendiente |

## Como ejecutar (pronto)

```bash
# Clonar repositorio
git clone https://github.com/jarjargonzales/spendguard.git
cd spendguard

# Levantar con Docker Compose
docker-compose up -d

# API: http://localhost:8080
# Web: http://localhost:4200
```

## Autor

**Jar Gonzales** — Java Backend Developer | Spring Boot | PostgreSQL | Angular

- GitHub: [@jarjargonzales](https://github.com/jarjargonzales)
- LinkedIn: [jar-gonzales](https://linkedin.com/in/jar-gonzales)
- Ubicacion: Huacho, Peru

## Licencia

MIT License — ver [LICENSE](LICENSE) para detalles.
