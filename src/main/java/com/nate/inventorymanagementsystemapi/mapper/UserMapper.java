package com.nate.inventorymanagementsystemapi.mapper;

import com.nate.inventorymanagementsystemapi.dto.UserDto;
import com.nate.inventorymanagementsystemapi.model.User;

public class UserMapper {

    public static UserDto toDto(User user){
        if(user == null){
            return null;
        }

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }


}
