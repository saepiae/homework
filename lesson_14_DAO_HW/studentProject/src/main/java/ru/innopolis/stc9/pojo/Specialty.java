package ru.innopolis.stc9.pojo;

import org.apache.log4j.Logger;

import java.util.Objects;

import static ru.innopolis.stc9.Tools.isCorrect;

public class Specialty {
    final static Logger logger = Logger.getLogger(Specialty.class);
    /**
     * уникальный ключ специальности
     */
    private int id;
    /**
     * Полное имя специальности
     */
    private String name;
    /**
     * Сокращенное обозначение специальности
     */
    private String abbreviation;

    public Specialty(int id, String name, String abbreviation) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Specialty{");
        s.append(id).append(';');
        s.append(name).append(';');
        s.append(abbreviation);
        s.append('}');
        String result = s.toString();
        logger.debug("object " + result + " was converted to String");
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Specialty specialty = (Specialty) o;
        boolean result = id == specialty.id &&
                Objects.equals(name, specialty.name) &&
                Objects.equals(abbreviation, specialty.abbreviation);
        logger.debug("objects are equal? " + result);
        return result;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, abbreviation);
        logger.debug("hashcode of " + toString() + " = " + result);
        return result;
    }
}
