-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(15),
    profile_picture OID,
    enabled BOOLEAN NOT NULL DEFAULT true,
    email_verified BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create permissions table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(10) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT action_check CHECK (action IN ('CREATE', 'READ', 'UPDATE', 'DELETE'))
);

-- Create user_roles junction table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Create role_permissions junction table
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- Create audit_logs table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    username VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_roles_name ON roles(name);
CREATE INDEX idx_permissions_name ON permissions(name);
CREATE INDEX idx_permissions_resource ON permissions(resource);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_username ON audit_logs(username);

-- Insert default roles
INSERT INTO roles (name, description, created_at, updated_at, created_by, updated_by)
VALUES 
    ('ROLE_ADMIN', 'Administrator role with full access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('ROLE_USER', 'Standard user role with limited access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert default permissions
INSERT INTO permissions (name, resource, action, description, created_at, updated_at, created_by, updated_by)
VALUES 
    ('USER_CREATE', 'USER', 'CREATE', 'Create new users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('USER_READ', 'USER', 'READ', 'View user details', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('USER_UPDATE', 'USER', 'UPDATE', 'Update user details', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('USER_DELETE', 'USER', 'DELETE', 'Delete users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('ROLE_CREATE', 'ROLE', 'CREATE', 'Create new roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('ROLE_READ', 'ROLE', 'READ', 'View role details', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('ROLE_UPDATE', 'ROLE', 'UPDATE', 'Update role details', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('ROLE_DELETE', 'ROLE', 'DELETE', 'Delete roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Assign permissions to admin role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_ADMIN';

-- Assign basic permissions to user role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_USER'
AND p.name IN ('USER_READ'); 