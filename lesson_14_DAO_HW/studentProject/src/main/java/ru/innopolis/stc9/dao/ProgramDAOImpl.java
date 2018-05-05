package ru.innopolis.stc9.dao;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.connectionManager.ConnectionManager;
import ru.innopolis.stc9.connectionManager.ConnectionManagerJDBCImpl;
import ru.innopolis.stc9.pojo.Program;
import ru.innopolis.stc9.pojo.Specialty;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import static ru.innopolis.stc9.Tools.parseToDate;

// TODO: 27.04.2018 реализовать все методы интерфейса
public class ProgramDAOImpl implements ProgramDAO {
    final static Logger logger = Logger.getLogger(ProgramDAOImpl.class);
    private static ConnectionManager connectionManager = ConnectionManagerJDBCImpl.getInstance();

    @Override
    public boolean add(Program program) {
        logger.debug("object " + program.toString() + " is ready to be added to DB");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO programs (specialty, approval_date) VALUES (?, ?);");
            st.setInt(1, program.getSpecialty().getId());
            st.setDate(2, Date.valueOf(program.getApprovalDate()));
            st.executeUpdate();
            connection.close();
            logger.debug(program.toString() + " was successfully added to table programs");
            return true;
        } catch (SQLException e) {
            logger.error("failed insert " + program.toString() + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public Program get(int id) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT p.program_id AS program," +
                            " s.specialty_id AS spec_id," +
                            " s.specialty_name AS name," +
                            " s.specialty_code AS abbr," +
                            " p.approval_date AS date" +
                            " FROM programs p INNER JOIN specialties s " +
                            "ON p.specialty = s.specialty_id AND p.program_id=?;"
            );
            st.setInt(1, id);
            ArrayList<Program> list = select(st, connection);
            if (list.size() == 1) {
                Program result = list.get(0);
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
    public ArrayList<Program> get(LocalDate date) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT p.program_id AS program," +
                            " s.specialty_id AS spec_id," +
                            " s.specialty_name AS name," +
                            " s.specialty_code AS abbr," +
                            " p.approval_date AS date" +
                            " FROM programs p INNER JOIN specialties s " +
                            "ON p.specialty = s.specialty_id AND p.approval_date=?;"
            );
            st.setDate(1, Date.valueOf(date));
            ArrayList<Program> list = select(st, connection);
            logger.debug("found " + list.size() + " objects of Program");
            return list;
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        logger.debug("return NULL");
        return null;
    }

    @Override
    public ArrayList<Program> get(Specialty specialty) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT p.program_id AS program," +
                            " s.specialty_id AS spec_id," +
                            " s.specialty_name AS name," +
                            " s.specialty_code AS abbr," +
                            " p.approval_date AS date " +
                            "FROM programs p INNER JOIN specialties s " +
                            "ON p.specialty = s.specialty_id " +
                            "AND s.specialty_id=? AND s.specialty_name=? AND s.specialty_code=?;"
            );
            st.setInt(1, specialty.getId());
            st.setString(2, specialty.getName());
            st.setString(3, specialty.getAbbreviation());

            ArrayList<Program> list = select(st, connection);
            logger.debug("found " + list.size() + " objects of Program");
            return list;
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        logger.debug("return NULL");
        return null;
    }

    @Override
    public boolean update(Program oldProgram, Program newProgram) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("UPDATE programs " +
                    "SET specialty = ?, approval_date = ? " +
                    "WHERE program_id=?;");
            st.setInt(1, newProgram.getSpecialty().getId());
            st.setDate(2, Date.valueOf(newProgram.getApprovalDate()));
            st.setInt(3, oldProgram.getId());
            st.executeUpdate();
            connection.close();
            logger.info("Program " + oldProgram.toString() + " was successfully updated to " + newProgram.toString());
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Program program) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM specialties WHERE specialty_id=? CASCADE;");
            st.setInt(1, program.getId());
            st.executeUpdate();
            connection.close();
            logger.debug(program.toString() + " was deleted");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Вариации выборки
     *
     * @param st
     * @param connection
     * @return
     */
    private ArrayList<Program> select(PreparedStatement st, Connection connection) {
        ArrayList<Program> list = new ArrayList<>();
        try {
            ResultSet resultSet = st.executeQuery();
            Program program;
            while (resultSet.next()) {
                program = new Program(
                        resultSet.getInt("program"),
                        new Specialty(
                                resultSet.getInt("spec_id"),
                                resultSet.getString("name"),
                                resultSet.getString("abbr")
                        ),
                        resultSet.getDate("date").toLocalDate()
                );
                if (program != null) list.add(program);
            }
            connection.close();
            logger.info("found " + list.size() + " object(s) of Program");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return list;
    }
}
