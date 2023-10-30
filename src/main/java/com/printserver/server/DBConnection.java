package com.printserver.server;

import java.sql.*;

public class DBConnection {
    public static boolean authUser(String username, String password) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:h2:file:./testdb", "sa", "");
        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("select hashkey from user_details WHERE name='" + username + "' limit 1;");
        while (rs.next()) {
            if (password.equals(rs.getString("hashkey"))) {
                System.out.println("User :" + username + " authenticated successfully!!");
                return true;
            }
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}
