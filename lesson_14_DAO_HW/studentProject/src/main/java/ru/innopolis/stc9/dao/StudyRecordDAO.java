package ru.innopolis.stc9.dao;

import ru.innopolis.stc9.pojo.Lesson;
import ru.innopolis.stc9.pojo.Student;
import ru.innopolis.stc9.pojo.StudyRecord;

import java.util.ArrayList;

/**
 * Created by ich on 04.05.2018.
 */
public interface StudyRecordDAO {
    boolean add(StudyRecord studyRecord);
    boolean update(StudyRecord oldStudyRecord, StudyRecord newStudyRecord);
    boolean delete(StudyRecord studyRecord);
    StudyRecord get(int id);
    ArrayList<StudyRecord> get(Student student);
    ArrayList<StudyRecord> get(Lesson lesson);

    // TODO: 04.05.2018 Удалить его в конце
    ArrayList<StudyRecord> get();
}
