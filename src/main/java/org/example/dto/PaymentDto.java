package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDto {
    private int paymentId;
    private String paymentType;
    private BigDecimal amount;
    private BigDecimal balancePayment;
    private String date;
    private String time;
    private int patientId;
    private String programId;
    private String patientName;
    private String programName;
}
