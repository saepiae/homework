package ru.innopolis.stc9.pojo;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Программа обучения по специальности.
 */
public class Program {
    final static Logger logger = Logger.getLogger(Program.class);
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
    /**
     * Код программы
     */
    private int id;
    /**
     * код специальности
     */
    private Specialty specialty;
    /**
     * Дата вступления программы в силу
     */
    private LocalDate approvalDate;

//    public Program() {
//        logger.debug("empty object " + toString());
//    }

    public Program(int id, Specialty specialty, LocalDate approvalDate) {
        this.id = id;
        this.specialty = specialty;
        this.approvalDate = approvalDate;
    }

    public int getId() {
        return id;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Program{");
        s.append(id).append(';');
        s.append(specialty.toString()).append(';');
        s.append(dateFormat.format(approvalDate)).append(';');
        s.append('}');
        String result = s.toString();
        logger.debug("object " + result + " was converted to String");
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        boolean result = id == program.id &&
                Objects.equals(specialty, program.specialty) &&
                Objects.equals(approvalDate, program.approvalDate);
        logger.debug("objects are equal? " + result);
        return result;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, specialty, approvalDate);
        logger.debug("hashcode of " + toString() + " = " + result);
        return result;
    }
}
