package ru.innopolis.stc9.dao;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.connectionManager.ConnectionManager;
import ru.innopolis.stc9.connectionManager.ConnectionManagerJDBCImpl;
import ru.innopolis.stc9.pojo.Specialty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class SpecialtyDAOImpl implements SpecialtyDAO {
    final static Logger logger = Logger.getLogger(SpecialtyDAOImpl.class);
    private static ConnectionManager connectionManager = ConnectionManagerJDBCImpl.getInstance();

    /**
     * Добавляет в таблицу запись о новой специальности.
     * При этом id объекта значение не имеет.
     *
     * @param
     * @return specialty true при успешной вставке, иначе false
     */
    @Override
    public boolean add(Specialty specialty) {
        logger.debug("object " + specialty.toString() + " is ready to be added to DB");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO specialties (specialty_name, specialty_code) VALUES (?, ?);");
            st.setString(1, specialty.getName());
            st.setString(2, specialty.getAbbreviation());
            st.executeUpdate();
            connection.close();
            logger.debug(specialty.toString() + " was successfully added to table specialties");
            return true;
        } catch (SQLException e) {
            logger.error("failed insert " + specialty.toString() + " : " + e.getMessage());
            return false;
        }
    }

    /**
     * По id находит в таблице нужный объект или null (возвращает только один объект)
     *
     * @param id
     * @return
     */
    @Override
    public Specialty get(int id) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM specialties WHERE specialty_id=?;");
            st.setInt(1, id);
            ArrayList<Specialty> list = select(st, connection);
            if (list.size() == 1) {
                Specialty result = list.get(0);
                logger.debug(result.toString());
                return result;
            }
        } catch (SQLException e) {
            logger.debug(e.getMessage());
        }
        logger.debug("return NULL");
        return null;
    }

    /**
     * Возвращает список всех специальностей по их названию или шифру
     *
     * @param name
     * @return
     */
    @Override
    public Specialty get(String name) {
        logger.debug("start");
        if (name != null && !name.equals("")) {
            Connection connection = connectionManager.getConnection();
            try {
                PreparedStatement st = connection.prepareStatement("SELECT * FROM specialties WHERE specialty_name=? OR specialty_code=?;");
                st.setString(1, name);
                st.setString(2, name);
                ArrayList<Specialty> list = select(st, connection);
                logger.debug("found " + list.size() + " objects of Specialty");
                if (list.size() == 1) {
                    Specialty result = list.get(0);
                    logger.debug(result.toString());
                    return result;
                }
            } catch (SQLException e) {
                logger.debug(e.getMessage());
                return null;
            }
        }
        logger.debug("return NULL");
        return null;
    }

    /**
     * Возвращает список специальностей по ее названию и шифру
     *
     * @param specialtyName
     * @param specialtyCode
     * @return
     */
    @Override
    public ArrayList<Specialty> get(String specialtyName, String specialtyCode) {
        logger.debug("start");
        if (specialtyName != null && !specialtyName.equals("") && specialtyCode != null && !specialtyCode.equals("") && specialtyCode.length() <= 4) {
            Connection connection = connectionManager.getConnection();
            try {
                PreparedStatement st = connection.prepareStatement("SELECT * FROM specialties WHERE specialty_name=? AND specialty_code=?;");
                st.setString(1, specialtyName);
                st.setString(2, specialtyCode);
                return select(st, connection);
            } catch (SQLException e) {
                logger.debug(e.getMessage());
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean update(Specialty oldSpecialty, Specialty newSpecialty) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("UPDATE specialties " +
                    "SET specialty_name = ?, specialty_code = ? " +
                    "WHERE specialty_id=?;");
            st.setString(1, newSpecialty.getName());
            st.setString(2, newSpecialty.getAbbreviation());
            st.setInt(3, oldSpecialty.getId());
            st.executeUpdate();
            connection.close();
            logger.info("Specialty " + oldSpecialty.toString() + " was successfully updated to " + newSpecialty.toString());
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * удаление каскадом (по сути только по id)
     *
     * @param specialty
     * @return
     */
    @Override
    public boolean delete(Specialty specialty) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM specialties WHERE specialty_id=? CASCADE;");
            st.setInt(1, specialty.getId());
            st.executeUpdate();
            connection.close();
            logger.debug(specialty.toString() + " was deleted");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Вариации поиска
     *
     * @param st
     * @return
     */
    private ArrayList<Specialty> select(PreparedStatement st, Connection connection) {
        ArrayList<Specialty> list = new ArrayList<>();
        try {
            ResultSet resultSet = st.executeQuery();
            Specialty specialty;
            while (resultSet.next()) {
                specialty = new Specialty(
                        resultSet.getInt("specialty_id"),
                        resultSet.getString("specialty_name"),
                        resultSet.getString("specialty_code")
                );
                if (specialty != null) list.add(specialty);
            }
            connection.close();
            logger.info("found " + list.size() + " object(s) of Specialty");
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return list;
    }
}
