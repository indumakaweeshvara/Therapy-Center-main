package org.example.bo.custom;

import org.example.bo.SuperBO;
import org.example.dto.PatientDto;
import org.example.view.tdm.PatientTm;

import java.sql.SQLException;
import java.util.List;

public interface PatientBO extends SuperBO {
    String getNextPatientId() throws SQLException;
    boolean savePatient(PatientDto patientDTO) throws SQLException;
    List<PatientTm> getAllPatients() throws SQLException;
    PatientDto getPatientById(int id) throws SQLException;
    boolean deletePatient(int id) throws SQLException;
    boolean updatePatient(PatientDto patientDTO) throws SQLException;

    int getRemainingSessions(int patientId);
}
