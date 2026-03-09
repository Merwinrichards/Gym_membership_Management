package com.gym.gym_membership_system.exception;

/**
 * Custom exception thrown when a requested resource (e.g., Member) is not found in the database.
 *
 * This exception extends RuntimeException, making it an unchecked exception that doesn't need
 * to be explicitly caught or declared in method signatures.
 *
 * Example usage:
 * <pre>
 * Member member = memberRepository.findById(id)
 *     .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
 * </pre>
 *
 * @author Gym Membership System
 * @since 1.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message explaining why the resource was not found
     * @param cause the cause of the exception (can be retrieved later by getCause())
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ResourceNotFoundException for a resource identified by type and ID.
     *
     * @param resourceName the name of the resource type (e.g., "Member")
     * @param fieldName the name of the field used to search (e.g., "id")
     * @param fieldValue the value that was searched for
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}

