package ru.innopolis.stc9.pojo;

import org.apache.log4j.Logger;

/**
 * Created by ich on 04.05.2018.
 */
public class StudyRecord {
    final static Logger logger = Logger.getLogger(StudyRecord.class);
    private int id;
    private Lesson lesson;
    private Student student;
    private boolean isOnLesson;
    private int mark;
    private String comment;

    public StudyRecord(int id, Lesson lesson, Student student, boolean isOnLesson, int mark, String comment) {
        this(id, lesson, student, isOnLesson);
        this.mark = mark;
        this.comment = comment;
    }

    public StudyRecord(int id, Lesson lesson, Student student, boolean isOnLesson) {
        this.id = id;
        this.lesson = lesson;
        this.student = student;
        this.isOnLesson = isOnLesson;
    }

    public StudyRecord(int id, Lesson lesson, Student student, int mark) {
        this(id, lesson, student, true);
        this.mark = mark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public boolean isOnLesson() {
        return isOnLesson;
    }

    public void setOnLesson(boolean onLesson) {
        isOnLesson = onLesson;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("StudyRecord{");
        s.append(id).append(';');
        s.append(lesson).append(';');
        s.append(student).append(';');
        s.append(isOnLesson).append(';');
        if (mark > 0) {
            s.append(mark);
        }
        s.append(';');
        if (comment != null) {
            s.append(comment);
        }
        s.append(";}");
        String result = s.toString();
        logger.debug("object " + result + " was converted to String");
        return result;
    }

    @Override
    public boolean equals(Object o) {
        logger.debug("start compare");
        if (this == o) return true;
        if (!(o instanceof StudyRecord)) return false;

        StudyRecord that = (StudyRecord) o;

        if (getId() != that.getId()) return false;
        if (isOnLesson() != that.isOnLesson()) return false;
        if (getMark() != that.getMark()) return false;
        if (!getLesson().equals(that.getLesson())) return false;
        if (!getStudent().equals(that.getStudent())) return false;
        boolean result = getComment() != null ? getComment().equals(that.getComment()) : that.getComment() == null;
        logger.debug(result);
        return result;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getLesson().hashCode();
        result = 31 * result + getStudent().hashCode();
        result = 31 * result + (isOnLesson() ? 1 : 0);
        result = 31 * result + getMark();
        result = 31 * result + (getComment() != null ? getComment().hashCode() : 0);
        logger.debug(result);
        return result;
    }
}
