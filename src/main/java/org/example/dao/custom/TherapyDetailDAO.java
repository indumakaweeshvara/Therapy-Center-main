package org.example.dao.custom;

import org.example.dao.CrudDAO;
import org.example.entity.TherapyDetail;
import org.example.entity.TherapyDetailId;

import java.sql.SQLException;


public interface TherapyDetailDAO extends CrudDAO<TherapyDetail, TherapyDetailId> {
    boolean saveWithReferences(TherapyDetail therapyDetail, int therapistId, String programId) throws SQLException;
}
