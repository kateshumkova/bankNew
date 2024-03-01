package com.example.banknew.entities;

import com.example.banknew.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity(name="schedule")
public class InterestRatePaymentScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
  // @Column(name = "account_id")
  // private Long accountId;

    @Column(name = "date_of_payment")
    private LocalDate dateOfPayment;

    @PositiveOrZero(message = "Interest rate must be greater than 0")
    @Column(name = "interest_amount")
    private BigDecimal interestAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus paymentStatus;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

}

