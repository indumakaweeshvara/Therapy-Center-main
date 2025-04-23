package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TherapyDetailId {
    @Column(name = "therapist_id")
    private int therapistId;

    @Column(name = "program_id")
    private String programId;

}
