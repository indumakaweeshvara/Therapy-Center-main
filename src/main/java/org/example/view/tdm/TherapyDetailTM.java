package org.example.view.tdm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapyDetailTM {
    private String therapist;  // Format: "ID - Name"
    private String program;    // Format: "ID - Name"
    private String note;
}
