package ru.innopolis.stc9.dao;

import ru.innopolis.stc9.pojo.Program;
import ru.innopolis.stc9.pojo.Schedule;

import java.util.ArrayList;

public interface ScheduleDAO {
    /**
     * Добавить занятие в программу специальности
     * @param schedule
     * @return
     */
    boolean add(Schedule schedule);

    /**
     * Получить детали занятия из расписания по его id
     * @param id
     * @return
     */
    Schedule get(int id);

    /**
     * Полчить список всех занятий по расписанию по конкретной программе специальности.
     * @param program
     * @return
     */
    ArrayList<Schedule> get(Program program);

    /**
     * Обновить данные занятия из плана обучения
     * @param oldSchedule
     * @param newSchedule
     * @return
     */
    boolean update(Schedule oldSchedule, Schedule newSchedule);

    /**
     * Удалить занятие из плана обучения.
     * @param schedule
     * @return
     */
    boolean delete(Schedule schedule);
}
