package com.rev.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseConnection {
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                java.util.Properties props = new java.util.Properties();
                java.io.InputStream input = null;

                input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties");

                if (input == null) {
                    input = DatabaseConnection.class.getClassLoader().getResourceAsStream("main/resources/db.properties");
                }

                if (input == null) {
                    try {
                        input = new java.io.FileInputStream("src/main/resources/db.properties");
                    } catch (java.io.FileNotFoundException ignored) {
                        try {
                            input = new java.io.FileInputStream("resources/db.properties");
                        } catch (java.io.FileNotFoundException ignored2) {
                        }
                    }
                }

                if (input == null) {
                    logger.error("CRITICAL ERROR: db.properties not found in classpath or file system.");
                    return null;
                }

                try {
                    props.load(input);
                } finally {
                    input.close();
                }

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.username");
                String password = props.getProperty("db.password");
                connection = DriverManager.getConnection(url, user, password);
                logger.info("Database connected");
            }
        } catch (SQLException | java.io.IOException e) {
            logger.error("Database connection failed", e);
            return null;
        }
        return connection;
    }
}
