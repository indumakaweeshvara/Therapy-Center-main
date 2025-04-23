package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapySessionDto {
    private int patientId;
    private int therapistId;
    private String date;
    private String time;
    private String status;
    private String sessionNote;
    private String patientName;
    private String therapistName;
}
