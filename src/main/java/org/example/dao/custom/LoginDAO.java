package org.example.dao.custom;

import org.example.dao.CrudDAO;
import org.example.entity.Users;

public interface LoginDAO extends CrudDAO<Users,String> {
    boolean authenticate(String username, String password);
    String getRole();

    boolean isWrongPsw();

    Users getUser();
}
