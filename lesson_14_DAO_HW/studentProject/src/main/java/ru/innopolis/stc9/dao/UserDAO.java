package ru.innopolis.stc9.dao;

import ru.innopolis.stc9.pojo.Role;
import ru.innopolis.stc9.pojo.Student;
import ru.innopolis.stc9.pojo.Teacher;
import ru.innopolis.stc9.pojo.User;

import java.util.ArrayList;

public interface UserDAO {

    boolean add(User user);

    boolean update(User oldUser, User newUser);

    boolean delete(User user);

    User get(String login, String password);
    ArrayList<User> allWithRole(Role role);
}
