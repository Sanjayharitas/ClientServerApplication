package com.printserver.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

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

}
