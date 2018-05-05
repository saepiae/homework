package ru.innopolis.stc9.pojo;

import org.apache.log4j.Logger;

import java.util.Objects;

public class Schedule {
    final static Logger logger = Logger.getLogger(Schedule.class);
    /**
     * Первичныйключ в БД
     */
    private int id;
    /**
     * Конкретная программа обучения.
     */
    private Program program;
    /**
     * Номер урока.
     */
    private int lessonNumber;
    /**
     * Тема урока.
     */
    private String lessonTheme;

    /*public Schedule() {
        logger.debug("empty object " + toString());
    }*/

    public Schedule(int id, Program program, int lessonNumber, String lessonTheme) {
        this.id = id;
        this.program = program;
        this.lessonNumber = lessonNumber;
        this.lessonTheme = lessonTheme;
        logger.debug("object " + toString());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public String getLessonTheme() {
        return lessonTheme;
    }

    public void setLessonTheme(String lessonTheme) {
        this.lessonTheme = lessonTheme;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Schedule{");
        s.append(id).append(';');
        s.append(program.toString()).append(';');
        s.append(lessonNumber).append(';');
        s.append(lessonTheme);
        s.append('}');
        String result = s.toString();
        logger.debug("object " + result + " was converted to String");
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        boolean result = id == schedule.id &&
                lessonNumber == schedule.lessonNumber &&
                Objects.equals(program, schedule.program) &&
                Objects.equals(lessonTheme, schedule.lessonTheme);
        logger.debug("objects are equal? " + result);
        return result;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, program, lessonNumber, lessonTheme);
        logger.debug("hashcode of " + toString() + " = " + result);
        return result;
    }
}
