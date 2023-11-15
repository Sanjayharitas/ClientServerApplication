package com.printserver.interfaces;

import com.printserver.models.Role;
import com.printserver.models.User;

import java.util.List;

public interface IUserService {
    void register(User user);
    void delete(String username);
    void updateRole(int userId, String newRole);
    User getUser(String username);
    List<User> getUsers();
    List<Role> getRolesList();
}
