package com.starchat.model.mappers;

import com.intersystems.jdbc.IRISResultSet;
import com.starchat.model.User;
import com.starchat.model.UserRoles;

import java.sql.SQLException;

public class UserMapper {
    public static User map(IRISResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        rs.getString("user_role");
        user.setRole(UserRoles.getByValue(rs.getInt("user_role")));
        user.setPassword(rs.getString("password"));
        user.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdated_at(rs.getTimestamp("updated_at").toLocalDateTime());
        return user;
    }
}
