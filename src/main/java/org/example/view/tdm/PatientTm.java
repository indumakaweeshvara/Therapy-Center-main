package org.example.view.tdm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientTm {
    private int id;
    private String name;
    private String gender;
    private int age;
    private String phone;
    private String email;
    private int remainingSessions;
}
