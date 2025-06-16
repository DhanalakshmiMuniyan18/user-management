# Database Schema Documentation

## Overview
This document outlines the database schema for the User Management System. The schema is designed to support user management, role-based access control, and department management functionalities.

## Database Technology
- **Database**: PostgreSQL 15+
- **ORM**: Prisma
- **Migration Tool**: Prisma Migrate

## Schema Design

### Users Table
```sql
model User {
  id            String    @id @default(uuid())
  email         String    @unique
  password      String
  firstName     String
  lastName      String
  isActive      Boolean   @default(true)
  lastLogin     DateTime?
  createdAt     DateTime  @default(now())
  updatedAt     DateTime  @updatedAt
  roleId        String
  departmentId  String?
  role          Role      @relation(fields: [roleId], references: [id])
  department    Department? @relation(fields: [departmentId], references: [id])
  sessions      Session[]
  auditLogs     AuditLog[]
}
```

### Roles Table
```sql
model Role {
  id            String    @id @default(uuid())
  name          String    @unique
  description   String?
  permissions   Permission[]
  users         User[]
  createdAt     DateTime  @default(now())
  updatedAt     DateTime  @updatedAt
}
```

### Permissions Table
```sql
model Permission {
  id            String    @id @default(uuid())
  name          String    @unique
  description   String?
  roles         Role[]
  createdAt     DateTime  @default(now())
  updatedAt     DateTime  @updatedAt
}
```

### Departments Table
```sql
model Department {
  id            String    @id @default(uuid())
  name          String    @unique
  description   String?
  parentId      String?
  managerId     String?
  parent        Department?  @relation("DepartmentHierarchy", fields: [parentId], references: [id])
  children      Department[] @relation("DepartmentHierarchy")
  users         User[]
  createdAt     DateTime  @default(now())
  updatedAt     DateTime  @updatedAt
}
```

### Sessions Table
```sql
model Session {
  id            String    @id @default(uuid())
  userId        String
  token         String    @unique
  deviceInfo    Json?
  ipAddress     String?
  lastActive    DateTime  @default(now())
  expiresAt     DateTime
  user          User      @relation(fields: [userId], references: [id])
  createdAt     DateTime  @default(now())
  updatedAt     DateTime  @updatedAt
}
```

### Audit Logs Table
```sql
model AuditLog {
  id            String    @id @default(uuid())
  userId        String
  action        String
  entityType    String
  entityId      String
  changes       Json?
  ipAddress     String?
  user          User      @relation(fields: [userId], references: [id])
  createdAt     DateTime  @default(now())
}
```

## Relationships

### User Relationships
- One-to-Many with Sessions
- One-to-Many with AuditLogs
- Many-to-One with Role
- Many-to-One with Department

### Role Relationships
- One-to-Many with Users
- Many-to-Many with Permissions

### Department Relationships
- Self-referential (parent-child)
- One-to-Many with Users

### Session Relationships
- Many-to-One with User

### AuditLog Relationships
- Many-to-One with User

## Indexes

### Primary Indexes
- Users: `id`, `email`
- Roles: `id`, `name`
- Permissions: `id`, `name`
- Departments: `id`, `name`
- Sessions: `id`, `token`
- AuditLogs: `id`

### Secondary Indexes
- Users: `roleId`, `departmentId`
- Sessions: `userId`, `expiresAt`
- AuditLogs: `userId`, `createdAt`
- Departments: `parentId`, `managerId`

## Constraints

### Unique Constraints
- User email
- Role name
- Permission name
- Department name
- Session token

### Foreign Key Constraints
- User.roleId → Role.id
- User.departmentId → Department.id
- Department.parentId → Department.id
- Session.userId → User.id
- AuditLog.userId → User.id

## Data Types

### Common Fields
- `id`: UUID
- `createdAt`: DateTime
- `updatedAt`: DateTime

### User Fields
- `email`: String (max 255)
- `password`: String (hashed)
- `firstName`: String (max 100)
- `lastName`: String (max 100)
- `isActive`: Boolean
- `lastLogin`: DateTime (nullable)

### Role Fields
- `name`: String (max 100)
- `description`: String (max 500, nullable)

### Permission Fields
- `name`: String (max 100)
- `description`: String (max 500, nullable)

### Department Fields
- `name`: String (max 100)
- `description`: String (max 500, nullable)

### Session Fields
- `token`: String (max 500)
- `deviceInfo`: JSON (nullable)
- `ipAddress`: String (max 45, nullable)
- `lastActive`: DateTime
- `expiresAt`: DateTime

### AuditLog Fields
- `action`: String (max 100)
- `entityType`: String (max 100)
- `entityId`: String (max 100)
- `changes`: JSON (nullable)
- `ipAddress`: String (max 45, nullable)

## Security Considerations

### Password Storage
- Passwords are hashed using bcrypt
- Salt rounds: 12
- Never stored in plain text

### Session Management
- JWT tokens with expiration
- Refresh token rotation
- Session invalidation on logout

### Audit Trail
- All user actions are logged
- Changes are stored as JSON diffs
- IP addresses are logged for security

## Migration Strategy

### Initial Migration
1. Create base tables
2. Add indexes
3. Set up constraints
4. Create initial roles and permissions

### Future Migrations
- Version controlled using Prisma Migrate
- Each migration is reversible
- Includes data migration scripts
- Tested in staging before production

## Backup Strategy

### Automated Backups
- Daily full backups
- Hourly incremental backups
- Retention period: 30 days

### Backup Verification
- Weekly backup restoration tests
- Data integrity checks
- Performance impact monitoring

## Performance Considerations

### Query Optimization
- Indexed foreign keys
- Composite indexes for common queries
- Regular index maintenance

### Data Archiving
- Audit logs older than 1 year archived
- Archived data compressed
- Archived data accessible via separate API

## Monitoring

### Database Metrics
- Query performance
- Connection pool usage
- Index usage
- Table sizes

### Alert Thresholds
- High CPU usage (>80%)
- High memory usage (>80%)
- Slow queries (>1s)
- Connection pool exhaustion