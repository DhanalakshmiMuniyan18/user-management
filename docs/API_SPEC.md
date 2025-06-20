# REST API Specification for User Management System (Spring Boot + ORM)

---

## 1. Users Module

### Create User
- **Endpoint:** `POST /users`
- **Description:** Create a new user account.
- **Authentication/Authorization:** JWT Required, Admin Only
- **Request Headers:**
  - `Content-Type: application/json`
- **Request Body:**
```json
{
  "name": "string", // required
  "email": "string", // required
  "status": "string" // optional, default: "active"
}
```
- **Success Response:**
  - `201 Created`
```json
{
  "id": 1,
  "name": "string",
  "email": "string",
  "status": "active",
  "created_at": "2024-06-01T12:00:00Z"
}
```
- **Error Responses:**
  - `400 Bad Request` (validation error)
  - `401 Unauthorized`
  - `403 Forbidden`
  - `409 Conflict` (email already exists)
- **Security Notes:** Input validation, unique email constraint.

---

### Get User by ID
- **Endpoint:** `GET /users/{id}`
- **Description:** Retrieve user details by user ID.
- **Authentication/Authorization:** JWT Required, Admin or Self
- **Path Parameters:**
  - `id` (integer, required): User ID
- **Success Response:**
  - `200 OK`
```json
{
  "id": 1,
  "name": "string",
  "email": "string",
  "status": "active",
  "last_login": "2024-06-01T12:00:00Z",
  "created_at": "2024-06-01T12:00:00Z",
  "updated_at": "2024-06-01T12:00:00Z"
}
```
- **Error Responses:**
  - `401 Unauthorized`
  - `403 Forbidden`
  - `404 Not Found`

---

### List Users
- **Endpoint:** `GET /users`
- **Description:** List users with pagination and filtering.
- **Authentication/Authorization:** JWT Required, Admin Only
- **Query Parameters:**
  - `limit` (integer, optional): Max results per page
  - `offset` (integer, optional): Pagination offset
  - `status` (string, optional): Filter by status
  - `search` (string, optional): Search by name or email
- **Success Response:**
  - `200 OK`
```json
{
  "total": 100,
  "users": [
    { "id": 1, "name": "string", "email": "string", "status": "active" }
  ]
}
```
- **Error Responses:**
  - `401 Unauthorized`
  - `403 Forbidden`

---

### Update User
- **Endpoint:** `PUT /users/{id}`
- **Description:** Update user details.
- **Authentication/Authorization:** JWT Required, Admin or Self
- **Request Headers:**
  - `Content-Type: application/json`
- **Path Parameters:**
  - `id` (integer, required): User ID
- **Request Body:**
```json
{
  "name": "string", // optional
  "status": "string" // optional
}
```
- **Success Response:**
  - `200 OK` (updated user object)
- **Error Responses:**
  - `400 Bad Request`
  - `401 Unauthorized`
  - `403 Forbidden`
  - `404 Not Found`

---

### Delete (Deactivate) User
- **Endpoint:** `DELETE /users/{id}`
- **Description:** Deactivate a user account.
- **Authentication/Authorization:** JWT Required, Admin Only
- **Path Parameters:**
  - `id` (integer, required): User ID
- **Success Response:**
  - `204 No Content`
- **Error Responses:**
  - `401 Unauthorized`
  - `403 Forbidden`
  - `404 Not Found`

---

## 2. Roles Module

### Create Role
- **Endpoint:** `POST /roles`
- **Description:** Create a new role.
- **Authentication/Authorization:** JWT Required, Admin Only
- **Request Headers:**
  - `Content-Type: application/json`
- **Request Body:**
```json
{
  "name": "string", // required
  "description": "string" // optional
}
```
- **Success Response:**
  - `201 Created` (role object)
- **Error Responses:**
  - `400 Bad Request`
  - `401 Unauthorized`
  - `403 Forbidden`
  - `409 Conflict` (role name exists)

---

### List Roles
- **Endpoint:** `GET /roles`
- **Description:** List all roles.
- **Authentication/Authorization:** JWT Required
- **Success Response:**
  - `200 OK` (array of roles)

---

### Assign Role to User
- **Endpoint:** `POST /users/{id}/roles`
- **Description:** Assign one or more roles to a user.
- **Authentication/Authorization:** JWT Required, Admin Only
- **Path Parameters:**
  - `id` (integer, required): User ID
- **Request Body:**
```json
{
  "roleIds": [1, 2] // required, array of role IDs
}
```
- **Success Response:**
  - `200 OK` (updated user roles)
- **Error Responses:**
  - `400 Bad Request`
  - `401 Unauthorized`
  - `403 Forbidden`
  - `404 Not Found`

---

## 3. Permissions Module

### List Permissions
- **Endpoint:** `GET /permissions`
- **Description:** List all permissions.
- **Authentication/Authorization:** JWT Required, Admin Only
- **Success Response:**
  - `200 OK` (array of permissions)

---

### Assign Permissions to Role
- **Endpoint:** `POST /roles/{id}/permissions`
- **Description:** Assign permissions to a role.
- **Authentication/Authorization:** JWT Required, Admin Only
- **Path Parameters:**
  - `id` (integer, required): Role ID
- **Request Body:**
```json
{
  "permissionIds": [1, 2] // required, array of permission IDs
}
```
- **Success Response:**
  - `200 OK` (updated role permissions)
- **Error Responses:**
  - `400 Bad Request`
  - `401 Unauthorized`
  - `403 Forbidden`
  - `404 Not Found`

---

## 4. Audit Logs Module

### List Audit Logs
- **Endpoint:** `GET /audit-logs`
- **Description:** List audit logs with filtering and pagination.
- **Authentication/Authorization:** JWT Required, Admin or Compliance Officer
- **Query Parameters:**
  - `userId` (integer, optional): Filter by user
  - `action` (string, optional): Filter by action
  - `from` (string, optional): Start date (ISO8601)
  - `to` (string, optional): End date (ISO8601)
  - `limit` (integer, optional)
  - `offset` (integer, optional)
- **Success Response:**
  - `200 OK` (paginated audit logs)
- **Error Responses:**
  - `401 Unauthorized`
  - `403 Forbidden`

---

## 5. Access Requests Module

### Create Access Request
- **Endpoint:** `POST /access-requests`
- **Description:** User requests access to a role.
- **Authentication/Authorization:** JWT Required
- **Request Headers:**
  - `Content-Type: application/json`
- **Request Body:**
```json
{
  "requestedRole": 1 // required, role ID
}
```
- **Success Response:**
  - `201 Created` (access request object)
- **Error Responses:**
  - `400 Bad Request`
  - `401 Unauthorized`
  - `403 Forbidden`

---

### List Access Requests
- **Endpoint:** `GET /access-requests`
- **Description:** List access requests (admin/manager only).
- **Authentication/Authorization:** JWT Required, Admin Only
- **Query Parameters:**
  - `status` (string, optional)
  - `userId` (integer, optional)
  - `limit` (integer, optional)
  - `offset` (integer, optional)
- **Success Response:**
  - `200 OK` (paginated access requests)

---

### Approve/Reject Access Request
- **Endpoint:** `PUT /access-requests/{id}`
- **Description:** Approve or reject an access request.
- **Authentication/Authorization:** JWT Required, Admin Only
- **Path Parameters:**
  - `id` (integer, required): Access Request ID
- **Request Body:**
```json
{
  "status": "approved" // or "rejected"
}
```
- **Success Response:**
  - `200 OK` (updated access request)
- **Error Responses:**
  - `400 Bad Request`
  - `401 Unauthorized`
  - `403 Forbidden`
  - `404 Not Found`

---

## General Security Notes
- All endpoints require JWT authentication unless otherwise specified.
- Role-based access control enforced at endpoint level.
- Input validation and error handling for all endpoints.
- Rate limiting and audit logging recommended for sensitive operations. 