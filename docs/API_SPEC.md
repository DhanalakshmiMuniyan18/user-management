# API Specification

## Authentication

All endpoints except `/auth/login` and `/auth/register` require JWT authentication.
Include the JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

## Common Headers

All requests should include:
```
Content-Type: application/json
Accept: application/json
```

## Common Query Parameters

The following query parameters are available for list endpoints:

| Parameter | Type    | Description                                    | Default |
|-----------|---------|------------------------------------------------|---------|
| page      | integer | Page number for pagination                      | 1       |
| limit     | integer | Number of items per page                        | 20      |
| sortBy    | string  | Field to sort by                                | created_at |
| order     | string  | Sort order (asc/desc)                           | desc    |
| search    | string  | Search term for filtering                       | null    |

## Authentication Endpoints

### Login
- **Path**: `POST /auth/login`
- **Description**: Authenticate user and return JWT token
- **Auth**: None
- **Request Body**:
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```
- **Response**: 
  ```json
  {
    "token": "string",
    "user": {
      "id": "integer",
      "email": "string",
      "firstName": "string",
      "lastName": "string",
      "role": "string"
    }
  }
  ```

### Register
- **Path**: `POST /auth/register`
- **Description**: Register new user
- **Auth**: None
- **Request Body**:
  ```json
  {
    "email": "string",
    "password": "string",
    "firstName": "string",
    "lastName": "string",
    "departmentId": "integer"
  }
  ```
- **Response**: 
  ```json
  {
    "id": "integer",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "status": "string"
  }
  ```

### Refresh Token
- **Path**: `POST /auth/refresh`
- **Description**: Refresh JWT token
- **Auth**: JWT Required
- **Response**: 
  ```json
  {
    "token": "string"
  }
  ```

## User Management Endpoints

### List Users
- **Path**: `GET /users`
- **Description**: Get paginated list of users
- **Auth**: JWT Required, Admin Only
- **Query Parameters**:
  - `status`: string (active/inactive/suspended/terminated)
  - `departmentId`: integer
  - `roleId`: integer
- **Response**: 
  ```json
  {
    "data": [
      {
        "id": "integer",
        "email": "string",
        "firstName": "string",
        "lastName": "string",
        "department": {
          "id": "integer",
          "name": "string"
        },
        "role": {
          "id": "integer",
          "name": "string"
        },
        "status": "string",
        "createdAt": "datetime"
      }
    ],
    "pagination": {
      "total": "integer",
      "page": "integer",
      "limit": "integer",
      "pages": "integer"
    }
  }
  ```

### Get User
- **Path**: `GET /users/{id}`
- **Description**: Get user details
- **Auth**: JWT Required
- **Response**: 
  ```json
  {
    "id": "integer",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "department": {
      "id": "integer",
      "name": "string"
    },
    "role": {
      "id": "integer",
      "name": "string"
    },
    "status": "string",
    "createdAt": "datetime",
    "lastLoginAt": "datetime"
  }
  ```

### Update User
- **Path**: `PUT /users/{id}`
- **Description**: Update user details
- **Auth**: JWT Required, Admin Only
- **Request Body**:
  ```json
  {
    "firstName": "string",
    "lastName": "string",
    "departmentId": "integer",
    "roleId": "integer",
    "status": "string"
  }
  ```
- **Response**: Updated user object

### Delete User
- **Path**: `DELETE /users/{id}`
- **Description**: Delete user
- **Auth**: JWT Required, Admin Only
- **Response**: 204 No Content

## Role Management Endpoints

### List Roles
- **Path**: `GET /roles`
- **Description**: Get paginated list of roles
- **Auth**: JWT Required, Admin Only
- **Response**: 
  ```json
  {
    "data": [
      {
        "id": "integer",
        "name": "string",
        "description": "string",
        "isSystemRole": "boolean",
        "permissions": [
          {
            "id": "integer",
            "name": "string",
            "module": "string",
            "action": "string"
          }
        ]
      }
    ],
    "pagination": {
      "total": "integer",
      "page": "integer",
      "limit": "integer",
      "pages": "integer"
    }
  }
  ```

### Create Role
- **Path**: `POST /roles`
- **Description**: Create new role
- **Auth**: JWT Required, Admin Only
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string",
    "permissionIds": ["integer"]
  }
  ```
- **Response**: Created role object

### Update Role
- **Path**: `PUT /roles/{id}`
- **Description**: Update role
- **Auth**: JWT Required, Admin Only
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string",
    "permissionIds": ["integer"]
  }
  ```
- **Response**: Updated role object

### Delete Role
- **Path**: `DELETE /roles/{id}`
- **Description**: Delete role
- **Auth**: JWT Required, Admin Only
- **Response**: 204 No Content

## Permission Management Endpoints

### List Permissions
- **Path**: `GET /permissions`
- **Description**: Get paginated list of permissions
- **Auth**: JWT Required, Admin Only
- **Query Parameters**:
  - `module`: string
  - `action`: string
- **Response**: 
  ```json
  {
    "data": [
      {
        "id": "integer",
        "name": "string",
        "description": "string",
        "module": "string",
        "action": "string",
        "resourceType": "string"
      }
    ],
    "pagination": {
      "total": "integer",
      "page": "integer",
      "limit": "integer",
      "pages": "integer"
    }
  }
  ```

## Department Management Endpoints

### List Departments
- **Path**: `GET /departments`
- **Description**: Get paginated list of departments
- **Auth**: JWT Required
- **Response**: 
  ```json
  {
    "data": [
      {
        "id": "integer",
        "name": "string",
        "description": "string",
        "manager": {
          "id": "integer",
          "firstName": "string",
          "lastName": "string"
        },
        "parentDepartment": {
          "id": "integer",
          "name": "string"
        }
      }
    ],
    "pagination": {
      "total": "integer",
      "page": "integer",
      "limit": "integer",
      "pages": "integer"
    }
  }
  ```

### Create Department
- **Path**: `POST /departments`
- **Description**: Create new department
- **Auth**: JWT Required, Admin Only
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string",
    "managerId": "integer",
    "parentDepartmentId": "integer"
  }
  ```
- **Response**: Created department object

### Update Department
- **Path**: `PUT /departments/{id}`
- **Description**: Update department
- **Auth**: JWT Required, Admin Only
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string",
    "managerId": "integer",
    "parentDepartmentId": "integer"
  }
  ```
- **Response**: Updated department object

### Delete Department
- **Path**: `DELETE /departments/{id}`
- **Description**: Delete department
- **Auth**: JWT Required, Admin Only
- **Response**: 204 No Content

## Audit Log Endpoints

### List Audit Logs
- **Path**: `GET /audit-logs`
- **Description**: Get paginated list of audit logs
- **Auth**: JWT Required, Admin Only
- **Query Parameters**:
  - `userId`: integer
  - `action`: string
  - `module`: string
  - `severity`: string
  - `startDate`: datetime
  - `endDate`: datetime
- **Response**: 
  ```json
  {
    "data": [
      {
        "id": "integer",
        "user": {
          "id": "integer",
          "email": "string"
        },
        "action": "string",
        "timestamp": "datetime",
        "ipAddress": "string",
        "details": "object",
        "module": "string",
        "severity": "string",
        "status": "string"
      }
    ],
    "pagination": {
      "total": "integer",
      "page": "integer",
      "limit": "integer",
      "pages": "integer"
    }
  }
  ```

## Session Management Endpoints

### List Active Sessions
- **Path**: `GET /sessions`
- **Description**: Get paginated list of active sessions
- **Auth**: JWT Required, Admin Only
- **Query Parameters**:
  - `userId`: integer
  - `status`: string
- **Response**: 
  ```json
  {
    "data": [
      {
        "id": "integer",
        "user": {
          "id": "integer",
          "email": "string"
        },
        "startTime": "datetime",
        "lastActivity": "datetime",
        "ipAddress": "string",
        "deviceInfo": "object",
        "status": "string"
      }
    ],
    "pagination": {
      "total": "integer",
      "page": "integer",
      "limit": "integer",
      "pages": "integer"
    }
  }
  ```

### Terminate Session
- **Path**: `DELETE /sessions/{id}`
- **Description**: Terminate a session
- **Auth**: JWT Required, Admin Only
- **Response**: 204 No Content

## Error Responses

All endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "error": {
    "code": "string",
    "message": "string",
    "details": ["string"]
  }
}
```

### 401 Unauthorized
```json
{
  "error": {
    "code": "unauthorized",
    "message": "Invalid or expired token"
  }
}
```

### 403 Forbidden
```json
{
  "error": {
    "code": "forbidden",
    "message": "Insufficient permissions"
  }
}
```

### 404 Not Found
```json
{
  "error": {
    "code": "not_found",
    "message": "Resource not found"
  }
}
```

### 500 Internal Server Error
```json
{
  "error": {
    "code": "internal_error",
    "message": "An unexpected error occurred"
  }
}
```

## Rate Limiting

API endpoints are rate-limited to:
- 100 requests per minute for authenticated users
- 20 requests per minute for unauthenticated users

Rate limit headers are included in all responses:
```
X-RateLimit-Limit: <limit>
X-RateLimit-Remaining: <remaining>
X-RateLimit-Reset: <reset_timestamp>
``` 