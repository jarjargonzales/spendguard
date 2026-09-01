-- ============================================================
-- SpendGuard — Expense Governance Engine
-- Esquema de base de datos completo
-- PostgreSQL 16+
-- ============================================================

-- --------------------------------------------------------------
-- 1. ENUMS
-- --------------------------------------------------------------

CREATE TYPE user_role AS ENUM (
    'EMPLOYEE',
    'MANAGER',
    'DIRECTOR',
    'CFO',
    'ADMIN'
);

CREATE TYPE request_status AS ENUM (
    'DRAFT',
    'SUBMITTED',
    'UNDER_REVIEW',
    'APPROVED',
    'REJECTED',
    'PAID',
    'ARCHIVED'
);

CREATE TYPE approval_action AS ENUM (
    'APPROVED',
    'REJECTED',
    'ESCALATED'
);

CREATE TYPE audit_event_type AS ENUM (
    'REQUEST_CREATED',
    'REQUEST_SUBMITTED',
    'APPROVAL_GRANTED',
    'APPROVAL_REJECTED',
    'STATUS_CHANGED',
    'BUDGET_UPDATED',
    'POLICY_VIOLATION'
);

-- --------------------------------------------------------------
-- 2. TABLAS PRINCIPALES
-- --------------------------------------------------------------

-- Tabla: departments
CREATE TABLE departments (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(255),
    annual_budget   DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    monthly_budget  DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_annual_budget_positive CHECK (annual_budget >= 0),
    CONSTRAINT chk_monthly_budget_positive CHECK (monthly_budget >= 0)
);

CREATE UNIQUE INDEX idx_departments_name ON departments(name);

-- Tabla: users
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    role            user_role NOT NULL DEFAULT 'EMPLOYEE',
    department_id   BIGINT REFERENCES departments(id) ON DELETE SET NULL,
    manager_id      BIGINT REFERENCES users(id) ON DELETE SET NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_department ON users(department_id);
CREATE INDEX idx_users_manager ON users(manager_id);
CREATE INDEX idx_users_role ON users(role);

-- Tabla: expense_categories
CREATE TABLE expense_categories (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    description     VARCHAR(255),
    requires_approval BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: expense_policies
CREATE TABLE expense_policies (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(200) NOT NULL,
    department_id       BIGINT REFERENCES departments(id) ON DELETE CASCADE,
    category_id         BIGINT REFERENCES expense_categories(id) ON DELETE CASCADE,
    max_amount_no_approval DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    approver_role       user_role NOT NULL,
    requires_dual_approval BOOLEAN NOT NULL DEFAULT FALSE,
    dual_approval_threshold DECIMAL(15, 2),
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    priority            INTEGER NOT NULL DEFAULT 0,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_max_amount_positive CHECK (max_amount_no_approval >= 0),
    CONSTRAINT chk_dual_threshold CHECK (
        (requires_dual_approval = FALSE AND dual_approval_threshold IS NULL) OR
        (requires_dual_approval = TRUE AND dual_approval_threshold > 0)
    )
);

CREATE INDEX idx_policies_department ON expense_policies(department_id);
CREATE INDEX idx_policies_category ON expense_policies(category_id);
CREATE INDEX idx_policies_active ON expense_policies(is_active);

-- Tabla: expense_requests (con optimistic locking)
CREATE TABLE expense_requests (
    id              BIGSERIAL PRIMARY KEY,
    request_number  VARCHAR(50) NOT NULL UNIQUE,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    department_id   BIGINT NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    category_id     BIGINT NOT NULL REFERENCES expense_categories(id) ON DELETE CASCADE,
    amount          DECIMAL(15, 2) NOT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'PEN',
    description     TEXT NOT NULL,
    justification   TEXT,
    status          request_status NOT NULL DEFAULT 'DRAFT',
    current_approver_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    version         INTEGER NOT NULL DEFAULT 0,
    submitted_at    TIMESTAMP WITH TIME ZONE,
    resolved_at     TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_version_positive CHECK (version >= 0)
);

CREATE INDEX idx_requests_user ON expense_requests(user_id);
CREATE INDEX idx_requests_department ON expense_requests(department_id);
CREATE INDEX idx_requests_status ON expense_requests(status);
CREATE INDEX idx_requests_submitted ON expense_requests(submitted_at);
CREATE INDEX idx_requests_approver ON expense_requests(current_approver_id);

-- Tabla: approvals
CREATE TABLE approvals (
    id              BIGSERIAL PRIMARY KEY,
    request_id      BIGINT NOT NULL REFERENCES expense_requests(id) ON DELETE CASCADE,
    approver_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    action          approval_action NOT NULL,
    comments        TEXT,
    ip_address      INET,
    user_agent      VARCHAR(500),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_approval_per_user_request UNIQUE (request_id, approver_id)
);

CREATE INDEX idx_approvals_request ON approvals(request_id);
CREATE INDEX idx_approvals_approver ON approvals(approver_id);
CREATE INDEX idx_approvals_action ON approvals(action);

-- Tabla: audit_log (INSERT-ONLY, inmutable)
CREATE TABLE audit_log (
    id              BIGSERIAL PRIMARY KEY,
    request_id      BIGINT REFERENCES expense_requests(id) ON DELETE SET NULL,
    user_id         BIGINT REFERENCES users(id) ON DELETE SET NULL,
    event_type      audit_event_type NOT NULL,
    old_value       JSONB,
    new_value       JSONB,
    description     TEXT,
    ip_address      INET,
    user_agent      VARCHAR(500),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_request ON audit_log(request_id);
CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_event ON audit_log(event_type);
CREATE INDEX idx_audit_created ON audit_log(created_at);

-- Tabla: budget_consumption (tracking en tiempo real)
CREATE TABLE budget_consumption (
    id              BIGSERIAL PRIMARY KEY,
    department_id   BIGINT NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    year            INTEGER NOT NULL,
    month           INTEGER NOT NULL,
    budget_allocated DECIMAL(15, 2) NOT NULL,
    budget_consumed  DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    budget_remaining DECIMAL(15, 2) GENERATED ALWAYS AS (budget_allocated - budget_consumed) STORED,
    alert_sent_80   BOOLEAN NOT NULL DEFAULT FALSE,
    alert_sent_100  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_year_range CHECK (year >= 2020 AND year <= 2100),
    CONSTRAINT chk_month_range CHECK (month >= 1 AND month <= 12),
    CONSTRAINT unique_budget_period UNIQUE (department_id, year, month)
);

CREATE INDEX idx_budget_dept ON budget_consumption(department_id);
CREATE INDEX idx_budget_period ON budget_consumption(year, month);

-- --------------------------------------------------------------
-- 3. TRIGGERS DE AUDITORIA AUTOMATICA
-- --------------------------------------------------------------

CREATE OR REPLACE FUNCTION fn_audit_expense_request_change()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO audit_log (request_id, user_id, event_type, old_value, new_value, description, created_at)
    VALUES (
        NEW.id,
        NEW.user_id,
        'STATUS_CHANGED',
        jsonb_build_object('status', OLD.status, 'version', OLD.version),
        jsonb_build_object('status', NEW.status, 'version', NEW.version),
        'Cambio de estado de solicitud de gasto',
        CURRENT_TIMESTAMP
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_audit_request_status_change
    AFTER UPDATE OF status ON expense_requests
    FOR EACH ROW
    WHEN (OLD.status IS DISTINCT FROM NEW.status)
    EXECUTE FUNCTION fn_audit_expense_request_change();

CREATE OR REPLACE FUNCTION fn_update_budget_on_approval()
RETURNS TRIGGER AS $$
DECLARE
    v_department_id BIGINT;
    v_amount DECIMAL(15,2);
    v_year INTEGER;
    v_month INTEGER;
BEGIN
    IF NEW.status = 'APPROVED' AND OLD.status != 'APPROVED' THEN
        SELECT department_id, amount INTO v_department_id, v_amount
        FROM expense_requests WHERE id = NEW.id;

        v_year := EXTRACT(YEAR FROM CURRENT_DATE);
        v_month := EXTRACT(MONTH FROM CURRENT_DATE);

        UPDATE budget_consumption
        SET budget_consumed = budget_consumed + v_amount,
            updated_at = CURRENT_TIMESTAMP
        WHERE department_id = v_department_id
          AND year = v_year
          AND month = v_month;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_budget_on_approval
    AFTER UPDATE ON expense_requests
    FOR EACH ROW
    WHEN (NEW.status = 'APPROVED' AND OLD.status IS DISTINCT FROM NEW.status)
    EXECUTE FUNCTION fn_update_budget_on_approval();

-- --------------------------------------------------------------
-- 4. FUNCIONES AUXILIARES
-- --------------------------------------------------------------

CREATE OR REPLACE FUNCTION fn_generate_request_number()
RETURNS VARCHAR(50) AS $$
DECLARE
    v_prefix VARCHAR(10) := 'EXP-';
    v_year VARCHAR(4);
    v_sequence BIGINT;
    v_result VARCHAR(50);
BEGIN
    v_year := EXTRACT(YEAR FROM CURRENT_DATE)::TEXT;
    SELECT nextval('expense_requests_id_seq') INTO v_sequence;
    v_result := v_prefix || v_year || '-' || LPAD(v_sequence::TEXT, 6, '0');
    RETURN v_result;
END;
$$ LANGUAGE plpgsql;

-- --------------------------------------------------------------
-- 5. DATOS DE SEED (DEMOSTRACION)
-- --------------------------------------------------------------

INSERT INTO departments (name, description, annual_budget, monthly_budget) VALUES
    ('Ingenieria', 'Departamento de desarrollo de software', 120000.00, 10000.00),
    ('Marketing', 'Departamento de marketing y ventas', 80000.00, 6666.67),
    ('Recursos Humanos', 'Gestion de talento y cultura', 50000.00, 4166.67),
    ('Finanzas', 'Contabilidad y tesoreria', 60000.00, 5000.00);

INSERT INTO users (email, password_hash, first_name, last_name, role, department_id, manager_id, is_active) VALUES
    ('cfo@company.com', '$2a$10$hashed', 'Carlos', 'Mendoza', 'CFO', 4, NULL, TRUE),
    ('director.tech@company.com', '$2a$10$hashed', 'Ana', 'Lopez', 'DIRECTOR', 1, 1, TRUE),
    ('manager.marketing@company.com', '$2a$10$hashed', 'Luis', 'Torres', 'MANAGER', 2, 1, TRUE),
    ('lead.dev@company.com', '$2a$10$hashed', 'Maria', 'Garcia', 'MANAGER', 1, 2, TRUE),
    ('dev.junior@company.com', '$2a$10$hashed', 'Juan', 'Perez', 'EMPLOYEE', 1, 4, TRUE),
    ('dev.senior@company.com', '$2a$10$hashed', 'Pedro', 'Ruiz', 'EMPLOYEE', 1, 4, TRUE),
    ('marketing.exec@company.com', '$2a$10$hashed', 'Sofia', 'Castro', 'EMPLOYEE', 2, 3, TRUE);

INSERT INTO expense_categories (name, description, requires_approval) VALUES
    ('Viajes', 'Gastos de transporte, hospedaje y viaticos', TRUE),
    ('Software', 'Licencias, suscripciones y herramientas', TRUE),
    ('Capacitacion', 'Cursos, certificaciones y eventos', TRUE),
    ('Oficina', 'Material de oficina y suministros', FALSE),
    ('Marketing', 'Campañas, publicidad y eventos', TRUE);

INSERT INTO expense_policies (name, department_id, category_id, max_amount_no_approval, approver_role, requires_dual_approval, dual_approval_threshold, priority) VALUES
    ('Ingenieria - Viajes', 1, 1, 0.00, 'DIRECTOR', FALSE, NULL, 1),
    ('Ingenieria - Software', 1, 2, 300.00, 'MANAGER', FALSE, NULL, 2),
    ('Ingenieria - Capacitacion', 1, 3, 500.00, 'MANAGER', TRUE, 1000.00, 3),
    ('Marketing - Campañas', 2, 5, 0.00, 'DIRECTOR', FALSE, NULL, 1),
    ('General - Oficina', NULL, 4, 100.00, 'MANAGER', FALSE, NULL, 10);

INSERT INTO budget_consumption (department_id, year, month, budget_allocated, budget_consumed) VALUES
    (1, 2026, 8, 10000.00, 3500.00),
    (2, 2026, 8, 6666.67, 1200.00),
    (3, 2026, 8, 4166.67, 800.00),
    (4, 2026, 8, 5000.00, 500.00);
