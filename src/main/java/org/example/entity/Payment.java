package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name = "payment")
public class Payment implements SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    private String paymentType;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_payment", precision = 10, scale = 2)
    private BigDecimal balancePayment;

    private String date;
    private String time;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patients patient;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private TherapyProgram program;
}
