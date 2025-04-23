package org.example.bo.custom;

import org.example.bo.SuperBO;
import org.example.dto.UserDto;

import java.sql.SQLException;
import java.util.List;

public interface UserBO extends SuperBO {
    List<UserDto> getAllReceptionist() throws Exception;

    boolean save(UserDto userDto) throws Exception;

    boolean update(UserDto userdto) throws SQLException;

    boolean deleteByPK(String cusId);
}
