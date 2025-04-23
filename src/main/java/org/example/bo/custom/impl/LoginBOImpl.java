package org.example.bo.custom.impl;

import org.example.bo.custom.LoginBO;
import org.example.dao.DAOFactory;
import org.example.dao.DAOTypes;
import org.example.dao.custom.LoginDAO;
import org.example.dto.UserDto;
import org.example.entity.Users;

public class LoginBOImpl implements LoginBO {
    LoginDAO loginDAO = DAOFactory.getInstance().getDAO(DAOTypes.LOGIN);

    @Override
    public boolean authenticate(String username, String password) {
        try {
            return loginDAO.authenticate(username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getRole() {
        return loginDAO.getRole();
    }

    @Override
    public boolean isWrongPsw() {
        return loginDAO.isWrongPsw();
    }

    @Override
    public UserDto getUser() {
        Users user = loginDAO.getUser();
        if(user != null){
            return new UserDto(user.getId(), user.getUsername(), user.getFullname(), user.getEmail(), user.getPassword(), user.getRole());
        }
        return null;
    }
}
