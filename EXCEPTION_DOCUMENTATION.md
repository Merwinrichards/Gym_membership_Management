# ResourceNotFoundException Documentation
## Overview
ResourceNotFoundException is a custom exception class used to signal that a requested resource (such as a Member) was not found in the database. It extends RuntimeException, making it an unchecked exception.
## Package
```java
package com.gym.gym_membership_system.exception;
```
## Class Declaration
```java
public class ResourceNotFoundException extends RuntimeException
```
## Why Use Custom Exceptions?
1. **Clear Intent**: Specifically indicates a resource was not found
2. **Better Error Handling**: Can be caught and handled differently from generic exceptions
3. **Cleaner Code**: Provides meaningful error messages
4. **REST API Ready**: Can be mapped to appropriate HTTP status codes (404 Not Found)
5. **Business Logic**: Separates business exceptions from system exceptions
## Constructors
### 1. Simple Message Constructor
```java
public ResourceNotFoundException(String message)
```
**Purpose**: Create exception with a custom message
**Parameters**:
- message - The detail message explaining why the resource was not found
**Example**:
```java
throw new ResourceNotFoundException("Member not found");
```
### 2. Message with Cause Constructor
```java
public ResourceNotFoundException(String message, Throwable cause)
```
**Purpose**: Create exception with a message and the underlying cause
**Parameters**:
- message - The detail message
- cause - The underlying exception that caused this exception
**Example**:
```java
try {
    // Database operation
} catch (SQLException e) {
    throw new ResourceNotFoundException("Failed to find member", e);
}
```
### 3. Structured Constructor (Recommended)
```java
public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue)
```
**Purpose**: Create exception with structured information about what was not found
**Parameters**:
- esourceName - The name of the resource type (e.g., "Member")
- ieldName - The field used for searching (e.g., "id", "contactNumber")
- ieldValue - The value that was searched for
**Generated Message Format**: "{resourceName} not found with {fieldName}: '{fieldValue}'"
**Example**:
```java
throw new ResourceNotFoundException("Member", "id", 123);
// Message: "Member not found with id: '123'"
throw new ResourceNotFoundException("Member", "contactNumber", "1234567890");
// Message: "Member not found with contactNumber: '1234567890'"
```
## Usage Examples
### Example 1: In Repository Layer (with Optional)
```java
import com.gym.gym_membership_system.exception.ResourceNotFoundException;
import com.gym.gym_membership_system.model.Member;
import com.gym.gym_membership_system.repository.MemberRepository;
@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    // Get member by ID
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
    }
    // Alternative with simple message
    public Member getMemberByIdAlt(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }
}
```
### Example 2: In Service Layer
```java
@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    public Member updateMember(Long id, Member updatedMember) {
        // Find existing member or throw exception
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        // Update fields
        existingMember.setName(updatedMember.getName());
        existingMember.setContactNumber(updatedMember.getContactNumber());
        existingMember.setMembershipPlan(updatedMember.getMembershipPlan());
        existingMember.setPaymentStatus(updatedMember.getPaymentStatus());
        return memberRepository.save(existingMember);
    }
    public void deleteMember(Long id) {
        // Check if member exists before deleting
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        memberRepository.delete(member);
    }
    public Member searchByContactNumber(String contactNumber) {
        List<Member> members = memberRepository.findByContactNumber(contactNumber);
        if (members.isEmpty()) {
            throw new ResourceNotFoundException("Member", "contactNumber", contactNumber);
        }
        return members.get(0);
    }
}
```
### Example 3: In Controller Layer with Exception Handling
```java
@RestController
@RequestMapping("/api/members")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        // If member not found, exception will be thrown
        Member member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id, 
            @RequestBody @Valid Member member) {
        Member updatedMember = memberService.updateMember(id, member);
        return ResponseEntity.ok(updatedMember);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
```
## Global Exception Handler
To properly handle this exception in a REST API, create a global exception handler:
```java
package com.gym.gym_membership_system.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
```
### Example Response (404 Not Found)
```json
{
    "timestamp": "2026-03-09T10:30:45",
    "status": 404,
    "error": "Not Found",
    "message": "Member not found with id: '123'",
    "path": "/api/members/123"
}
```
## Error Response DTO (Optional)
Create a structured error response:
```java
package com.gym.gym_membership_system.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
```
Then update the exception handler:
```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
        ResourceNotFoundException ex, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        "Not Found",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
}
```
## Testing the Exception
### Unit Test Example
```java
package com.gym.gym_membership_system.service;
import com.gym.gym_membership_system.exception.ResourceNotFoundException;
import com.gym.gym_membership_system.model.Member;
import com.gym.gym_membership_system.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberService memberService;
    @Test
    public void testGetMemberById_NotFound_ThrowsException() {
        // Given
        Long memberId = 999L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        // When & Then
        assertThatThrownBy(() -> memberService.getMemberById(memberId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id: '999'");
    }
}
```
### Integration Test Example
```java
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void testGetMemberById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/members/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found with id: '999'"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
```
## Best Practices
1. **Use Specific Messages**: Provide clear information about what was not found
   ```java
   // Good
   throw new ResourceNotFoundException("Member", "id", 123);
   // Avoid
   throw new ResourceNotFoundException("Not found");
   ```
2. **Use in Service Layer**: Throw exceptions in the service layer, not in controllers
   ```java
   // Service Layer (Good)
   public Member getMemberById(Long id) {
       return repository.findById(id)
           .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
   }
   ```
3. **Handle Globally**: Use @RestControllerAdvice for centralized exception handling
4. **Log Exceptions**: Add logging for debugging
   ```java
   @ExceptionHandler(ResourceNotFoundException.class)
   public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
           ResourceNotFoundException ex, WebRequest request) {
       log.warn("Resource not found: {}", ex.getMessage());
       // Return error response
   }
   ```
5. **Return Appropriate HTTP Status**: 404 Not Found for REST APIs
## Complete Service Layer Example
```java
package com.gym.gym_membership_system.service;
import com.gym.gym_membership_system.exception.ResourceNotFoundException;
import com.gym.gym_membership_system.model.Member;
import com.gym.gym_membership_system.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@Transactional
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
    }
    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }
    public List<Member> getMembersByPaymentStatus(String status) {
        return memberRepository.findByPaymentStatus(status);
    }
    public Member updateMember(Long id, Member updatedMember) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        existingMember.setName(updatedMember.getName());
        existingMember.setContactNumber(updatedMember.getContactNumber());
        existingMember.setMembershipPlan(updatedMember.getMembershipPlan());
        existingMember.setStartDate(updatedMember.getStartDate());
        existingMember.setPaymentStatus(updatedMember.getPaymentStatus());
        return memberRepository.save(existingMember);
    }
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        memberRepository.delete(member);
    }
}
```
## Summary
- **Type**: Unchecked exception (extends RuntimeException)
- **Purpose**: Signal that a requested resource was not found
- **Usage**: Thrown in service layer, handled in controller advice
- **HTTP Status**: Maps to 404 Not Found in REST APIs
- **Best Practice**: Use structured constructor for clear error messages
---
**Created**: March 9, 2026  
**Status**: Production-ready  
**Next Step**: Create GlobalExceptionHandler for centralized error handling
