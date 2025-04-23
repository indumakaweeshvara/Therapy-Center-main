package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapistDto {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
}
