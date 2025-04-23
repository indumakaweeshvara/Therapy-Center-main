package org.example.bo.custom;

import org.example.bo.SuperBO;
import org.example.dto.TherapyDetailDto;

import java.sql.SQLException;
import java.util.List;

public interface TherapyDetailBO extends SuperBO {
    boolean save(TherapyDetailDto therapyDetailDto) throws Exception;
    List<TherapyDetailDto> getAll() throws Exception;
    boolean update(TherapyDetailDto therapyDetailDto) throws SQLException;
    boolean deleteByPK(int therapistId, String programId) throws SQLException;
}
