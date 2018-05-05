package ru.innopolis.stc9.dao;

import ru.innopolis.stc9.pojo.Program;
import ru.innopolis.stc9.pojo.Specialty;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ProgramDAO {
    /**
     * Добавляем новую программу обучения по специальности
     * @param program
     * @return
     */
    boolean add(Program program);

    /**
     * найти программу обучения по специальности по прямому ключу.
     * @param id
     * @return
     */
    Program get(int id);

    /**
     * Найти все программы обучения по всем специальностям, утвержденных указанной датой (в строковом предаставлении)
     * @param date
     * @return
     */
    ArrayList<Program> get(LocalDate date);

    /**
     * Найти все программы обучения по указанной специальности
     * @param specialty
     * @return
     */
    ArrayList<Program> get(Specialty specialty);

    /**
     * Обновить данные по программе обучения по специальности
     * @param oldProgram
     * @param newProgram
     * @return
     */
    boolean update(Program oldProgram, Program newProgram);

    /**
     * Удалить данные по программе обучения по специальности.
     * @param oldProgram
     * @return
     */
    boolean delete(Program oldProgram);
}
