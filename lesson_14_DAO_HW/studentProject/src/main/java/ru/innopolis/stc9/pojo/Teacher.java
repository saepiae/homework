package ru.innopolis.stc9.pojo;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.util.Objects;

public class Teacher {
    final static Logger logger = Logger.getLogger(Teacher.class);
    private User user;
    private String firstName;
    private String lastName;
    private String patronymic;
    private LocalDate birthday;
    private int passport;
    private String adress;
    private int phone;
    private String skype;
    private String telegram;

    public Teacher(User user, String firstName, String lastName) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Teacher(User user, String firstName, String lastName, String patronymic, LocalDate birthday, int passport, String adress, int phone, String skype, String telegram) {
        this(user, firstName, lastName);
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.passport = passport;
        this.adress = adress;
        this.phone = phone;
        this.skype = skype;
        this.telegram = telegram;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static Logger getLogger() {
        return logger;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public int getPassport() {
        return passport;
    }

    public void setPassport(int passport) {
        this.passport = passport;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        boolean result = passport == teacher.passport &&
                phone == teacher.phone &&
                Objects.equals(user, teacher.user) &&
                Objects.equals(firstName,  teacher.firstName) &&
                Objects.equals(lastName,  teacher.lastName) &&
                Objects.equals(patronymic,  teacher.patronymic) &&
                Objects.equals(birthday,  teacher.birthday) &&
                Objects.equals(adress,  teacher.adress) &&
                Objects.equals(skype,  teacher.skype) &&
                Objects.equals(telegram,  teacher.telegram);
        logger.debug(result);
        return result;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(user, firstName, lastName, patronymic, birthday, passport, adress, phone, skype, telegram);
        logger.debug(result);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Teacher{");
        s.append(user).append(';');
        s.append(firstName).append(';');
        s.append(lastName).append(';');
        s.append(patronymic).append(';');
        s.append(birthday).append(';');
        s.append(String.valueOf(passport).hashCode()).append(';');  //надо как-то эту инфу скрыть)
        s.append(adress).append(';');
        s.append(phone).append(';');
        s.append(skype).append(';');
        s.append(telegram).append(';');
        s.append('}');
        String result = s.toString();
        logger.debug("object " + result + " was converted to String");
        return result;
    }
}
