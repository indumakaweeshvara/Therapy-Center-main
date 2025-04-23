package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientDto {
    private int id;
    private String name;
    private String gender;
    private int age;
    private String address;
    private String phone;
    private String email;
    private int userId;
    private int remainingSessions;

    // Registration data
    private String programId;
    private int sessionCount;
    private String registrationDate;
    private String registrationTime;

    // Payment data
    private String paymentType;
    private BigDecimal amount;
    private BigDecimal balancePayment;
    private String paymentDate;
    private String paymentTime;
}
