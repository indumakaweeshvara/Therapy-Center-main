package org.example.dao.custom;

import org.example.dao.CrudDAO;
import org.example.entity.TherapyProgram;

import java.sql.SQLException;

public interface TherapyProgramDAO extends CrudDAO<TherapyProgram,String> {
    boolean exists(String programId) throws SQLException;
    TherapyProgram getProgram(String programId) throws SQLException;
}
