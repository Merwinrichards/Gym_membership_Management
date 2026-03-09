# MemberRepository Interface Documentation
## Overview
The MemberRepository is a Spring Data JPA repository interface that provides data access operations for the Member entity. It extends JpaRepository which provides built-in CRUD operations and adds custom query methods for specific business requirements.
## Package
```java
package com.gym.gym_membership_system.repository;
```
## Interface Declaration
```java
@Repository
public interface MemberRepository extends JpaRepository<Member, Long>
```
## Built-in Methods (from JpaRepository)
By extending JpaRepository<Member, Long>, the repository automatically inherits these methods:
### Save Operations
- save(Member entity) - Save or update a member
- saveAll(Iterable<Member> entities) - Save or update multiple members
- saveAndFlush(Member entity) - Save and immediately flush changes
### Find Operations
- indById(Long id) - Find member by ID, returns Optional<Member>
- indAll() - Get all members
- indAllById(Iterable<Long> ids) - Find members by multiple IDs
- indAll(Sort sort) - Get all members with sorting
- indAll(Pageable pageable) - Get members with pagination
### Delete Operations
- deleteById(Long id) - Delete member by ID
- delete(Member entity) - Delete a specific member
- deleteAll() - Delete all members
- deleteAll(Iterable<Member> entities) - Delete multiple members
### Count and Exists
- count() - Count total members
- existsById(Long id) - Check if member exists by ID
## Custom Query Methods
### 1. findByNameContainingIgnoreCase
```java
List<Member> findByNameContainingIgnoreCase(String name);
```
**Purpose**: Search members whose name contains the search term (partial match, case-insensitive)
**Parameters**:
- 
ame - The search term to look for in member names
**Returns**: List of members whose name contains the search term
**Example Usage**:
```java
// Find all members with "john" in their name (John, Johnny, Johnathan, etc.)
List<Member> members = memberRepository.findByNameContainingIgnoreCase("john");
```
**Generated SQL**:
```sql
SELECT * FROM members WHERE LOWER(name) LIKE LOWER('%john%');
```
### 2. findByPaymentStatus
```java
List<Member> findByPaymentStatus(String paymentStatus);
```
**Purpose**: Filter members by their payment status
**Parameters**:
- paymentStatus - The payment status to filter by (e.g., "Paid" or "Pending")
**Returns**: List of members with the specified payment status
**Example Usage**:
```java
// Get all members with pending payments
List<Member> pendingMembers = memberRepository.findByPaymentStatus("Pending");
// Get all members who have paid
List<Member> paidMembers = memberRepository.findByPaymentStatus("Paid");
```
**Generated SQL**:
```sql
SELECT * FROM members WHERE payment_status = 'Pending';
```
### 3. findByMembershipPlan (Bonus Method)
```java
List<Member> findByMembershipPlan(String membershipPlan);
```
**Purpose**: Filter members by their membership plan type
**Parameters**:
- membershipPlan - The plan type to filter by (e.g., "Monthly", "Quarterly", "Yearly")
**Returns**: List of members with the specified membership plan
**Example Usage**:
```java
// Get all members on monthly plan
List<Member> monthlyMembers = memberRepository.findByMembershipPlan("Monthly");
// Get all members on yearly plan
List<Member> yearlyMembers = memberRepository.findByMembershipPlan("Yearly");
```
### 4. findByNameIgnoreCase (Bonus Method)
```java
List<Member> findByNameIgnoreCase(String name);
```
**Purpose**: Find members by exact name match (case-insensitive)
**Parameters**:
- 
ame - The exact name to search for
**Returns**: List of members with the exact name
**Example Usage**:
```java
// Find members named exactly "John Doe" (case-insensitive)
List<Member> members = memberRepository.findByNameIgnoreCase("john doe");
```
### 5. existsByContactNumber (Bonus Method)
```java
boolean existsByContactNumber(String contactNumber);
```
**Purpose**: Check if a member with the given contact number already exists
**Parameters**:
- contactNumber - The contact number to check
**Returns**: 	rue if a member exists with this contact number, alse otherwise
**Example Usage**:
```java
// Check if contact number is already registered
String phone = "1234567890";
if (memberRepository.existsByContactNumber(phone)) {
    throw new RuntimeException("Contact number already exists!");
}
```
## Spring Data JPA Query Method Naming Convention
Spring Data JPA automatically implements these methods based on naming conventions:
| Keyword         | Example                              | JPQL Snippet                              |
|-----------------|--------------------------------------|-------------------------------------------|
| findBy          | findByName                           | ... where x.name = ?1                     |
| Containing      | findByNameContaining                 | ... where x.name like %?1%                |
| IgnoreCase      | findByNameIgnoreCase                 | ... where LOWER(x.name) = LOWER(?1)       |
| And             | findByNameAndStatus                  | ... where x.name = ?1 and x.status = ?2   |
| Or              | findByNameOrEmail                    | ... where x.name = ?1 or x.email = ?2     |
| Between         | findByStartDateBetween               | ... where x.startDate between ?1 and ?2   |
| LessThan        | findByIdLessThan                     | ... where x.id < ?1                       |
| GreaterThan     | findByIdGreaterThan                  | ... where x.id > ?1                       |
| Before          | findByStartDateBefore                | ... where x.startDate < ?1                |
| After           | findByStartDateAfter                 | ... where x.startDate > ?1                |
| IsNull          | findByEndDateIsNull                  | ... where x.endDate is null               |
| IsNotNull       | findByEndDateIsNotNull               | ... where x.endDate is not null           |
| OrderBy         | findByNameOrderByCreatedAtDesc       | ... order by x.createdAt desc             |
| existsBy        | existsByContactNumber                | select count(*) > 0                       |
## Complete Usage Example
```java
package com.gym.gym_membership_system.service;
import com.gym.gym_membership_system.model.Member;
import com.gym.gym_membership_system.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    // Create a new member
    public Member createMember(Member member) {
        // Check if contact number already exists
        if (memberRepository.existsByContactNumber(member.getContactNumber())) {
            throw new RuntimeException("Contact number already exists!");
        }
        return memberRepository.save(member);
    }
    // Get all members
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
    // Get member by ID
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }
    // Search members by name
    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }
    // Filter members by payment status
    public List<Member> getMembersByPaymentStatus(String status) {
        return memberRepository.findByPaymentStatus(status);
    }
    // Get members by membership plan
    public List<Member> getMembersByPlan(String plan) {
        return memberRepository.findByMembershipPlan(plan);
    }
    // Update member
    public Member updateMember(Long id, Member updatedMember) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        existingMember.setName(updatedMember.getName());
        existingMember.setContactNumber(updatedMember.getContactNumber());
        existingMember.setMembershipPlan(updatedMember.getMembershipPlan());
        existingMember.setStartDate(updatedMember.getStartDate());
        existingMember.setPaymentStatus(updatedMember.getPaymentStatus());
        return memberRepository.save(existingMember);
    }
    // Delete member
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
    // Get statistics
    public long getTotalMembers() {
        return memberRepository.count();
    }
    public long getPendingPaymentsCount() {
        return memberRepository.findByPaymentStatus("Pending").size();
    }
    public long getPaidMembersCount() {
        return memberRepository.findByPaymentStatus("Paid").size();
    }
}
```
## Testing the Repository
```java
package com.gym.gym_membership_system.repository;
import com.gym.gym_membership_system.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Test
    public void testFindByNameContainingIgnoreCase() {
        // Given
        Member member = Member.builder()
                .name("John Doe")
                .contactNumber("1234567890")
                .membershipPlan("Monthly")
                .startDate(LocalDate.now())
                .paymentStatus("Paid")
                .build();
        memberRepository.save(member);
        // When
        List<Member> result = memberRepository.findByNameContainingIgnoreCase("john");
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
    }
    @Test
    public void testFindByPaymentStatus() {
        // Given
        Member member1 = createMember("Member 1", "Paid");
        Member member2 = createMember("Member 2", "Pending");
        memberRepository.saveAll(List.of(member1, member2));
        // When
        List<Member> paidMembers = memberRepository.findByPaymentStatus("Paid");
        // Then
        assertThat(paidMembers).hasSize(1);
        assertThat(paidMembers.get(0).getPaymentStatus()).isEqualTo("Paid");
    }
    @Test
    public void testExistsByContactNumber() {
        // Given
        Member member = createMember("Test Member", "Paid");
        memberRepository.save(member);
        // When
        boolean exists = memberRepository.existsByContactNumber(member.getContactNumber());
        // Then
        assertThat(exists).isTrue();
    }
    private Member createMember(String name, String status) {
        return Member.builder()
                .name(name)
                .contactNumber("1234567890")
                .membershipPlan("Monthly")
                .startDate(LocalDate.now())
                .paymentStatus(status)
                .build();
    }
}
```
## Advanced Query Methods (Optional Extensions)
You can add more custom methods to the repository as needed:
```java
// Find members whose membership is expiring within days
List<Member> findByEndDateBetween(LocalDate start, LocalDate end);
// Find members by multiple criteria
List<Member> findByPaymentStatusAndMembershipPlan(String status, String plan);
// Find members with pagination
Page<Member> findByPaymentStatus(String status, Pageable pageable);
// Count members by status
long countByPaymentStatus(String status);
// Delete by payment status
void deleteByPaymentStatus(String status);
// Custom JPQL query
@Query("SELECT m FROM Member m WHERE m.endDate < :date AND m.paymentStatus = 'Pending'")
List<Member> findExpiredMembersWithPendingPayment(@Param("date") LocalDate date);
// Native SQL query
@Query(value = "SELECT * FROM members WHERE DATEDIFF(end_date, CURDATE()) <= 7", nativeQuery = true)
List<Member> findMembersExpiringInAWeek();
```
## Benefits of Using Spring Data JPA Repository
1. **No Implementation Required**: Methods are automatically implemented by Spring
2. **Type-Safe**: Compile-time checking of method signatures
3. **Consistent**: Follows standard naming conventions
4. **Less Code**: No need to write SQL or JPQL for simple queries
5. **Maintainable**: Easy to understand and modify
6. **Testable**: Easy to test with @DataJpaTest
7. **Flexible**: Can add custom queries when needed with @Query
## Configuration Required
Ensure your pplication.properties is configured:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/gym_db
spring.datasource.username=root
spring.datasource.password=your_password
# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
# Enable repository scanning
spring.data.jpa.repositories.enabled=true
```
---
**Created**: March 9, 2026  
**Status**: Production-ready  
**Next Step**: Create the Service layer to use this repository
