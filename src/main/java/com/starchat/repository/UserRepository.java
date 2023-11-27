package com.starchat.repository;

import com.starchat.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {

    User getUserById(Long id);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    Long getUserIdByEmail(String email);

    void addUser(User user);
    void deleteUserById (Long id);
}

