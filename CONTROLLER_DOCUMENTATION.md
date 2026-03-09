# REST Controller Documentation

## Overview
The `MemberController` is a Spring Boot REST controller that provides REST API endpoints for managing gym members. It handles all HTTP requests related to member operations and returns appropriate responses with proper HTTP status codes.

## Package
```java
package com.gym.gym_membership_system.controller;
```

## Base Path
All endpoints are prefixed with: `/api/members`

## Endpoints

### 1. Add New Member
**Method**: POST  
**Endpoint**: `/api/members`  
**HTTP Status**: 201 (Created)

**Request Body** (JSON):
```json
{
    "name": "John Doe",
    "contactNumber": "1234567890",
    "membershipPlan": "Monthly",
    "startDate": "2026-03-09",
    "paymentStatus": "Paid"
}
```

**Request Validation**:
- `name`: Cannot be empty (from @NotBlank in Member entity)
- `contactNumber`: Cannot be empty (from @NotBlank in Member entity)
- `membershipPlan`: Cannot be empty (from @NotBlank in Member entity)
- `startDate`: Cannot be null (from @NotNull in Member entity)
- Contact number must not already exist (checked in service layer)

**Response** (Success - 201):
```json
{
    "message": "Member added successfully",
    "data": {
        "id": 1,
        "name": "John Doe",
        "contactNumber": "1234567890",
        "membershipPlan": "Monthly",
        "startDate": "2026-03-09",
        "endDate": "2026-04-09",
        "paymentStatus": "Paid",
        "createdAt": "2026-03-09T10:30:45"
    },
    "timestamp": "2026-03-09T10:30:45"
}
```

**Response** (Validation Error - 400):
```json
{
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed for one or more fields",
    "path": "/api/members",
    "fieldErrors": {
        "name": "Name cannot be empty",
        "contactNumber": "Contact number cannot be empty"
    },
    "timestamp": "2026-03-09T10:30:45"
}
```

### 2. Get All Members
**Method**: GET  
**Endpoint**: `/api/members`  
**HTTP Status**: 200 (OK)

**Query Parameters**: None

**Response** (Success - 200):
```json
{
    "message": "Members retrieved successfully",
    "count": 5,
    "data": [
        {
            "id": 1,
            "name": "John Doe",
            "contactNumber": "1234567890",
            "membershipPlan": "Monthly",
            "startDate": "2026-03-09",
            "endDate": "2026-04-09",
            "paymentStatus": "Paid",
            "createdAt": "2026-03-09T10:30:45"
        },
        {
            "id": 2,
            "name": "Jane Smith",
            "contactNumber": "0987654321",
            "membershipPlan": "Yearly",
            "startDate": "2026-01-01",
            "endDate": "2027-01-01",
            "paymentStatus": "Pending",
            "createdAt": "2026-01-01T08:15:30"
        }
    ],
    "timestamp": "2026-03-09T10:30:45"
}
```

### 3. Get Member by ID
**Method**: GET  
**Endpoint**: `/api/members/{id}`  
**HTTP Status**: 200 (OK)

**Path Parameters**:
- `id` (required): The member ID (positive integer)

**Example**: `GET /api/members/1`

**Response** (Success - 200):
```json
{
    "message": "Member retrieved successfully",
    "data": {
        "id": 1,
        "name": "John Doe",
        "contactNumber": "1234567890",
        "membershipPlan": "Monthly",
        "startDate": "2026-03-09",
        "endDate": "2026-04-09",
        "paymentStatus": "Paid",
        "createdAt": "2026-03-09T10:30:45"
    },
    "timestamp": "2026-03-09T10:30:45"
}
```

**Response** (Not Found - 404):
```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Member not found with id: '999'",
    "path": "/api/members/999",
    "timestamp": "2026-03-09T10:30:45"
}
```

### 4. Update Member
**Method**: PUT  
**Endpoint**: `/api/members/{id}`  
**HTTP Status**: 200 (OK)

**Path Parameters**:
- `id` (required): The member ID

**Request Body** (JSON):
```json
{
    "name": "John Smith",
    "contactNumber": "1111111111",
    "membershipPlan": "Quarterly",
    "startDate": "2026-03-09",
    "paymentStatus": "Paid"
}
```

**Example**: `PUT /api/members/1`

**Response** (Success - 200):
```json
{
    "message": "Member updated successfully",
    "data": {
        "id": 1,
        "name": "John Smith",
        "contactNumber": "1111111111",
        "membershipPlan": "Quarterly",
        "startDate": "2026-03-09",
        "endDate": "2026-06-09",
        "paymentStatus": "Paid",
        "createdAt": "2026-03-09T10:30:45"
    },
    "timestamp": "2026-03-09T10:30:45"
}
```

### 5. Delete Member
**Method**: DELETE  
**Endpoint**: `/api/members/{id}`  
**HTTP Status**: 200 (OK)

**Path Parameters**:
- `id` (required): The member ID

**Example**: `DELETE /api/members/1`

**Response** (Success - 200):
```json
{
    "message": "Member deleted successfully",
    "timestamp": "2026-03-09T10:30:45"
}
```

### 6. Search Members by Name
**Method**: GET  
**Endpoint**: `/api/members/search?name={searchTerm}`  
**HTTP Status**: 200 (OK)

**Query Parameters**:
- `name` (required): Search term (partial match, case-insensitive)

**Example**: `GET /api/members/search?name=john`

**Response** (Success - 200):
```json
{
    "message": "Members found with search term: john",
    "count": 2,
    "searchTerm": "john",
    "data": [
        {
            "id": 1,
            "name": "John Doe",
            "contactNumber": "1234567890",
            "membershipPlan": "Monthly",
            "startDate": "2026-03-09",
            "endDate": "2026-04-09",
            "paymentStatus": "Paid",
            "createdAt": "2026-03-09T10:30:45"
        },
        {
            "id": 3,
            "name": "Johnathan Smith",
            "contactNumber": "5555555555",
            "membershipPlan": "Yearly",
            "startDate": "2026-02-01",
            "endDate": "2027-02-01",
            "paymentStatus": "Paid",
            "createdAt": "2026-02-01T14:20:10"
        }
    ],
    "timestamp": "2026-03-09T10:30:45"
}
```

### 7. Filter Members by Payment Status
**Method**: GET  
**Endpoint**: `/api/members/filter?status={paymentStatus}`  
**HTTP Status**: 200 (OK)

**Query Parameters**:
- `status` (required): Payment status ("Paid" or "Pending")

**Example**: `GET /api/members/filter?status=Paid`

**Response** (Success - 200):
```json
{
    "message": "Members filtered by payment status: Paid",
    "count": 3,
    "paymentStatus": "Paid",
    "data": [
        {
            "id": 1,
            "name": "John Doe",
            "contactNumber": "1234567890",
            "membershipPlan": "Monthly",
            "startDate": "2026-03-09",
            "endDate": "2026-04-09",
            "paymentStatus": "Paid",
            "createdAt": "2026-03-09T10:30:45"
        }
    ],
    "timestamp": "2026-03-09T10:30:45"
}
```

## Bonus Endpoints

### 8. Get Total Members Count
**Method**: GET  
**Endpoint**: `/api/members/stats/total`  
**HTTP Status**: 200 (OK)

**Example**: `GET /api/members/stats/total`

**Response**:
```json
{
    "message": "Total members count retrieved",
    "totalCount": 5,
    "timestamp": "2026-03-09T10:30:45"
}
```

### 9. Get Members by Membership Plan
**Method**: GET  
**Endpoint**: `/api/members/plan?plan={membershipPlan}`  
**HTTP Status**: 200 (OK)

**Query Parameters**:
- `plan` (required): Membership plan ("Monthly", "Quarterly", or "Yearly")

**Example**: `GET /api/members/plan?plan=Monthly`

**Response**:
```json
{
    "message": "Members retrieved with membership plan: Monthly",
    "count": 2,
    "membershipPlan": "Monthly",
    "data": [
        {
            "id": 1,
            "name": "John Doe",
            "contactNumber": "1234567890",
            "membershipPlan": "Monthly",
            "startDate": "2026-03-09",
            "endDate": "2026-04-09",
            "paymentStatus": "Paid",
            "createdAt": "2026-03-09T10:30:45"
        }
    ],
    "timestamp": "2026-03-09T10:30:45"
}
```

### 10. Health Check
**Method**: GET  
**Endpoint**: `/api/members/health`  
**HTTP Status**: 200 (OK)

**Example**: `GET /api/members/health`

**Response**:
```json
{
    "status": "OK",
    "message": "Gym Membership Management API is running",
    "timestamp": "2026-03-09T10:30:45"
}
```

## HTTP Status Codes

| Status Code | Meaning | When Used |
|-------------|---------|-----------|
| 200 | OK | Successful GET, PUT, DELETE operations |
| 201 | Created | Successful POST operation (new resource created) |
| 400 | Bad Request | Validation errors, invalid input data |
| 404 | Not Found | Resource (member) not found in database |
| 500 | Internal Server Error | Unexpected server errors |

## Testing with cURL

### Add Member
```bash
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "contactNumber": "1234567890",
    "membershipPlan": "Monthly",
    "startDate": "2026-03-09",
    "paymentStatus": "Paid"
  }'
```

### Get All Members
```bash
curl -X GET http://localhost:8080/api/members
```

### Get Member by ID
```bash
curl -X GET http://localhost:8080/api/members/1
```

### Update Member
```bash
curl -X PUT http://localhost:8080/api/members/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "contactNumber": "1111111111",
    "membershipPlan": "Quarterly",
    "startDate": "2026-03-09",
    "paymentStatus": "Paid"
  }'
```

### Delete Member
```bash
curl -X DELETE http://localhost:8080/api/members/1
```

### Search by Name
```bash
curl -X GET "http://localhost:8080/api/members/search?name=john"
```

### Filter by Payment Status
```bash
curl -X GET "http://localhost:8080/api/members/filter?status=Paid"
```

### Get by Membership Plan
```bash
curl -X GET "http://localhost:8080/api/members/plan?plan=Monthly"
```

## Testing with Postman

1. **Create Collection**: "Gym Membership System"
2. **Set Base URL**: `http://localhost:8080`

3. **Create Requests**:
   - POST /api/members
   - GET /api/members
   - GET /api/members/{{id}}
   - PUT /api/members/{{id}}
   - DELETE /api/members/{{id}}
   - GET /api/members/search?name={{name}}
   - GET /api/members/filter?status={{status}}

4. **Environment Variables**:
   - base_url: http://localhost:8080
   - id: 1
   - name: john
   - status: Paid

## Configuration for Running

1. **Start the Application**:
   ```bash
   mvn spring-boot:run
   ```

2. **Or compile and run**:
   ```bash
   mvn clean package
   java -jar target/gym-membership-system-0.0.1-SNAPSHOT.jar
   ```

3. **Default Server**:
   - URL: http://localhost:8080
   - Port: 8080 (configurable in application.properties)

## Error Handling

All errors are handled by the `GlobalExceptionHandler` class which returns structured error responses:

### Validation Error (400)
```json
{
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed for one or more fields",
    "path": "/api/members",
    "fieldErrors": {
        "name": "Name cannot be empty"
    },
    "timestamp": "2026-03-09T10:30:45"
}
```

### Resource Not Found (404)
```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Member not found with id: '999'",
    "path": "/api/members/999",
    "timestamp": "2026-03-09T10:30:45"
}
```

### Server Error (500)
```json
{
    "status": 500,
    "error": "Internal Server Error",
    "message": "An unexpected error occurred: Database connection failed",
    "path": "/api/members",
    "timestamp": "2026-03-09T10:30:45"
}
```

## Best Practices Used

1. **RESTful Design**: Follows REST conventions with proper HTTP methods and status codes
2. **Validation**: Uses @Valid annotation for request body validation
3. **Error Handling**: Centralized error handling with GlobalExceptionHandler
4. **Response Format**: Consistent response structure with metadata and timestamp
5. **Transaction Management**: All service methods are transactional
6. **Documentation**: Comprehensive JavaDoc for all endpoints
7. **CORS**: Enabled for cross-origin requests (@CrossOrigin)

---

**Created**: March 9, 2026  
**Status**: Production-ready  
**Framework**: Spring Boot 4.0.3

