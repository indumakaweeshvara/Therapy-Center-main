package org.example.dao.custom;

import org.example.dao.CrudDAO;
import org.example.entity.Users;

import java.sql.SQLException;
import java.util.List;

public interface UserDAO extends CrudDAO<Users,String> {
    List<Users> getAllReceptionist() throws Exception;

}
