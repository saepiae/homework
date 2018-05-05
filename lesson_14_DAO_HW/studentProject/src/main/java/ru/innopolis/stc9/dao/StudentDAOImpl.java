package ru.innopolis.stc9.dao;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.connectionManager.ConnectionManager;
import ru.innopolis.stc9.connectionManager.ConnectionManagerJDBCImpl;
import ru.innopolis.stc9.pojo.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class StudentDAOImpl implements StudentDAO {
    final static Logger logger = Logger.getLogger(StudentDAOImpl.class);
    private static ConnectionManager connectionManager = ConnectionManagerJDBCImpl.getInstance();

    @Override
    public boolean add(Student student) {
        logger.debug("object " + student.toString() + " is ready to be added to DB");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO students (user_login, user_password," +
                    " first_name, second_name, patronymic, birthday, user_passport, adress," +
                    " phone, skype, telegram, team_id) VALUES (?,?,?,?,? ,?,? ,?,? ,?,?,?);");
            st.setString(1, student.getUser().getLogin());
            st.setString(2, student.getUser().getPassword());
            st.setString(3, student.getFirstName());
            st.setString(4, student.getLastName());
            st.setString(5, student.getPatronymic());
            if (student.getBirthday() != null) {
                st.setDate(6, Date.valueOf(student.getBirthday()));
            } else {
                st.setDate(6, null);
            }
            st.setInt(7, student.getPassport());
            st.setString(8, student.getAdress());
            st.setInt(9, student.getPhone());
            st.setString(10, student.getSkype());
            st.setString(11, student.getTelegram());
            st.setInt(12, student.getTeam().getId());
            st.executeUpdate();
            connection.close();
            logger.debug(student.toString() + " was successfully added to table programs");
            return true;
        } catch (SQLException e) {
            logger.error("failed insert " + student.toString() + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Student oldStudent, Student newStudent) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("UPDATE ONLY students " +
                    "SET user_login=?, user_password=?, first_name=?, second_name=?, patronymic=?," +
                    " birthday=?, user_passport=?, adress=?, phone=?, skype=?, telegram=?,team_id=? " +
                    "WHERE user_login=?;");
            st.setString(1, newStudent.getUser().getLogin());
            st.setString(2, newStudent.getUser().getPassword());
            st.setString(3, newStudent.getFirstName());
            st.setString(4, newStudent.getLastName());
            st.setString(5, newStudent.getPatronymic());
            if (newStudent.getBirthday() != null) {
                st.setDate(6, Date.valueOf(newStudent.getBirthday()));
            } else {
                st.setDate(6, null);
            }
            st.setInt(7, newStudent.getPassport());
            st.setString(8, newStudent.getAdress());
            st.setInt(9, newStudent.getPhone());
            st.setString(10, newStudent.getSkype());
            st.setString(11, newStudent.getTelegram());
            st.setString(12, newStudent.getTelegram());
            st.setString(13, oldStudent.getUser().getLogin());
            st.executeUpdate();
            connection.close();
            logger.info("Student " + oldStudent.toString() + " was successfully updated to " + newStudent.toString());
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Student student) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM ONLY students WHERE user_login=?;");
            st.setString(1, student.getUser().getLogin());
            st.executeUpdate();
            connection.close();
            logger.debug(student.toString() + " was deleted");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Student get(User user) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT" +
                            "  s.user_login, " +
                            "  s.user_password, " +
                            "  s.first_name, " +
                            "  s.second_name, " +
                            "  s.patronymic, " +
                            "  s.birthday, " +
                            "  s.user_passport, " +
                            "  s.adress, " +
                            "  s.phone, " +
                            "  s.skype, " +
                            "  s.telegram, " +
                            "  t.team_id, " +
                            "  t.start_training, " +
                            "  t.end_training, " +
                            "  t.group_number, " +
                            "  p.program_id, " +
                            "  sp.specialty_id, " +
                            "  sp.specialty_name, " +
                            "  sp.specialty_code, " +
                            "  p.approval_date " +
                            "  FROM students s " +
                            "INNER JOIN  teams t ON s.team_id = t.team_id " +
                            "INNER JOIN programs p ON t.program = p.program_id " +
                            "INNER JOIN specialties sp ON p.specialty = sp.specialty_id " +
                            "WHERE s.user_login=?;"
            );
            st.setString(1, user.getLogin());
            ResultSet resultSet = st.executeQuery();
            Student student = null;
            if (resultSet.next()) {
                LocalDate birthday = null;
                try {
                    birthday = resultSet.getDate("birthday").toLocalDate();
                } catch (NullPointerException e) {
                    logger.debug(e.getMessage());
                }
                student = new Student(
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
                        resultSet.getString("telegram"),
                        new Team(
                                resultSet.getInt("team_id"),
                                resultSet.getDate("start_training").toLocalDate(),
                                resultSet.getDate("end_training").toLocalDate(),
                                resultSet.getInt("group_number"),
                                new Program(
                                        resultSet.getInt("program_id"),
                                        new Specialty(
                                                resultSet.getInt("specialty_id"),
                                                resultSet.getString("specialty_name"),
                                                resultSet.getString("specialty_code")
                                        ),
                                        resultSet.getDate("approval_date").toLocalDate()
                                )
                        ));
            }
            connection.close();
            logger.info("found " + student.toString() + " object of Student");
            return student;
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        logger.debug("return NULL");
        return null;
    }

    @Override
    public ArrayList<Student> get(Team team) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        ArrayList<Student> list = new ArrayList<>();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT * FROM ONLY students WHERE team_id=?;"
            );
            st.setInt(1, team.getId());
            ResultSet resultSet = st.executeQuery();
            Student student = null;
            while (resultSet.next()) {
                LocalDate birthday = null;
                try {
                    birthday = resultSet.getDate("birthday").toLocalDate();
                } catch (NullPointerException e) {
                    logger.debug(e.getMessage());
                }
                student = new Student(
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
                        resultSet.getString("telegram"),
                        team);
                list.add(student);
            }
            connection.close();
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        logger.info("found " + list.size() + " object(s) of Student");
        return list;
    }
}

