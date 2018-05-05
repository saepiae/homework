package ru.innopolis.stc9.dao;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.connectionManager.ConnectionManager;
import ru.innopolis.stc9.connectionManager.ConnectionManagerJDBCImpl;
import ru.innopolis.stc9.pojo.Role;
import ru.innopolis.stc9.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAOImpl implements UserDAO {
    final static Logger logger = Logger.getLogger(UserDAOImpl.class);
    private static ConnectionManager connectionManager = ConnectionManagerJDBCImpl.getInstance();

    @Override
    public boolean add(User user) {
        logger.debug("object " + user.toString() + " is ready to be added to DB");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO users (user_login, user_password) VALUES (?,?);");
            st.setString(1, user.getLogin());
            st.setString(2, user.getPassword());
            st.executeUpdate();
            connection.close();
            logger.debug(user.toString() + " was successfully added to table programs");
            return true;
        } catch (SQLException e) {
            logger.error("failed insert " + user.toString() + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(User oldUser, User newUser) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("UPDATE users " +
                    "SET user_login=?, user_password=?" +
                    "WHERE user_login=?;");
            st.setString(1, newUser.getLogin());
            st.setString(2, newUser.getPassword());
            st.setString(3, oldUser.getLogin());
            st.executeUpdate();
            connection.close();
            logger.info("User " + oldUser.toString() + " was successfully updated to " + newUser.toString());
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(User user) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM users WHERE user_login=?;");
            st.setString(1, user.getLogin());
            st.executeUpdate();
            connection.close();
            logger.debug(user.toString() + " was deleted");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public User get(String login, String password) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT s.user_login, s.user_password, p.relname  " +
                            "FROM users s, pg_class p " +
                            "WHERE user_login = ? AND user_password=? AND s.tableoid = p.oid;"
            );
            st.setString(1, login);
            st.setString(2, password);
            ResultSet resultSet = st.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = new User(
                        resultSet.getString("user_login"),
                        resultSet.getString("user_password"),
                        findRole(resultSet.getString("relname"))
                );
            }
            connection.close();
            if (user != null) {
                logger.info("found " + user.toString() + " object of User ");
            } else {
                logger.debug("return NULL");
            }
            return user;
        } catch (SQLException e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList allWithRole(Role role) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        ArrayList<User> list = new ArrayList<>();

        String table = null;
        switch (role.ordinal()) {
            case (1):
                table = "students";
                break;
            case (2):
                table = "teachers";
                break;
            case (3):
                table = "users";
                break;
        }
        if (table != null) {
            try {
                PreparedStatement st = connection.prepareStatement(
                        "SELECT s.user_login, s.user_password  FROM ONLY "+table+" s, pg_class p WHERE s.tableoid = p.oid;"
                );
                ResultSet resultSet = st.executeQuery();
                while (resultSet.next()) {
                    User user = new User(
                            resultSet.getString("user_login"),
                            resultSet.getString("user_password"),
                            role
                    );
                    list.add(user);
                }
                connection.close();
            } catch (SQLException e) {
                logger.debug(e.getMessage());
                return null;
            }
        }
        return list;
    }

    private Role findRole(String s) {
        if (s != null) {
            switch (s) {
                case ("users"):
                    return Role.ADMIN;
                case ("students"):
                    return Role.STUDENT;
                case ("teachers"):
                    return Role.TEACHER;
            }
        }
        return Role.NO_SUCH_USER;
    }
}
