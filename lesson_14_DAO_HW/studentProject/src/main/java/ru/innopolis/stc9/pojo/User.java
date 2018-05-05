package ru.innopolis.stc9.pojo;

import org.apache.log4j.Logger;

import java.util.Objects;

public class User {
    final static Logger logger = Logger.getLogger(User.class);
    private String login;
    private String password;
    private Role role;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
    public User(String login, String password, Role role) {
        this(login, password);
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public static Logger getLogger() {
        return logger;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        boolean result = Objects.equals(login, user.login) &&
                Objects.equals(password, user.password);
        logger.debug(result);
        return result;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(login, password);
        logger.debug(result);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("User{");
        s.append(login).append(';');
        s.append(password.hashCode());  //надо как-то эту инфу скрыть)
        s.append('}');
        String result = s.toString();
        logger.debug("object " + result + " was converted to String");
        return result;
    }
}
