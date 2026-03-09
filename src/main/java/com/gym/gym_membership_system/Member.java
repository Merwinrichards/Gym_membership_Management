package com.gym.gym_membership_system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Member {
    private String id;
    private String name;
    private String contactNumber;
    private MembershipPlan membershipPlan;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;

    public enum MembershipPlan {
        MONTHLY(1, "Monthly"),
        QUARTERLY(3, "Quarterly"),
        YEARLY(12, "Yearly");

        private final int months;
        private final String displayName;

        MembershipPlan(int months, String displayName) {
            this.months = months;
            this.displayName = displayName;
        }

        public int getMonths() {
            return months;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PaymentStatus {
        PAID("Paid"),
        PENDING("Pending");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructor
    public Member(String id, String name, String contactNumber, MembershipPlan membershipPlan,
                  LocalDateTime startDate, PaymentStatus paymentStatus) {
        this.id = id;
        this.name = name;
        this.contactNumber = contactNumber;
        this.membershipPlan = membershipPlan;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(membershipPlan.getMonths());
        this.paymentStatus = paymentStatus;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public MembershipPlan getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(MembershipPlan membershipPlan) {
        this.membershipPlan = membershipPlan;
        // Recalculate end date when plan changes
        this.endDate = this.startDate.plusMonths(membershipPlan.getMonths());
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        // Recalculate end date when start date changes
        this.endDate = startDate.plusMonths(membershipPlan.getMonths());
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "========================================================================\n" +
                "| Member ID         : %-50s |\n" +
                "| Name              : %-50s |\n" +
                "| Contact Number    : %-50s |\n" +
                "| Membership Plan   : %-50s |\n" +
                "| Start Date        : %-50s |\n" +
                "| End Date          : %-50s |\n" +
                "| Payment Status    : %-50s |\n" +
                "| Created At        : %-50s |\n" +
                "========================================================================",
                id, name, contactNumber, membershipPlan.getDisplayName(),
                startDate.format(formatter), endDate.format(formatter),
                paymentStatus.getDisplayName(), createdAt.format(formatter)
        );
    }

    public String toTableRow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("| %-10s | %-20s | %-15s | %-12s | %-12s | %-12s | %-10s |",
                id, name, contactNumber, membershipPlan.getDisplayName(),
                startDate.format(formatter), endDate.format(formatter),
                paymentStatus.getDisplayName());
    }
}

