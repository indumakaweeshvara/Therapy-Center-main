package org.example.dao.custom;

import org.example.dao.SuperDAO;
import org.example.entity.Therapist;

import java.util.List;

public interface QueryDAO extends SuperDAO {
    List<Therapist> getTherapistsByPatientId(String patientId) throws Exception;
}
