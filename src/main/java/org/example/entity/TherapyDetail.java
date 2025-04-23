package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "therapy_detail")
public class TherapyDetail implements SuperEntity {
    @EmbeddedId
    private TherapyDetailId therapyDetailId;

    @ManyToOne
    @MapsId("therapistId")
    @JoinColumn(name = "therapist_id")
    private Therapist therapist;

    @ManyToOne
    @MapsId("programId")
    @JoinColumn(name = "program_id")
    private TherapyProgram program;

    @Lob
    private String note;

}
