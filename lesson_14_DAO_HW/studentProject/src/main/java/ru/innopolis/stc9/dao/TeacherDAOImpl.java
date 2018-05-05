package ru.innopolis.stc9.dao;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.connectionManager.ConnectionManager;
import ru.innopolis.stc9.connectionManager.ConnectionManagerJDBCImpl;
import ru.innopolis.stc9.pojo.Teacher;
import ru.innopolis.stc9.pojo.User;

import java.sql.*;
import java.time.LocalDate;

public class TeacherDAOImpl implements TeacherDAO {
    final static Logger logger = Logger.getLogger(TeacherDAOImpl.class);
    private static ConnectionManager connectionManager = ConnectionManagerJDBCImpl.getInstance();

    @Override
    public boolean add(Teacher teacher) {
        logger.debug("object " + teacher.toString() + " is ready to be added to DB");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO teachers (user_login, user_password," +
                    " first_name, second_name, patronymic, birthday, user_passport, adress," +
                    " phone, skype, telegram) VALUES (?,?,?,?,? ,?,? ,?,? ,?,?);");
            st.setString(1, teacher.getUser().getLogin());
            st.setString(2, teacher.getUser().getPassword());
            st.setString(3, teacher.getFirstName());
            st.setString(4, teacher.getLastName());
            st.setString(5, teacher.getPatronymic());
            if (teacher.getBirthday() != null) {
                st.setDate(6, Date.valueOf(teacher.getBirthday()));
            } else {
                st.setDate(6, null);
            }
            st.setInt(7, teacher.getPassport());
            st.setString(8, teacher.getAdress());
            st.setInt(9, teacher.getPhone());
            st.setString(10, teacher.getSkype());
            st.setString(11, teacher.getTelegram());
            st.executeUpdate();
            connection.close();
            logger.debug(teacher.toString() + " was successfully added to table programs");
            return true;
        } catch (SQLException e) {
            logger.error("failed insert " + teacher.toString() + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Teacher oldTeacher, Teacher newTeacher) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("UPDATE ONLY teachers " +
                    "SET user_login=?, user_password=?, first_name=?, second_name=?, patronymic=?," +
                    " birthday=?, user_passport=?, adress=?, phone=?, skype=?, telegram=? " +
                    "WHERE user_login=?;");
            st.setString(1, newTeacher.getUser().getLogin());
            st.setString(2, newTeacher.getUser().getPassword());
            st.setString(3, newTeacher.getFirstName());
            st.setString(4, newTeacher.getLastName());
            st.setString(5, newTeacher.getPatronymic());
            if (newTeacher.getBirthday() != null) {
                st.setDate(6, Date.valueOf(newTeacher.getBirthday()));
            } else {
                st.setDate(6, null);
            }
            st.setInt(7, newTeacher.getPassport());
            st.setString(8, newTeacher.getAdress());
            st.setInt(9, newTeacher.getPhone());
            st.setString(10, newTeacher.getSkype());
            st.setString(11, newTeacher.getTelegram());
            st.setString(12, oldTeacher.getUser().getLogin());
            st.executeUpdate();
            connection.close();
            logger.info("User " + oldTeacher.toString() + " was successfully updated to " + newTeacher.toString());
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Teacher teacher) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM ONLY teachers WHERE user_login=?;");
            st.setString(1, teacher.getUser().getLogin());
            st.executeUpdate();
            connection.close();
            logger.debug(teacher.toString() + " was deleted");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Teacher get(User user) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT * FROM teachers WHERE user_login= ? AND user_password=?;"
            );
            st.setString(1, user.getLogin());
            st.setString(2, user.getPassword());
            ResultSet resultSet = st.executeQuery();
            Teacher teacher = null;
            if (resultSet.next()) {
                LocalDate birthday = null;
                try {
                    birthday = resultSet.getDate("birthday").toLocalDate();
                } catch (NullPointerException e) {
                    logger.debug(e.getMessage());
                }
                teacher = new Teacher(
                        new User(
                        resultSet.getString("user_login"),
                        resultSet.getString("user_password")
                        ),
                        resultSet.getString("first_name"),
                        resultSet.getString("second_name"),
                        resultSet.getString("patronymic"),
                        birthday,
                        resultSet.getInt("user_passport"),
                        resultSet.getString("adress"),
                        resultSet.getInt("phone"),
                        resultSet.getString("skype"),
                        resultSet.getString("telegram")
                );
            }
            connection.close();
            if (teacher != null) {
                logger.info("found " + teacher.toString() + " object of Teacher ");
            } else {
                logger.debug("return NULL");
            }
            return teacher;
        } catch (SQLException e) {
            logger.debug(e.getMessage());
            return null;
        }
    }
}
