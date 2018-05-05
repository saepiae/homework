package ru.innopolis.stc9.pojo;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.util.Objects;

public class Lesson {
    final static Logger logger = Logger.getLogger(Lesson.class);
    private int id;
    private Team team;
    private Teacher teacher;
    private Schedule schedule;
    private LocalDate localDate;
    private String homework;
    private String comment;

    public Lesson(int id, Team team, Teacher teacher, Schedule schedule, LocalDate localDate) {
        this.id = id;
        this.team = team;
        this.teacher = teacher;
        this.schedule = schedule;
        this.localDate = localDate;
    }

    public Lesson(int id, Team team, Teacher teacher, Schedule schedule, LocalDate localDate, String homework, String comment) {
        this.id = id;
        this.team = team;
        this.teacher = teacher;
        this.schedule = schedule;
        this.localDate = localDate;
        this.homework = homework;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        boolean result = id == lesson.id &&
                Objects.equals(team, lesson.team) &&
                Objects.equals(teacher, lesson.teacher) &&
                Objects.equals(schedule, lesson.schedule) &&
                Objects.equals(localDate, lesson.localDate) &&
                Objects.equals(homework, lesson.homework) &&
                Objects.equals(comment, lesson.comment);
        logger.debug(result);
        return result;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, team, teacher, schedule, localDate, homework, comment);
        logger.debug(result);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Lesson{");
        s.append(id).append(';');
        s.append(team).append(';');
        s.append(teacher).append(';');
        s.append(schedule).append(';');
        s.append(localDate).append(';');
        if (homework != null) {
            s.append(homework);
        }
        s.append(';');
        if (comment != null) {
            s.append(comment);
        }
        s.append(';');
        s.append('}');
        String result = s.toString();
        logger.debug("object " + result + " was converted to String");
        return result;
    }
}
