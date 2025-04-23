package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RegistrationId {
    @Column(name = "program_id")
    private String programId;

    @Column(name="patient_id")
    private int patientId;

}
