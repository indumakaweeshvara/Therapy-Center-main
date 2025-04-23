package org.example.view.tdm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentTm {
    private int paymentId;
    private String paymentType;
    private BigDecimal amount;
    private String date;
    private String patientName;
    private String programName;
}


