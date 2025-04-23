package org.example.bo.custom.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.bo.custom.UserBO;
import org.example.dao.DAOFactory;
import org.example.dao.DAOTypes;
import org.example.dao.custom.UserDAO;
import org.example.dto.UserDto;
import org.example.entity.Users;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserBOImpl implements UserBO {
    UserDAO userDAO = DAOFactory.getInstance().getDAO(DAOTypes.USER);

    @Override
    public List<UserDto> getAllReceptionist() throws Exception {
        List<Users> allReceptionist = userDAO.getAllReceptionist();
        List<UserDto> userDtos = new ArrayList<>();

        for (Users user : allReceptionist) {
            UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getFullname(), user.getEmail(), user.getPassword(), user.getRole());
            userDtos.add(userDto);
        }
        return userDtos;
    }

    @Override
    public boolean save(UserDto userDto) throws Exception {
        Users user = new Users();
        user.setUsername(userDto.getUsername());
        user.setFullname(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPassword(BCrypt.withDefaults().hashToString(12, userDto.getPassword().toCharArray()));
        user.setRole(userDto.getRole());
        return userDAO.save(user);
    }

    @Override
    public boolean update(UserDto userDto) throws SQLException {
        Users user = new Users();

        user.setId(userDto.getId()); // ID is important for finding the correct record
        user.setUsername(userDto.getUsername());
        user.setFullname(userDto.getFullName());
        user.setEmail(userDto.getEmail());

        // Only hash and set the password if userDto.getPassword() is not null or empty
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            String hashedPassword = BCrypt.withDefaults().hashToString(12, userDto.getPassword().toCharArray());
            user.setPassword(hashedPassword);
        } else {
            user.setPassword(null); // Let DAO handle skipping update if password is null
        }

        return userDAO.update(user); // Calls your updated DAO method
    }

    @Override
    public boolean deleteByPK(String cusId) {
        try{
            return userDAO.deleteByPK(cusId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
