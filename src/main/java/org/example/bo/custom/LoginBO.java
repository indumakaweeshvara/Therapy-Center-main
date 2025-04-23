package org.example.bo.custom;

import org.example.bo.SuperBO;
import org.example.dto.UserDto;

public interface LoginBO extends SuperBO {
    boolean authenticate(String username, String password);
    String getRole();

    boolean isWrongPsw();

    UserDto getUser();
}
