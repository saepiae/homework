package ru.innopolis.stc9.pojo;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Группа
 */
public class Team {
    final static Logger logger = Logger.getLogger(Team.class);
    private int id;
    private LocalDate start;
    private LocalDate finish;
    private int stream;
    private Program program;

    public Team(int id, LocalDate start, LocalDate finish, int stream, Program program) {
        this.id = id;
        this.start = start;
        this.finish = finish;
        this.stream = stream;
        this.program = program;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getFinish() {
        return finish;
    }

    public void setFinish(LocalDate finish) {
        this.finish = finish;
    }

    public int getStream() {
        return stream;
    }

    public void setStream(int stream) {
        this.stream = stream;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        boolean result = id == team.id &&
                stream == team.stream &&
                Objects.equals(start, team.start) &&
                Objects.equals(finish, team.finish) &&
                Objects.equals(program, team.program);
        logger.debug(result);
        return result;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, start, finish, stream, program);
        logger.debug(result);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Team{");
        s.append(id).append(';');
        s.append(start).append(';');
        s.append(finish).append(';');
        s.append(stream).append(';');
        s.append(program).append(';');
        s.append("}");
        String result = s.toString();
        logger.debug(result);
        return result;
    }
}
