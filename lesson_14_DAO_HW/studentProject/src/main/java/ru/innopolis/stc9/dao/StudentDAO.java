package ru.innopolis.stc9.dao;

import ru.innopolis.stc9.pojo.Student;
import ru.innopolis.stc9.pojo.Team;
import ru.innopolis.stc9.pojo.User;

import java.util.ArrayList;

public interface StudentDAO {
    boolean add(Student student);

    boolean update(Student oldStudent, Student newStudent);

    boolean delete(Student student);

    Student get(User user);

    ArrayList<Student> get(Team team);
}
