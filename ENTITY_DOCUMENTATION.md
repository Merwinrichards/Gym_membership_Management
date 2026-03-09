# Member Entity (JPA)
## Overview
The Member entity is a Spring Boot JPA entity class that represents a gym member in the database. It uses Jakarta Persistence API (JPA) annotations for ORM mapping and Lombok for reducing boilerplate code.
## Package
```java
package com.gym.gym_membership_system.model;
```
## Entity Details
### Table Mapping
- **Entity Name**: Member
- **Table Name**: members
- **Primary Key**: id (auto-generated)
### Fields
| Field Name       | Type            | Database Column | Constraints          | Description                                    |
|------------------|-----------------|-----------------|----------------------|------------------------------------------------|
| id               | Long            | id              | Primary Key, Auto    | Unique identifier for each member              |
| name             | String          | name            | NOT NULL, Not Blank  | Member's full name                             |
| contactNumber    | String          | contact_number  | NOT NULL, Not Blank  | Member's phone number                          |
| membershipPlan   | String          | membership_plan | NOT NULL, Not Blank  | Plan type: Monthly, Quarterly, or Yearly       |
| startDate        | LocalDate       | start_date      | NOT NULL             | Membership start date                          |
| endDate          | LocalDate       | end_date        | NOT NULL, Calculated | Membership end date (auto-calculated)          |
| paymentStatus    | String          | payment_status  | NOT NULL, Not Blank  | Payment status: Paid or Pending                |
| createdAt        | LocalDateTime   | created_at      | NOT NULL, Timestamp  | Record creation timestamp (auto-generated)     |
## Annotations Used
### JPA Annotations
- **@Entity**: Marks this class as a JPA entity
- **@Table(name = \"members\")**: Maps to the 'members' table in the database
- **@Id**: Marks the primary key field
- **@GeneratedValue(strategy = GenerationType.IDENTITY)**: Auto-generates ID values
- **@Column**: Defines column properties (nullable, updatable, etc.)
- **@CreationTimestamp**: Automatically sets the creation timestamp
- **@PrePersist**: Executes before inserting a new record
- **@PreUpdate**: Executes before updating an existing record
### Lombok Annotations
- **@Getter**: Generates getter methods for all fields
- **@Setter**: Generates setter methods for all fields
- **@NoArgsConstructor**: Generates a no-argument constructor
- **@AllArgsConstructor**: Generates a constructor with all fields
- **@Builder**: Implements the Builder pattern for object creation
- **@ToString**: Generates a toString() method
### Jakarta Validation Annotations
- **@NotBlank**: Ensures string fields are not null or empty
- **@NotNull**: Ensures fields are not null
## Smart Features
### Auto-Calculation of End Date
The entity includes a `calculateEndDate()` method that automatically calculates the membership end date based on the plan:
- **Monthly**: +1 month from start date
- **Quarterly**: +3 months from start date
- **Yearly**: +12 months from start date
This method is triggered:
- Before inserting a new record (@PrePersist)
- Before updating an existing record (@PreUpdate)
### Automatic Timestamp
The `createdAt` field is automatically populated when a new member is created, thanks to the `@CreationTimestamp` annotation.
## Usage Examples
### Creating a New Member
```java
Member member = Member.builder()
    .name(\"John Doe\")
    .contactNumber(\"1234567890\")
    .membershipPlan(\"Monthly\")
    .startDate(LocalDate.now())
    .paymentStatus(\"Paid\")
    .build();
// endDate will be calculated automatically before saving
// createdAt will be set automatically
memberRepository.save(member);
```
### Using Constructor
```java
Member member = new Member();
member.setName(\"Jane Smith\");
member.setContactNumber(\"0987654321\");
member.setMembershipPlan(\"Yearly\");
member.setStartDate(LocalDate.now());
member.setPaymentStatus(\"Pending\");
memberRepository.save(member);
```
### Updating a Member
```java
Member member = memberRepository.findById(1L).orElseThrow();
member.setPaymentStatus(\"Paid\");
member.setMembershipPlan(\"Quarterly\"); // endDate will be recalculated
memberRepository.save(member);
```
## Validation
The entity includes validation constraints that will be checked before saving:
- **Name**: Cannot be blank or null
- **Contact Number**: Cannot be blank or null
- **Membership Plan**: Cannot be blank or null
- **Start Date**: Cannot be null
- **Payment Status**: Cannot be blank or null
If validation fails, a `ConstraintViolationException` will be thrown.
## Database Schema
When using this entity, the following table will be created:
```sql
CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_number VARCHAR(255) NOT NULL,
    membership_plan VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```
## Integration with Spring Boot
To use this entity, you'll need:
1. **Repository Interface**:
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByPaymentStatus(String paymentStatus);
    List<Member> findByNameContainingIgnoreCase(String name);
}
```
2. **Service Layer**:
```java
@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}
```
3. **Database Configuration** (application.properties):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
## Dependencies Required
Ensure these dependencies are in your `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```
## Best Practices
1. **Always validate input**: The validation annotations ensure data integrity
2. **Use the Builder pattern**: For cleaner object creation
3. **Let JPA handle timestamps**: Don't manually set createdAt
4. **Use appropriate data types**: LocalDate for dates, LocalDateTime for timestamps
5. **Business logic in lifecycle callbacks**: @PrePersist and @PreUpdate for automatic calculations
## Next Steps
After creating the entity, you should:
1. Create a Repository interface extending JpaRepository
2. Create a Service layer for business logic
3. Create a Controller for REST API endpoints (if building a web app)
4. Create DTOs (Data Transfer Objects) for API responses
5. Add more validation rules as needed
6. Consider adding indexes for frequently queried fields
---
**Note**: This entity is production-ready and follows Spring Boot best practices with proper validation, automatic field calculation, and clean code structure using Lombok.
