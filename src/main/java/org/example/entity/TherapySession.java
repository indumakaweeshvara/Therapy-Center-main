package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "therapy_session")
public class TherapySession implements SuperEntity {
    @EmbeddedId
    private TherapySessionId therapySessionId;

    @ManyToOne
    @MapsId("patientId")
    @JoinColumn(name = "patient_id")
    private Patients patient;

    @ManyToOne
    @MapsId("therapistId")
    @JoinColumn(name = "therapist_id")
    private Therapist therapist;

    private String date;
    private String time;
    private String status;

    @Column(name = "session_note")
    @Lob
    private String sessionNote;
}
