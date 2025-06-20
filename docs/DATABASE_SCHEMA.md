# Database Schema for User Management Application

## Table: users
| Column Name   | Data Type        | Constraints                                  |
|--------------|------------------|----------------------------------------------|
| id           | SERIAL           | PRIMARY KEY, NOT NULL                        |
| name         | VARCHAR(255)     | NOT NULL                                     |
| email        | VARCHAR(255)     | NOT NULL, UNIQUE                             |
| status       | VARCHAR(50)      | NOT NULL, DEFAULT 'active'                   |
| last_login   | TIMESTAMP        |                                              |
| created_at   | TIMESTAMP        | NOT NULL, DEFAULT CURRENT_TIMESTAMP          |
| updated_at   | TIMESTAMP        | NOT NULL, DEFAULT CURRENT_TIMESTAMP          |

**Relationships:**
- Many-to-many with roles via user_roles
- One-to-many with audit_logs
- One-to-many with access_requests

---

## Table: roles
| Column Name   | Data Type        | Constraints                                  |
|--------------|------------------|----------------------------------------------|
| id           | SERIAL           | PRIMARY KEY, NOT NULL                        |
| name         | VARCHAR(100)     | NOT NULL, UNIQUE                             |
| description  | TEXT             |                                              |

**Relationships:**
- Many-to-many with users via user_roles
- Many-to-many with permissions via role_permissions

---

## Table: permissions
| Column Name   | Data Type        | Constraints                                  |
|--------------|------------------|----------------------------------------------|
| id           | SERIAL           | PRIMARY KEY, NOT NULL                        |
| name         | VARCHAR(100)     | NOT NULL, UNIQUE                             |
| description  | TEXT             |                                              |

**Relationships:**
- Many-to-many with roles via role_permissions

---

## Table: user_roles (junction table)
| Column Name   | Data Type        | Constraints                                  |
|--------------|------------------|----------------------------------------------|
| user_id      | INTEGER          | NOT NULL, FOREIGN KEY REFERENCES users(id)    |
| role_id      | INTEGER          | NOT NULL, FOREIGN KEY REFERENCES roles(id)    |

**Constraints:**
- PRIMARY KEY (user_id, role_id)

---

## Table: role_permissions (junction table)
| Column Name   | Data Type        | Constraints                                  |
|--------------|------------------|----------------------------------------------|
| role_id      | INTEGER          | NOT NULL, FOREIGN KEY REFERENCES roles(id)    |
| permission_id| INTEGER          | NOT NULL, FOREIGN KEY REFERENCES permissions(id) |

**Constraints:**
- PRIMARY KEY (role_id, permission_id)

---

## Table: audit_logs
| Column Name   | Data Type        | Constraints                                  |
|--------------|------------------|----------------------------------------------|
| id           | SERIAL           | PRIMARY KEY, NOT NULL                        |
| user_id      | INTEGER          | NOT NULL, FOREIGN KEY REFERENCES users(id)    |
| action       | VARCHAR(255)     | NOT NULL                                     |
| timestamp    | TIMESTAMP        | NOT NULL, DEFAULT CURRENT_TIMESTAMP          |
| details      | TEXT             |                                              |

**Relationships:**
- Many-to-one with users

---

## Table: access_requests
| Column Name     | Data Type        | Constraints                                  |
|----------------|------------------|----------------------------------------------|
| id             | SERIAL           | PRIMARY KEY, NOT NULL                        |
| user_id        | INTEGER          | NOT NULL, FOREIGN KEY REFERENCES users(id)    |
| requested_role | INTEGER          | NOT NULL, FOREIGN KEY REFERENCES roles(id)    |
| status         | VARCHAR(50)      | NOT NULL, DEFAULT 'pending'                  |
| requested_at   | TIMESTAMP        | NOT NULL, DEFAULT CURRENT_TIMESTAMP          |
| reviewed_by    | INTEGER          | FOREIGN KEY REFERENCES users(id)             |

**Relationships:**
- Many-to-one with users (user_id)
- Many-to-one with roles (requested_role)
- Many-to-one with users (reviewed_by)

---

## Indexing Recommendations
- Index `users.email` (already unique)
- Index `audit_logs.timestamp` for faster log queries
- Index `access_requests.status` and `requested_at` for workflow queries

---

## Example SQL DDL (PostgreSQL)
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'active',
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE user_roles (
    user_id INTEGER NOT NULL REFERENCES users(id),
    role_id INTEGER NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id INTEGER NOT NULL REFERENCES roles(id),
    permission_id INTEGER NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    action VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);

CREATE TABLE access_requests (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    requested_role INTEGER NOT NULL REFERENCES roles(id),
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_by INTEGER REFERENCES users(id)
);

CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_access_requests_status ON access_requests(status);
CREATE INDEX idx_access_requests_requested_at ON access_requests(requested_at);
``` 