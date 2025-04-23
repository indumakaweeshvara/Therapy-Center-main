package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapyProgramDto {
    private String programId;
    private String programName;
    private String duration;
    private BigDecimal programCost;
    private String programDescription;
}
