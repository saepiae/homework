package ru.innopolis.stc9.dao;

import ru.innopolis.stc9.pojo.Role;
import ru.innopolis.stc9.pojo.Teacher;
import ru.innopolis.stc9.pojo.User;

public interface TeacherDAO {
    boolean add(Teacher teacher);
    boolean update(Teacher oldTeacher, Teacher newTeacher);
    boolean delete(Teacher teacher);
    Teacher get(User user);
}
