package com.example.KFCC_Backend.DTO;

import com.example.KFCC_Backend.Enum.UserRoles;

public class UserRoleRequestDTO {
    Long userID;
    UserRoles role;

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public UserRoles getRole() {
        return role;
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }
}
