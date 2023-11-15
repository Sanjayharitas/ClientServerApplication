package com.printserver.server;

import com.printserver.models.Role;
import com.printserver.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Connection to H2 DB is made in this class
public class DBConnection {
    private static final Logger LOGGER = LogManager.getLogger(DBConnection.class);

    public static String getPW(String username) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:h2:file:./testdb", "sa", "");
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select hashkey from user_details WHERE name='" + username + "' limit 1;");
            while (rs.next()) {
                return rs.getString("hashkey");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static String getRole(String username){
        Connection conn = null;
        try{
            conn = DriverManager.getConnection("jdbc:h2:file:./testdb", "sa", "");

            String query = "SELECT roles.role_name " +
                    "FROM user_roles " +
                    "JOIN roles ON user_roles.role_id = roles.role_id " +
                    "WHERE user_roles.user_id = (SELECT id FROM user_details WHERE name = ? LIMIT 1);";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("role_name");
                    }
                }
            }
        }catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static User getUser(String username){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = DriverManager.getConnection("jdbc:h2:file:./testdb", "sa", "");

            String query = "SELECT u.*, r.role_name " +
                    "FROM user_details u " +
                    "JOIN user_roles ur ON u.id = ur.user_id " +
                    "JOIN roles r ON ur.role_id = r.role_id " +
                    "WHERE u.name = ?";

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);

            rs = pstmt.executeQuery();

            if (rs.next()){
                User user = new User();
                user.id = rs.getInt("id");
                user.username = rs.getString("name");
                user.role = rs.getString("role_name");
                user.password = "";
                user.dob = "";

                return user;
            }
        }catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static List<User> getUsers(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            List<User> usersList = new ArrayList<>();
            conn = DriverManager.getConnection("jdbc:h2:file:./testdb", "sa", "");

            String query = "SELECT u.*, r.role_name " +
                    "FROM user_details u " +
                    "JOIN user_roles ur ON u.id = ur.user_id " +
                    "JOIN roles r ON ur.role_id = r.role_id ";

            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

                while (rs.next()) {
                    User user = new User();
                    user.id = rs.getInt("id");
                    user.username = rs.getString("name");
                    user.role = rs.getString("role_name");

                    usersList.add(user);
                }

                return usersList;

        }catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static List<Role> getRolesList(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            List<Role> roles = new ArrayList<>();
            conn = DriverManager.getConnection("jdbc:h2:file:./testdb", "sa", "");

            String query = "SELECT r.* " +
                    "FROM roles r ";

            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
                while (rs.next()) {
                    Role role = new Role();
                    role.role_id = rs.getInt("role_id");
                    role.role_name = rs.getString("role_name");

                    roles.add(role);
                }
                return roles;

        }catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void insertUser(User user){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = DriverManager.getConnection("jdbc:h2:file:./testdb", "sa", "");


            String maxIdQuery = "SELECT MAX(id) AS max_id FROM user_details";
            pstmt = conn.prepareStatement(maxIdQuery);
            rs = pstmt.executeQuery();

            int newUserId = 8;
            if (rs.next()) {
                newUserId = rs.getInt("max_id") + 1;
            }

            String insertQuery = "INSERT INTO user_details (id, name, hashkey, dob) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.setInt(1, newUserId);
            pstmt.setString(2, user.username);
            pstmt.setString(3, user.password);
            pstmt.setString(4, user.dob);;
            int rowsAffected = pstmt.executeUpdate();

            String selectRoleQuery = "SELECT role_id FROM roles WHERE role_name = ?";
            pstmt = conn.prepareStatement(selectRoleQuery);
            pstmt.setString(1, user.role);
            rs = pstmt.executeQuery();

            int roleId = 4;
            if (rs.next()) {
                roleId = rs.getInt("role_id");
            }
                String insertUserRoleQuery = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
                pstmt = conn.prepareStatement(insertUserRoleQuery);
                pstmt.setInt(1, newUserId);
                pstmt.setInt(2, roleId);
                pstmt.executeUpdate();


        }catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void deleteUser(String username){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = DriverManager.getConnection("jdbc:h2:file:./testdb", "sa", "");

            String fetchUserQuery = "SELECT * FROM user_details WHERE name = ?";
            pstmt = conn.prepareStatement(fetchUserQuery);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            User user=new User();
            if (rs.next()) {
                user.id = rs.getInt("id");
            }

            String deleteRoleQuery = "DELETE FROM user_roles WHERE user_id = ?";
            pstmt = conn.prepareStatement(deleteRoleQuery);
            pstmt.setInt(1,user.id);
            pstmt.executeUpdate();

            String deleteUserQuery = "DELETE FROM user_details where id = ?";
            pstmt = conn.prepareStatement(deleteUserQuery);
            pstmt.setInt(1,user.id);
            pstmt.executeUpdate();


        }catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
