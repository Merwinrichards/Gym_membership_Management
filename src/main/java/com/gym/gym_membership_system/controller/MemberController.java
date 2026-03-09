package com.gym.gym_membership_system.controller;

import com.gym.gym_membership_system.model.Member;
import com.gym.gym_membership_system.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing gym members.
 *
 * Provides REST API endpoints for all member management operations including:
 * - Creating new members
 * - Retrieving members by various criteria
 * - Updating member information
 * - Deleting members
 * - Searching and filtering members
 *
 * Base path: /api/members
 *
 * @author Gym Membership System
 * @since 1.0
 */
@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * POST /api/members
     * Add a new member to the system.
     *
     * @param member the member object to be created (must include name, contactNumber, membershipPlan, startDate, paymentStatus)
     * @return ResponseEntity with the created member and HTTP 201 (Created) status
     * @throws IllegalArgumentException if validation fails or duplicate contact number
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addMember(@Valid @RequestBody Member member) {
        // Service validates and saves the member
        Member createdMember = memberService.addMember(member);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Member added successfully");
        response.put("data", createdMember);
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/members
     * Retrieve all members from the system.
     *
     * @return ResponseEntity with list of all members and HTTP 200 (OK) status
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Members retrieved successfully");
        response.put("count", members.size());
        response.put("data", members);
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/members/{id}
     * Retrieve a member by their ID.
     *
     * @param id the member ID
     * @return ResponseEntity with the member and HTTP 200 (OK) status
     * @throws ResourceNotFoundException if member not found
     * @throws IllegalArgumentException if ID is invalid
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMemberById(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Member retrieved successfully");
        response.put("data", member);
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/members/{id}
     * Update an existing member's information.
     *
     * @param id the member ID
     * @param member the member object containing updated information
     * @return ResponseEntity with the updated member and HTTP 200 (OK) status
     * @throws ResourceNotFoundException if member not found
     * @throws IllegalArgumentException if validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody Member member) {

        Member updatedMember = memberService.updateMember(id, member);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Member updated successfully");
        response.put("data", updatedMember);
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/members/{id}
     * Delete a member from the system.
     *
     * @param id the member ID
     * @return ResponseEntity with HTTP 204 (No Content) status
     * @throws ResourceNotFoundException if member not found
     * @throws IllegalArgumentException if ID is invalid
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Member deleted successfully");
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/members/search?name=
     * Search for members by name (partial match, case-insensitive).
     *
     * Query Parameters:
     * - name (required): The search term to look for in member names
     *
     * Example: GET /api/members/search?name=john
     *
     * @param name the search term
     * @return ResponseEntity with list of matching members and HTTP 200 (OK) status
     * @throws IllegalArgumentException if name is empty
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMembersByName(@RequestParam String name) {
        List<Member> members = memberService.searchMembersByName(name);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Members found with search term: " + name);
        response.put("count", members.size());
        response.put("searchTerm", name);
        response.put("data", members);
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/members/filter?status=
     * Filter members by payment status.
     *
     * Query Parameters:
     * - status (required): The payment status to filter by ("Paid" or "Pending")
     *
     * Example: GET /api/members/filter?status=Paid
     *
     * @param status the payment status ("Paid" or "Pending")
     * @return ResponseEntity with list of members matching the filter and HTTP 200 (OK) status
     * @throws IllegalArgumentException if status is invalid
     */
    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterMembersByPaymentStatus(@RequestParam String status) {
        List<Member> members = memberService.filterMembersByPaymentStatus(status);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Members filtered by payment status: " + status);
        response.put("count", members.size());
        response.put("paymentStatus", status);
        response.put("data", members);
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/members/stats/total
     * Get the total count of members in the system.
     * (Bonus endpoint for statistics)
     *
     * @return ResponseEntity with member count and HTTP 200 (OK) status
     */
    @GetMapping("/stats/total")
    public ResponseEntity<Map<String, Object>> getTotalMembersCount() {
        long totalCount = memberService.getTotalMembersCount();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Total members count retrieved");
        response.put("totalCount", totalCount);
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/members/plan?plan=
     * Get members by membership plan.
     * (Bonus endpoint)
     *
     * Query Parameters:
     * - plan (required): The membership plan ("Monthly", "Quarterly", or "Yearly")
     *
     * Example: GET /api/members/plan?plan=Monthly
     *
     * @param plan the membership plan
     * @return ResponseEntity with list of members with the specified plan and HTTP 200 (OK) status
     * @throws IllegalArgumentException if plan is invalid
     */
    @GetMapping("/plan")
    public ResponseEntity<Map<String, Object>> getMembersByMembershipPlan(@RequestParam String plan) {
        List<Member> members = memberService.getMembersByMembershipPlan(plan);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Members retrieved with membership plan: " + plan);
        response.put("count", members.size());
        response.put("membershipPlan", plan);
        response.put("data", members);
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for the API.
     *
     * @return ResponseEntity with API status and HTTP 200 (OK) status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Gym Membership Management API is running");
        response.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

