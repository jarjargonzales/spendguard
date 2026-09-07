CREATE DATABASE spendguard OWNER spendguard;

-- =====================================================
-- SpendGuard - Esquema Inicial de Base de Datos
-- PostgreSQL 16
-- =====================================================

-- Secuencias
CREATE SEQUENCE seq_departments START 1;
CREATE SEQUENCE seq_users START 1;
CREATE SEQUENCE seq_expense_categories START 1;
CREATE SEQUENCE seq_expense_policies START 1;
CREATE SEQUENCE seq_expense_requests START 1;
CREATE SEQUENCE seq_approvals START 1;
CREATE SEQUENCE seq_audit_log START 1;
CREATE SEQUENCE seq_budget_consumption START 1;

-- Tabla: departments
CREATE TABLE departments (
    department_id numeric(10) DEFAULT nextval('seq_departments'::regclass) PRIMARY KEY,
    name varchar(200) NOT NULL UNIQUE,
    description text,
    monthly_budget numeric(15,2) NOT NULL DEFAULT 0,
    active boolean NOT NULL DEFAULT true,
    version varchar(20) NOT NULL DEFAULT '1',
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by numeric(10) NOT NULL,
    updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by numeric(10) NOT NULL,
    owner_id numeric(10) NOT NULL
);

-- Tabla: users
CREATE TABLE users (
    user_id numeric(10) DEFAULT nextval('seq_users'::regclass) PRIMARY KEY,
    username varchar(100) NOT NULL UNIQUE,
    email varchar(200) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    first_name varchar(100),
    last_name varchar(100),
    role varchar(20) NOT NULL,
    department_id numeric(10) REFERENCES departments(department_id),
    manager_id numeric(10) REFERENCES users(user_id),
    active boolean NOT NULL DEFAULT true,
    version varchar(20) NOT NULL DEFAULT '1',
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by numeric(10) NOT NULL,
    updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by numeric(10) NOT NULL,
    owner_id numeric(10) NOT NULL
);

-- Tabla: expense_categories
CREATE TABLE expense_categories (
    category_id numeric(10) DEFAULT nextval('seq_expense_categories'::regclass) PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE,
    description text,
    requires_approval boolean NOT NULL DEFAULT true,
    active boolean NOT NULL DEFAULT true,
    version varchar(20) NOT NULL DEFAULT '1',
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by numeric(10) NOT NULL,
    updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by numeric(10) NOT NULL,
    owner_id numeric(10) NOT NULL
);

-- Tabla: expense_policies
CREATE TABLE expense_policies (
    policy_id numeric(10) DEFAULT nextval('seq_expense_policies'::regclass) PRIMARY KEY,
    name varchar(200) NOT NULL,
    description text,
    min_amount numeric(15,2) NOT NULL DEFAULT 0,
    max_amount numeric(15,2) NOT NULL DEFAULT 999999999,
    department_id numeric(10) REFERENCES departments(department_id),
    category_id numeric(10) REFERENCES expense_categories(category_id),
    requires_approval boolean NOT NULL DEFAULT true,
    approval_chain_type varchar(20) NOT NULL DEFAULT 'SEQUENTIAL',
    approver_roles varchar(500) NOT NULL,
    priority int NOT NULL DEFAULT 100,
    active boolean NOT NULL DEFAULT true,
    version varchar(20) NOT NULL DEFAULT '1',
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by numeric(10) NOT NULL,
    updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by numeric(10) NOT NULL,
    owner_id numeric(10) NOT NULL
);

-- Tabla: expense_requests
CREATE TABLE expense_requests (
    request_id numeric(10) DEFAULT nextval('seq_expense_requests'::regclass) PRIMARY KEY,
    requester_id numeric(10) NOT NULL REFERENCES users(user_id),
    department_id numeric(10) NOT NULL REFERENCES departments(department_id),
    category_id numeric(10) NOT NULL REFERENCES expense_categories(category_id),
    title varchar(200) NOT NULL,
    description text,
    amount numeric(15,2) NOT NULL CHECK (amount > 0),
    currency varchar(3) NOT NULL DEFAULT 'USD',
    status varchar(20) NOT NULL,
    submission_date timestamp,
    resolution_date timestamp,
    version_opt integer NOT NULL DEFAULT 0,
    version varchar(20) NOT NULL DEFAULT '1',
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by numeric(10) NOT NULL,
    updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by numeric(10) NOT NULL,
    owner_id numeric(10) NOT NULL
);

-- Tabla: approvals
CREATE TABLE approvals (
    approval_id numeric(10) DEFAULT nextval('seq_approvals'::regclass) PRIMARY KEY,
    request_id numeric(10) NOT NULL REFERENCES expense_requests(request_id),
    approver_id numeric(10) NOT NULL REFERENCES users(user_id),
    decision varchar(20) NOT NULL,
    comments text,
    decided_at timestamp,
    sequence_order int NOT NULL,
    version varchar(20) NOT NULL DEFAULT '1',
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by numeric(10) NOT NULL,
    updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by numeric(10) NOT NULL,
    owner_id numeric(10) NOT NULL
);

-- Tabla: audit_log
CREATE TABLE audit_log (
    audit_id numeric(10) DEFAULT nextval('seq_audit_log'::regclass) PRIMARY KEY,
    request_id numeric(10) REFERENCES expense_requests(request_id),
    user_id numeric(10) REFERENCES users(user_id),
    event_type varchar(50) NOT NULL,
    old_status varchar(20),
    new_status varchar(20),
    ip_address varchar(45),
    user_agent text,
    event_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details text,
    version varchar(20) NOT NULL DEFAULT '1',
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by numeric(10) NOT NULL,
    updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by numeric(10) NOT NULL,
    owner_id numeric(10) NOT NULL
);

-- Tabla: budget_consumption
CREATE TABLE budget_consumption (
    consumption_id numeric(10) DEFAULT nextval('seq_budget_consumption'::regclass) PRIMARY KEY,
    department_id numeric(10) NOT NULL REFERENCES departments(department_id),
    month_year varchar(7) NOT NULL,
    budget_allocated numeric(15,2) NOT NULL,
    budget_consumed numeric(15,2) NOT NULL DEFAULT 0,
    version varchar(20) NOT NULL DEFAULT '1',
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by numeric(10) NOT NULL,
    updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by numeric(10) NOT NULL,
    owner_id numeric(10) NOT NULL,
    CONSTRAINT uk_budget_dept_month UNIQUE (department_id, month_year)
);

-- Índices
CREATE INDEX idx_users_department ON users(department_id);
CREATE INDEX idx_requests_requester ON expense_requests(requester_id);
CREATE INDEX idx_requests_status ON expense_requests(status);
CREATE INDEX idx_requests_department ON expense_requests(department_id);
CREATE INDEX idx_approvals_request ON approvals(request_id);
CREATE INDEX idx_approvals_approver ON approvals(approver_id);
CREATE INDEX idx_audit_request ON audit_log(request_id);
CREATE INDEX idx_budget_dept ON budget_consumption(department_id);

-- Comentarios
COMMENT ON TABLE expense_requests IS 'Solicitudes de gasto con máquina de estados y control de versión optimista';
COMMENT ON COLUMN expense_requests.version_opt IS 'Campo para optimistic locking con JPA @Version';
COMMENT ON COLUMN expense_requests.version IS 'Versión de auditoría textual';
COMMENT ON TABLE audit_log IS 'Registro inmutable de eventos (INSERT-only)';