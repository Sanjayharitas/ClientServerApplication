package com.printserver.server;

import com.printserver.interfaces.IUserService;
import com.printserver.models.Role;
import com.printserver.models.User;

import java.util.List;

public class UserService implements IUserService {

    @Override
    public void register(User user) {
        DBConnection.insertUser(user);
    }

    @Override
    public void delete(String username) {
        DBConnection.deleteUser(username);
    }

    @Override
    public void updateRole(int userId, String newRole) {
        DBConnection.updateRole(userId, newRole);
    }

    @Override
    public User getUser(String username) {
        return DBConnection.getUser(username);
    }

    @Override
    public List<User> getUsers() {
        return DBConnection.getUsers();
    }

    @Override
    public List<Role> getRolesList() {
        return DBConnection.getRolesList();
    }

}
