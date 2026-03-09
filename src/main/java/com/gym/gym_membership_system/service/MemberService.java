package com.gym.gym_membership_system.service;

import com.gym.gym_membership_system.exception.ResourceNotFoundException;
import com.gym.gym_membership_system.model.Member;
import com.gym.gym_membership_system.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing gym members.
 *
 * This class provides business logic for member operations including:
 * - Creating new members
 * - Retrieving members by various criteria
 * - Updating member information
 * - Deleting members
 * - Searching and filtering members
 *
 * All database operations are wrapped in transactions for data consistency.
 *
 * @author Gym Membership System
 * @since 1.0
 */
@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Add a new member to the system.
     *
     * Creates a new member record in the database. The createdAt timestamp is automatically
     * set by the JPA entity's @CreationTimestamp annotation. The endDate is automatically
     * calculated based on the membership plan.
     *
     * @param member the member object to be added (name, contactNumber, membershipPlan, startDate, and paymentStatus must be set)
     * @return the saved member with generated ID and timestamps
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public Member addMember(Member member) {
        // Validation of required fields
        if (member == null) {
            throw new IllegalArgumentException("Member object cannot be null");
        }

        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty");
        }

        if (member.getContactNumber() == null || member.getContactNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact number cannot be empty");
        }

        if (member.getMembershipPlan() == null || member.getMembershipPlan().trim().isEmpty()) {
            throw new IllegalArgumentException("Membership plan cannot be empty");
        }

        if (member.getStartDate() == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }

        // Check if contact number already exists (optional but recommended for data integrity)
        if (memberRepository.existsByContactNumber(member.getContactNumber())) {
            throw new IllegalArgumentException("Contact number already exists: " + member.getContactNumber());
        }

        // Save and return the new member
        return memberRepository.save(member);
    }

    /**
     * Retrieve all members from the system.
     *
     * @return list of all members in the database, or empty list if no members exist
     */
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     * Get a member by their ID.
     *
     * @param id the member ID
     * @return the member if found
     * @throws ResourceNotFoundException if member with the given ID is not found
     */
    public Member getMemberById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive number");
        }

        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
    }

    /**
     * Update an existing member's information.
     *
     * Retrieves the member by ID and updates their fields with the provided values.
     * The createdAt timestamp is not modified (it's marked as updatable=false in the entity).
     * The endDate will be recalculated if the membershipPlan is changed.
     *
     * @param id the ID of the member to update
     * @param updatedMember the member object containing updated information
     * @return the updated member
     * @throws ResourceNotFoundException if member with the given ID is not found
     * @throws IllegalArgumentException if required fields are missing
     */
    public Member updateMember(Long id, Member updatedMember) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive number");
        }

        if (updatedMember == null) {
            throw new IllegalArgumentException("Member object cannot be null");
        }

        // Retrieve existing member or throw exception
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));

        // Update fields if provided (not null)
        if (updatedMember.getName() != null && !updatedMember.getName().trim().isEmpty()) {
            existingMember.setName(updatedMember.getName());
        } else {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (updatedMember.getContactNumber() != null && !updatedMember.getContactNumber().trim().isEmpty()) {
            // Check if the new contact number is already used by another member
            String newContactNumber = updatedMember.getContactNumber();
            if (!existingMember.getContactNumber().equals(newContactNumber) &&
                    memberRepository.existsByContactNumber(newContactNumber)) {
                throw new IllegalArgumentException("Contact number already exists: " + newContactNumber);
            }
            existingMember.setContactNumber(newContactNumber);
        } else {
            throw new IllegalArgumentException("Contact number cannot be empty");
        }

        if (updatedMember.getMembershipPlan() != null && !updatedMember.getMembershipPlan().trim().isEmpty()) {
            existingMember.setMembershipPlan(updatedMember.getMembershipPlan());
        } else {
            throw new IllegalArgumentException("Membership plan cannot be empty");
        }

        if (updatedMember.getStartDate() != null) {
            existingMember.setStartDate(updatedMember.getStartDate());
        }

        if (updatedMember.getPaymentStatus() != null && !updatedMember.getPaymentStatus().trim().isEmpty()) {
            existingMember.setPaymentStatus(updatedMember.getPaymentStatus());
        }

        // Save and return the updated member
        return memberRepository.save(existingMember);
    }

    /**
     * Delete a member from the system.
     *
     * @param id the ID of the member to delete
     * @throws ResourceNotFoundException if member with the given ID is not found
     * @throws IllegalArgumentException if ID is invalid
     */
    public void deleteMember(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive number");
        }

        // Check if member exists before attempting to delete
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member", "id", id);
        }

        memberRepository.deleteById(id);
    }

    /**
     * Search for members by name (partial match, case-insensitive).
     *
     * Searches for members whose name contains the provided search term.
     *
     * @param name the search term to look for in member names
     * @return list of members matching the search criteria, or empty list if none found
     * @throws IllegalArgumentException if name is null or empty
     */
    public List<Member> searchMembersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search name cannot be empty");
        }

        return memberRepository.findByNameContainingIgnoreCase(name.trim());
    }

    /**
     * Filter members by their payment status.
     *
     * Retrieves all members with a specific payment status (e.g., "Paid" or "Pending").
     *
     * @param paymentStatus the payment status to filter by (e.g., "Paid" or "Pending")
     * @return list of members with the specified payment status, or empty list if none found
     * @throws IllegalArgumentException if paymentStatus is null or empty
     */
    public List<Member> filterMembersByPaymentStatus(String paymentStatus) {
        if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment status cannot be empty");
        }

        // Validate payment status
        String status = paymentStatus.trim();
        if (!status.equals("Paid") && !status.equals("Pending")) {
            throw new IllegalArgumentException("Invalid payment status. Allowed values: 'Paid', 'Pending'");
        }

        return memberRepository.findByPaymentStatus(status);
    }

    /**
     * Get the total count of members in the system.
     *
     * @return the total number of members
     */
    public long getTotalMembersCount() {
        return memberRepository.count();
    }

    /**
     * Get members by membership plan.
     *
     * Retrieves all members with a specific membership plan.
     *
     * @param membershipPlan the membership plan to filter by (e.g., "Monthly", "Quarterly", "Yearly")
     * @return list of members with the specified plan, or empty list if none found
     * @throws IllegalArgumentException if membershipPlan is null or empty
     */
    public List<Member> getMembersByMembershipPlan(String membershipPlan) {
        if (membershipPlan == null || membershipPlan.trim().isEmpty()) {
            throw new IllegalArgumentException("Membership plan cannot be empty");
        }

        // Validate membership plan
        String plan = membershipPlan.trim();
        if (!plan.equals("Monthly") && !plan.equals("Quarterly") && !plan.equals("Yearly")) {
            throw new IllegalArgumentException("Invalid membership plan. Allowed values: 'Monthly', 'Quarterly', 'Yearly'");
        }

        return memberRepository.findByMembershipPlan(plan);
    }

    /**
     * Get statistics about members with a specific payment status.
     *
     * @param paymentStatus the payment status to count (e.g., "Paid" or "Pending")
     * @return the count of members with the specified status
     * @throws IllegalArgumentException if paymentStatus is null or empty
     */
    public long getCountByPaymentStatus(String paymentStatus) {
        if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment status cannot be empty");
        }

        return memberRepository.findByPaymentStatus(paymentStatus).size();
    }

    /**
     * Check if a member exists by ID.
     *
     * @param id the member ID
     * @return true if member exists, false otherwise
     * @throws IllegalArgumentException if ID is invalid
     */
    public boolean memberExists(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive number");
        }

        return memberRepository.existsById(id);
    }

    /**
     * Check if a contact number is already registered.
     *
     * Useful for validating contact numbers before adding or updating members.
     *
     * @param contactNumber the contact number to check
     * @return true if contact number already exists, false otherwise
     * @throws IllegalArgumentException if contactNumber is null or empty
     */
    public boolean contactNumberExists(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact number cannot be empty");
        }

        return memberRepository.existsByContactNumber(contactNumber.trim());
    }
}


