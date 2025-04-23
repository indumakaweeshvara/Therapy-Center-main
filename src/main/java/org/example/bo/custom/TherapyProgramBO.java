package org.example.bo.custom;

import org.example.bo.SuperBO;
import org.example.dto.TherapyProgramDto;

import java.sql.SQLException;
import java.util.List;

public interface TherapyProgramBO extends SuperBO {
    boolean exists(String programId) throws SQLException;
    boolean save(TherapyProgramDto programDto) throws Exception;
    List<TherapyProgramDto> getAll() throws Exception;
    boolean update(TherapyProgramDto programDto) throws SQLException;
    boolean deleteByPK(String programId) throws SQLException;
    public TherapyProgramDto getProgram(String programId) throws SQLException;

    List<TherapyProgramDto> getAllProgramOptions(String patientId);
}
