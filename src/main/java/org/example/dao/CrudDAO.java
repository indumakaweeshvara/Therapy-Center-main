package org.example.dao;

import org.example.entity.SuperEntity;

import java.sql.SQLException;
import java.util.List;

public interface CrudDAO <T extends SuperEntity,ID> extends SuperDAO{
    List<T> getAll() throws Exception;
    boolean save(T t) throws SQLException;
    boolean update(T t) throws SQLException;
    boolean deleteByPK(ID pk) throws SQLException;

}
