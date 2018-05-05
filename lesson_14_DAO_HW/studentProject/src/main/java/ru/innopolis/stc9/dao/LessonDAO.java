package ru.innopolis.stc9.dao;

import ru.innopolis.stc9.pojo.*;

import java.util.ArrayList;

public interface LessonDAO {
    boolean add(Lesson lesson);
    boolean update(Lesson oldLesson, Lesson newLesson);
    boolean delete(Lesson lesson);
    Lesson get(int id);
    ArrayList<Lesson> get(Teacher teacher);
    ArrayList<Lesson> get(Team team);
}
