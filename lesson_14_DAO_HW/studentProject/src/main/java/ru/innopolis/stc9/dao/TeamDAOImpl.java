package ru.innopolis.stc9.dao;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.connectionManager.ConnectionManager;
import ru.innopolis.stc9.connectionManager.ConnectionManagerJDBCImpl;
import ru.innopolis.stc9.pojo.Program;
import ru.innopolis.stc9.pojo.Specialty;
import ru.innopolis.stc9.pojo.Team;

import java.sql.*;
import java.util.ArrayList;

public class TeamDAOImpl implements TeamDAO {
    final static Logger logger = Logger.getLogger(TeamDAOImpl.class);
    private static ConnectionManager connectionManager = ConnectionManagerJDBCImpl.getInstance();
    @Override
    public boolean add(Team team) {
        logger.debug("object " + team.toString() + " is ready to be added to DB");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO teams (start_training, end_training, group_number, program) VALUES (?, ?, ?, ?);");
            st.setDate(1, Date.valueOf(team.getStart()));
            st.setDate(2, Date.valueOf(team.getStart()));
            st.setInt(3, team.getStream());
            st.setInt(4, team.getProgram().getId());
            st.executeUpdate();
            connection.close();
            logger.debug(team.toString() + " was successfully added to table programs");
            return true;
        } catch (SQLException e) {
            logger.error("failed insert " + team.toString() + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Team oldTeam, Team newTeam) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("UPDATE teams " +
                    "SET start_training = ?, end_training = ?, group_number = ?, program = ? " +
                    "WHERE team_id=?;");
            st.setDate(1, Date.valueOf(newTeam.getStart()));
            st.setDate(2, Date.valueOf(newTeam.getFinish()));
            st.setInt(3, newTeam.getStream());
            st.setInt(4, newTeam.getProgram().getId());
            st.setInt(5, oldTeam.getId());
            st.executeUpdate();
            connection.close();
            logger.info("Program " + oldTeam.toString() + " was successfully updated to " + newTeam.toString());
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Team team) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM teams WHERE team_id=? CASCADE;");
            st.setInt(1, team.getId());
            st.executeUpdate();
            connection.close();
            logger.debug(team.toString() + " was deleted");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Team get(int id) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT\n" +
                            "  t.team_id,\n" +
                            "  t.start_training,\n" +
                            "  t.end_training,\n" +
                            "  t.group_number,\n" +
                            "  p.program_id,\n" +
                            "  s.specialty_id,\n" +
                            "  s.specialty_name,\n" +
                            "  s.specialty_code,\n" +
                            "  p.approval_date\n" +
                            "FROM teams t\n" +
                            "INNER JOIN programs p ON t.program = p.program_id\n" +
                            "INNER JOIN specialties s ON p.specialty = s.specialty_id\n" +
                            "WHERE t.team_id = ?;"
            );
            st.setInt(1, id);
            ArrayList<Team> list = select(st, connection);
            if (list.size()==1) {
                Team result = list.get(0);
                logger.debug("found " + list.size() + " object of Team "+result);
                return result;
            }
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        logger.debug("return NULL");
        return null;
    }

    @Override
    public ArrayList<Team> get(Program program) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT\n" +
                            "  t.team_id,\n" +
                            "  t.start_training,\n" +
                            "  t.end_training,\n" +
                            "  t.group_number,\n" +
                            "  p.program_id,\n" +
                            "  s.specialty_id,\n" +
                            "  s.specialty_name,\n" +
                            "  s.specialty_code,\n" +
                            "  p.approval_date\n" +
                            "FROM teams t\n" +
                            "INNER JOIN programs p ON t.program = p.program_id\n" +
                            "INNER JOIN specialties s ON p.specialty = s.specialty_id\n" +
                            "WHERE p.program_id = ?;"
            );
            st.setInt(1, program.getId());
            ArrayList<Team> list = select(st, connection);
            logger.debug("found " + list.size() + " objects of Program");
            return list;
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        logger.debug("return NULL");
        return null;
    }
    private ArrayList<Team> select(PreparedStatement st, Connection connection) {
        ArrayList<Team> list = new ArrayList<>();
        try {
            ResultSet resultSet = st.executeQuery();
            Team team;
            while (resultSet.next()) {
                team = new Team(
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
                );
                if (team != null) list.add(team);
            }
            connection.close();
            logger.info("found " + list.size() + " object(s) of Team");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return list;
    }
}
