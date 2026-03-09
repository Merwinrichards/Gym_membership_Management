package com.gym.gym_membership_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Contact number cannot be empty")
    @Column(nullable = false)
    private String contactNumber;

    @NotBlank(message = "Membership plan cannot be empty")
    @Column(nullable = false)
    private String membershipPlan; // Monthly, Quarterly, Yearly

    @NotNull(message = "Start date cannot be null")
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @NotBlank(message = "Payment status cannot be empty")
    @Column(nullable = false)
    private String paymentStatus; // Paid, Pending

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void calculateEndDate() {
        if (startDate != null && membershipPlan != null) {
            switch (membershipPlan.toLowerCase()) {
                case "monthly":
                    this.endDate = startDate.plusMonths(1);
                    break;
                case "quarterly":
                    this.endDate = startDate.plusMonths(3);
                    break;
                case "yearly":
                    this.endDate = startDate.plusMonths(12);
                    break;
                default:
                    this.endDate = startDate.plusMonths(1);
            }
        }
    }
}

