package org.example.view.tdm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapistTM {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
}
