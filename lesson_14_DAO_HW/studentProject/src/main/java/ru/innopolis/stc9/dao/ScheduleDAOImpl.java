package ru.innopolis.stc9.dao;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.connectionManager.ConnectionManager;
import ru.innopolis.stc9.connectionManager.ConnectionManagerJDBCImpl;
import ru.innopolis.stc9.pojo.Program;
import ru.innopolis.stc9.pojo.Schedule;
import ru.innopolis.stc9.pojo.Specialty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ScheduleDAOImpl implements ScheduleDAO {
    final static Logger logger = Logger.getLogger(ScheduleDAOImpl.class);
    private static ConnectionManager connectionManager = ConnectionManagerJDBCImpl.getInstance();

    @Override
    public boolean add(Schedule schedule) {
        logger.debug("object " + schedule.toString() + " is ready to be added to DB");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO schedules (program, lesson_serial_number, lesson_theme) VALUES (?, ?,?);");
            st.setInt(1, schedule.getProgram().getId());
            st.setInt(2, schedule.getLessonNumber());
            st.setString(3, schedule.getLessonTheme());
            st.executeUpdate();
            connection.close();
            logger.debug(schedule.toString() + " was successfully added to table programs");
            return true;
        } catch (SQLException e) {
            logger.error("failed insert " + schedule.toString() + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Schedule oldSchedule, Schedule newSchedule) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("UPDATE schedules " +
                    "SET program = ?, lesson_serial_number = ?, lesson_theme=? " +
                    "WHERE schedule_id=?;");
            st.setInt(1, newSchedule.getProgram().getId());
            st.setInt(2, newSchedule.getLessonNumber());
            st.setString(3, newSchedule.getLessonTheme());
            st.setInt(4, oldSchedule.getId());
            st.executeUpdate();
            connection.close();
            logger.info("Program " + oldSchedule.toString() + " was successfully updated to " + newSchedule.toString());
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Schedule schedule) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM schedules WHERE schedule_id=? CASCADE;");
            st.setInt(1, schedule.getId());
            st.executeUpdate();
            connection.close();
            logger.debug(schedule.toString() + " was deleted");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Schedule get(int id) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT\n" +
                            "p.program_id AS program_PK,\n" +
                            "p.specialty AS specialty_FK,\n" +
                            "s.specialty_name AS specialty,\n" +
                            "s.specialty_code AS abbreviation,\n" +
                            "p.approval_date AS date,\n" +
                            "sch.schedule_id AS schedule_PK,\n" +
                            "sch.lesson_serial_number AS lesson,\n" +
                            "sch.lesson_theme AS theme\n" +
                            "FROM programs p\n" +
                            "LEFT JOIN schedules sch\n" +
                            "ON p.program_id = sch.program\n" +
                            "INNER JOIN specialties s\n" +
                            "ON p.specialty=s.specialty_id\n" +
                            "WHERE sch.schedule_id=?"
            );
            st.setInt(1, id);
            ArrayList<Schedule> list = select(st, connection);
            if (list.size() == 1) {
                Schedule result = list.get(0);
                logger.debug(result.toString());
                return result;
            }
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        logger.debug("return NULL");
        return null;
    }

    @Override
    public ArrayList<Schedule> get(Program program) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT\n" +
                            "p.program_id AS program_PK,\n" +
                            "p.specialty AS specialty_FK,\n" +
                            "s.specialty_name AS specialty,\n" +
                            "s.specialty_code AS abbreviation,\n" +
                            "p.approval_date AS date,\n" +
                            "sch.schedule_id AS schedule_PK,\n" +
                            "sch.lesson_serial_number AS lesson,\n" +
                            "sch.lesson_theme AS theme\n" +
                            "FROM programs p\n" +
                            "LEFT JOIN schedules sch\n" +
                            "ON p.program_id = sch.program\n" +
                            "INNER JOIN specialties s\n" +
                            "ON p.specialty=s.specialty_id\n" +
                            "WHERE p.program_id=?"
            );
            st.setInt(1, program.getId());
            ArrayList<Schedule> list = select(st, connection);
            logger.debug("found " + list.size() + " objects of Schedule");
            return list;
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        logger.debug("return NULL");
        return null;
    }

    /**
     * Вариации выборки
     *
     * @param st
     * @param connection
     * @return
     */
    private ArrayList<Schedule> select(PreparedStatement st, Connection connection) {
        ArrayList<Schedule> list = new ArrayList<>();
        try {
            ResultSet resultSet = st.executeQuery();
            Schedule schedule;
            while (resultSet.next()) {
                schedule = new Schedule(
                        resultSet.getInt("schedule_PK"),
                        new Program(
                                resultSet.getInt("program_PK"),
                                new Specialty(
                                        resultSet.getInt("specialty_FK"),
                                        resultSet.getString("specialty"),
                                        resultSet.getString("abbreviation")
                                ),
                                resultSet.getDate("date").toLocalDate()
                        ),
                        resultSet.getInt("lesson"),
                        resultSet.getString("theme")
                );
                if (schedule != null) list.add(schedule);
            }
            connection.close();
            logger.info("found " + list.size() + " object(s) of Program");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return list;
    }
}
