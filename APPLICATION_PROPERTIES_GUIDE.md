# Spring Boot Application Configuration Guide

## Configuration Summary

The `application.properties` file has been successfully configured for the Gym Membership Management System with MySQL database connection.

## Key Configuration Properties

### Database Connection (REQUIRED)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### Hibernate Configuration (ORM)
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Server Configuration
```properties
server.port=8080
```

### Logging Configuration
```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.level.com.gym.gym_membership_system=INFO
```

### Error Handling
```properties
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=on_param
```

## Before Running the Application

### Step 1: Create MySQL Database
```sql
CREATE DATABASE gym_db;
```

### Step 2: Verify MySQL is Running
- Start MySQL Server
- Default port: 3306

### Step 3: Update Credentials (if needed)
If your MySQL password is not "root", update:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Step 4: Run the Application
```bash
mvn spring-boot:run
```

## What Happens on Startup

1. **Database Connection**: Spring connects to MySQL database `gym_db`
2. **Schema Creation**: Hibernate auto-creates `members` table
3. **Validation**: Spring validates entity mappings
4. **Server Start**: Application starts on http://localhost:8080

## Verifying Configuration

### Test Connection
```bash
curl http://localhost:8080/api/members/health
```

Expected response:
```json
{
    "status": "OK",
    "message": "Gym Membership Management API is running"
}
```

## Important Notes

- **Development**: Current settings are suitable for development
- **Production**: Change password and disable SQL logging
- **Database Auto-Creation**: Tables are created automatically
- **SQL Queries**: Visible in console for debugging

## Properties Explained

| Property | Value | Meaning |
|----------|-------|---------|
| spring.datasource.url | jdbc:mysql://localhost:3306/gym_db | MySQL connection URL |
| spring.datasource.username | root | Database username |
| spring.datasource.password | root | Database password |
| spring.jpa.hibernate.ddl-auto | update | Auto-update schema |
| spring.jpa.show-sql | true | Log SQL queries |
| spring.jpa.properties.hibernate.dialect | org.hibernate.dialect.MySQLDialect | MySQL dialect |
| server.port | 8080 | Server port number |

## Next Steps

1. Create the gym_db database
2. Start MySQL server
3. Run the Spring Boot application
4. Access API at http://localhost:8080/api/members

---

Status: ✅ Configuration Complete

