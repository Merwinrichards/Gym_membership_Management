package com.gym.gym_membership_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Contact number cannot be empty")
    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @NotBlank(message = "Membership plan cannot be empty")
    @Column(name = "membership_plan", nullable = false)
    private String membershipPlan;

    @NotNull(message = "Start date cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotBlank(message = "Payment status cannot be empty")
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        // set created time
        this.createdAt = LocalDateTime.now();

        // calculate end date automatically
        if (startDate != null && membershipPlan != null) {

            switch (membershipPlan.toLowerCase()) {
                case "monthly":
                    endDate = startDate.plusMonths(1);
                    break;

                case "quarterly":
                    endDate = startDate.plusMonths(3);
                    break;

                case "yearly":
                    endDate = startDate.plusYears(1);
                    break;

                default:
                    endDate = startDate.plusMonths(1);
            }
        }
    }
}