package org.example.view.tdm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapySessionTm {
    private int patientId;
    private int therapistId;
    private String patientName;
    private String therapistName;
    private String date;
    private String time;
    private String status;
    private String sessionNote;
}
