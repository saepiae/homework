package ru.innopolis.stc9.connectionManager;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManagerJDBCImpl implements ConnectionManager{
    final static Logger logger = Logger.getLogger(ConnectionManager.class);

    private static ConnectionManager connectionManager;

    public static ConnectionManager getInstance(){
        if (connectionManager == null){
            connectionManager = new ConnectionManagerJDBCImpl();
            logger.info("create new ConnectionManagerJDBCImpl");
        }
        return connectionManager;
    }

    private ConnectionManagerJDBCImpl() {
        logger.info("constructor of ConnectionManagerJDBCImpl");
    }

    @Override
    public Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/own",
                    "postgres",
                    "123456789"        );
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        logger.debug("success of BD connection.");
        return con;
    }
}
