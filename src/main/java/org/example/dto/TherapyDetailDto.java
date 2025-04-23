package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapyDetailDto {
    private int therapistId;
    private String therapistName; // For display purposes tama use wun na save ekedi
    private String programId;
    private String programName; // For display purposes tama use wun na save ekedi
    private String note;
}
