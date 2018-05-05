package ru.innopolis.stc9.dao;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.connectionManager.ConnectionManager;
import ru.innopolis.stc9.connectionManager.ConnectionManagerJDBCImpl;
import ru.innopolis.stc9.pojo.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LessonDAOImpl implements LessonDAO {
    final static Logger logger = Logger.getLogger(LessonDAOImpl.class);
    private static ConnectionManager connectionManager = ConnectionManagerJDBCImpl.getInstance();

    @Override
    public boolean add(Lesson lesson) {
        logger.debug("object " + lesson.toString() + " is ready to be added to DB");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO lessons " +
                    "(team, teacher, schedule, lesson_date, homework, comment) " +
                    "VALUES (?,?,?,?,?,?);");
            st.setInt(1, lesson.getTeam().getId());
            st.setString(2, lesson.getTeacher().getUser().getLogin());
            st.setInt(3, lesson.getSchedule().getId());
            st.setDate(4, Date.valueOf(lesson.getLocalDate()));
            st.setString(5, lesson.getHomework());
            st.setString(6, lesson.getComment());
            st.executeUpdate();
            connection.close();
            logger.debug(lesson.toString() + " was successfully added to table programs");
            return true;
        } catch (SQLException e) {
            logger.error("failed insert " + lesson.toString() + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Lesson oldLesson, Lesson newLesson) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("UPDATE lessons " +
                    "SET team=?, teacher=?, schedule=?, lesson_date=?, homework=?, comment=? " +
                    "WHERE lesson_id=?;");
            st.setInt(1, newLesson.getTeam().getId());
            st.setString(2, newLesson.getTeacher().getUser().getLogin());
            st.setInt(3, newLesson.getSchedule().getId());
            st.setDate(4, Date.valueOf(newLesson.getLocalDate()));
            st.setString(5, newLesson.getHomework());
            st.setString(6, newLesson.getComment());
            st.setInt(7, oldLesson.getId());
            st.executeUpdate();
            connection.close();
            logger.info("User " + oldLesson.toString() + " was successfully updated to " + newLesson.toString());
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Lesson lesson) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM lessons " +
                    "WHERE lesson_id=?;");
            st.setInt(1, lesson.getId());
            st.executeUpdate();
            connection.close();
            logger.debug(lesson.toString() + " was deleted");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Lesson get(int id) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        StringBuilder s = new StringBuilder(getSql());
        ArrayList<Lesson> list;
        s.append(" where l.lesson_id = ?;");
        try {
            PreparedStatement st = connection.prepareStatement(s.toString());
            st.setInt(1, id);
            list = select(connection, st);
            if (list.size()==1) {
                return list.get(0);
            }
        } catch (SQLException e) {
            logger.debug(e.getMessage());
            return null;
        }
        return null;
    }

    @Override
    public ArrayList<Lesson> get(Teacher teacher) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        StringBuilder s = new StringBuilder(getSql());
        s.append(" where teach.user_login = ?;");
        try {
            PreparedStatement st = connection.prepareStatement(s.toString());
            st.setString(1, teacher.getUser().getLogin());
            return select(connection, st);
        } catch (SQLException e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<Lesson> get(Team team) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        StringBuilder s = new StringBuilder(getSql());
        s.append(" where tm.team_id = ?;");
        try {
            PreparedStatement st = connection.prepareStatement(s.toString());
            st.setInt(1, team.getId());
            return select(connection, st);
        } catch (SQLException e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    /**
     * Чтобы каждый раз не писать эту громадную строку, запишем заготовочку тут.
     *
     * @return
     */
    private String getSql() {
        StringBuilder s = new StringBuilder("SELECT ");
        s.append(" l.lesson_id, ");
        s.append(" tm.team_id, ");
        s.append(" tm.start_training, ");
        s.append(" tm.end_training, ");
        s.append(" tm.group_number, ");
        s.append(" p.program_id, ");
        s.append(" sp.specialty_id, ");
        s.append(" sp.specialty_name, ");
        s.append(" sp.specialty_code, ");
        s.append(" p.approval_date, ");
        s.append(" teach.user_login, ");
        s.append(" teach.user_password, ");
        s.append(" teach.first_name, ");
        s.append(" teach.second_name, ");
        s.append(" teach.patronymic, ");
        s.append(" teach.birthday, ");
        s.append(" teach.user_passport, ");
        s.append(" teach.adress, ");
        s.append(" teach.phone, ");
        s.append(" teach.skype, ");
        s.append(" teach.telegram, ");
        s.append(" sch.schedule_id, ");
        s.append(" sch.lesson_serial_number, ");
        s.append(" sch.lesson_theme, ");
        s.append(" l.lesson_date, ");
        s.append(" l.homework, ");
        s.append(" l.comment ");
        s.append(" FROM lessons l ");
        s.append(" INNER JOIN teams tm ON l.team = tm.team_id ");
        s.append(" INNER JOIN programs p ON tm.program = p.program_id ");
        s.append(" INNER JOIN specialties sp ON p.specialty = sp.specialty_id ");
        s.append(" INNER JOIN teachers teach ON l.teacher = teach.user_login ");
        // TODO: 04.05.2018 Проверить INNER LEFT/RIGHT
        s.append(" INNER JOIN schedules sch ON l.schedule = sch.schedule_id");
        return s.toString();
    }

    private ArrayList<Lesson> select(Connection connection, PreparedStatement st) throws SQLException {
        ArrayList<Lesson> list = new ArrayList<>();
        ResultSet resultSet = st.executeQuery();
        while (resultSet.next()) {
            Lesson lesson = null;
            Team team = null;
            Program program = null;
            Specialty specialty = null;
            Teacher teacher = null;
            Schedule schedule = null;
            specialty = new Specialty(
                    resultSet.getInt("specialty_id"),
                    resultSet.getString("specialty_name"),
                    resultSet.getString("specialty_code")
            );
            program = new Program(
                    resultSet.getInt("program_id"),
                    specialty,
                    resultSet.getDate("approval_date").toLocalDate()
            );
            team = new Team(
                    resultSet.getInt("team_id"),
                    resultSet.getDate("start_training").toLocalDate(),
                    resultSet.getDate("end_training").toLocalDate(),
                    resultSet.getInt("group_number"),
                    program
            );
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
            schedule = new Schedule(
                    resultSet.getInt("schedule_id"),
                    program,
                    resultSet.getInt("lesson_serial_number"),
                    resultSet.getString("lesson_theme")
            );

            lesson = new Lesson(
                    resultSet.getInt("lesson_id"),
                    team,
                    teacher,
                    schedule,
                    resultSet.getDate("lesson_date").toLocalDate(),
                    resultSet.getString("homework"),
                    resultSet.getString("comment")
            );
            if (lesson != null) {
                list.add(lesson);
            }
        }
        connection.close();
        return list;
    }
}
