package com.printserver.server;

import com.printserver.ApplicationServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

// Connection to H2 DB is made in this class
public class DBConnection {
    private static final Logger LOGGER = LogManager.getLogger(DBConnection.class);
    public static boolean authUser(String username, String password) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:h2:file:./testdb", "sa", "");
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select hashkey from user_details WHERE name='" + username + "' limit 1;");
            while (rs.next()) {
                if (password.equals(rs.getString("hashkey"))) {
                    System.out.println(java.time.LocalDateTime.now() + "    User :" + username + " authenticated successfully!!");
                    LOGGER.info(java.time.LocalDateTime.now() + "    User :" + username + " authenticated successfully!!");
                    return true;
                }
                LOGGER.info(java.time.LocalDateTime.now() + "    User :" + username + " failed attempt.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

}
