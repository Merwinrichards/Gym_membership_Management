package com.gym.gym_membership_system.repository;

import com.gym.gym_membership_system.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Find members by name containing the search term (case-insensitive)
     * @param name the search term
     * @return list of members whose name contains the search term
     */
    List<Member> findByNameContainingIgnoreCase(String name);

    /**
     * Find members by payment status
     * @param paymentStatus the payment status to filter by (e.g., "Paid" or "Pending")
     * @return list of members with the specified payment status
     */
    List<Member> findByPaymentStatus(String paymentStatus);

    /**
     * Find members by membership plan
     * @param membershipPlan the membership plan to filter by (e.g., "Monthly", "Quarterly", "Yearly")
     * @return list of members with the specified membership plan
     */
    List<Member> findByMembershipPlan(String membershipPlan);

    /**
     * Find members by exact name match (case-insensitive)
     * @param name the exact name to search for
     * @return list of members with the exact name
     */
    List<Member> findByNameIgnoreCase(String name);

    /**
     * Check if a member exists by contact number
     * @param contactNumber the contact number to check
     * @return true if member exists, false otherwise
     */
    boolean existsByContactNumber(String contactNumber);
}

