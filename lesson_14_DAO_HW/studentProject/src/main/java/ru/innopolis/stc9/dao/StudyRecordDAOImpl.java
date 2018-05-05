package ru.innopolis.stc9.dao;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.connectionManager.ConnectionManager;
import ru.innopolis.stc9.connectionManager.ConnectionManagerJDBCImpl;
import ru.innopolis.stc9.pojo.Lesson;
import ru.innopolis.stc9.pojo.Student;
import ru.innopolis.stc9.pojo.StudyRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by ich on 04.05.2018.
 */
public class StudyRecordDAOImpl implements StudyRecordDAO {
    final static Logger logger = Logger.getLogger(StudyRecordDAOImpl.class);
    private static ConnectionManager connectionManager = ConnectionManagerJDBCImpl.getInstance();

    @Override
    public boolean add(StudyRecord studyRecord) {
        logger.debug("object " + studyRecord.toString() + " is ready to be added to DB");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("INSERT INTO study_records " +
                    "(lesson_id, student_id, on_lesson, mark, comment) " +
                    "VALUES (?,?,?,?,?);");
            st.setInt(1, studyRecord.getLesson().getId());
            st.setString(2, studyRecord.getStudent().getUser().getLogin());
            st.setBoolean(3, studyRecord.isOnLesson());
            st.setInt(4, studyRecord.getMark());
            st.setString(5, studyRecord.getComment());
            st.executeUpdate();
            connection.close();
            logger.debug(studyRecord.toString() + " was successfully added to table programs");
            return true;
        } catch (SQLException e) {
            logger.error("failed insert " + studyRecord.toString() + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(StudyRecord oldStudyRecord, StudyRecord newStudyRecord) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("UPDATE study_records " +
                    "SET lesson_id=?, student_id=?, on_lesson=?, mark=?, comment=? " +
                    "WHERE record_id=?;");
            st.setInt(1, newStudyRecord.getLesson().getId());
            st.setString(2, newStudyRecord.getStudent().getUser().getLogin());
            st.setBoolean(3, newStudyRecord.isOnLesson());
            st.setInt(4, newStudyRecord.getMark());
            st.setString(5, newStudyRecord.getComment());
            st.setInt(6, oldStudyRecord.getId());
            st.executeUpdate();
            connection.close();
            logger.info("User " + oldStudyRecord.toString() + " was successfully updated to " + newStudyRecord.toString());
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(StudyRecord studyRecord) {
        logger.debug("start");
        Connection connection = connectionManager.getConnection();
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM study_records " +
                    "WHERE record_id=?;");
            st.setInt(1, studyRecord.getId());
            st.executeUpdate();
            connection.close();
            logger.debug(studyRecord.toString() + " was deleted");
            return true;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public StudyRecord get(int id) {
        return null;
    }

    @Override
    public ArrayList<StudyRecord> get(Student student) {
        return null;
    }

    @Override
    public ArrayList<StudyRecord> get(Lesson lesson) {
        return null;
    }

    @Override
    public ArrayList<StudyRecord> get() {
        return null;
    }
}
