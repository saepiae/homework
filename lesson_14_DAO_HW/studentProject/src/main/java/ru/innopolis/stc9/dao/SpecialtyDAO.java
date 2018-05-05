package ru.innopolis.stc9.dao;

import ru.innopolis.stc9.pojo.Specialty;

import java.util.ArrayList;

public interface SpecialtyDAO {
    /**
     * Добавить новую специальность в БД
     * @param specialty
     * @return
     */
    boolean add(Specialty specialty);

    /**
     * Найти специальность под номером id в БД
     * @param id
     * @return единственное значение, если ключ полностью совпадает
     */
    Specialty get(int id);

    /**
     * Найти специальность по полному имени или краткому наименованию
     * @param name
     * @return
     */
    Specialty get(String name);

    /**
     * Найти специальность по подлному и сокращенному именам.
     * @param specialtyName
     * @param specialtyCode
     * @return
     */
    ArrayList<Specialty> get(String specialtyName, String specialtyCode);

    /**
     * Обновить значения в таблице
     * @param oldSpecialty
     * @param newSpecialty
     * @return
     */
    boolean update(Specialty oldSpecialty, Specialty newSpecialty);

    /**
     * Удалить значения из таблицы.
     * @param specialty
     * @return
     */
    boolean delete(Specialty specialty);
}
