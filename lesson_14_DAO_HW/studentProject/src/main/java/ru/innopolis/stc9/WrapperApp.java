package ru.innopolis.stc9;

import org.apache.log4j.Logger;
import ru.innopolis.stc9.dao.*;
import ru.innopolis.stc9.pojo.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class WrapperApp {
    final static Logger logger = Logger.getLogger(WrapperApp.class);

    public static void main(String[] args) {
        SpecialtyDAO specialtyDAO = new SpecialtyDAOImpl();
        specialtyDAO.add(new Specialty(0, "Высшая математика и информатика", "ВТ"));
        ArrayList<Specialty> listOfSpecialties = specialtyDAO.get("Высшая математика и информатика", "ВТ");
        Specialty specialty = listOfSpecialties.get(0);

        ProgramDAO programDAO = new ProgramDAOImpl();
        programDAO.add(new Program(0, specialty, convert("2018-01-07")));
        programDAO.add(new Program(0, specialty, convert("2018-02-03")));
        programDAO.add(new Program(0, specialty, convert("2018-02-03")));
        ArrayList<Program> listOfPrograms = programDAO.get(specialty);

        ScheduleDAO scheduleDAO = new ScheduleDAOImpl();
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(1), 4, "Кот жив или мертв?!"));
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(0), 1, "Общие понятия и принципы высшей математики"));
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(1), 2, "Почему коты это жидкость?"));
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(0), 3, "Математический анализ"));
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(0), 4, "Итогая контрольная работа"));
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(1), 1, "Концепция котов"));
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(0), 2, "Основы информатики"));
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(1), 3, "Теория кошечьего заговора"));
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(0), 5, "Экзамен"));
        scheduleDAO.add(new Schedule(0, listOfPrograms.get(1), 5, "Практическая работа: \"Завести себе кота\""));

        ArrayList<Schedule> listOfScheduleProgram1 = scheduleDAO.get(listOfPrograms.get(0));
        ArrayList<Schedule> listOfScheduleProgram2 = scheduleDAO.get(listOfPrograms.get(1));

        TeacherDAO teacherDAO = new TeacherDAOImpl();
        teacherDAO.add(new Teacher(new User("Smirnov", "1q2w3e4r"), "Смирнов", "Олег"));
        teacherDAO.add(new Teacher(new User("KOT", "may-may"), "Всевлодыка", "Котофей"));

        UserDAO userDAO = new UserDAOImpl();
        ArrayList<User> listOfTeacher = userDAO.allWithRole(Role.TEACHER);

        TeamDAO teamDAO = new TeamDAOImpl();
        teamDAO.add(new Team(0, convert("2018-01-10"), convert("2018-06-10"), 1, listOfPrograms.get(0)));
        teamDAO.add(new Team(0, convert("2018-01-29"), convert("2018-06-29"), 2, listOfPrograms.get(0)));
        teamDAO.add(new Team(0, convert("2018-03-07"), convert("2018-07-07"), 1, listOfPrograms.get(1)));
        ArrayList<Team> listOfTeams1 = teamDAO.get(listOfPrograms.get(0));
        ArrayList<Team> listOfTeams2 = teamDAO.get(listOfPrograms.get(1));

        StudentDAO studentDAO = new StudentDAOImpl();
        studentDAO.add(new Student(new User("l1", "1"), "Акакунов", "Петр", listOfTeams1.get(0)));
        studentDAO.add(new Student(new User("l2", "2"), "Самохина", "Анна", listOfTeams1.get(0)));
        studentDAO.add(new Student(new User("l3", "3"), "Акбар", "Назыл", listOfTeams1.get(0)));
        studentDAO.add(new Student(new User("l4", "4"), "Иванникова", "Марина", listOfTeams1.get(0)));

        studentDAO.add(new Student(new User("l11", "5"), "Судаков", "Андрей", listOfTeams1.get(1)));
        studentDAO.add(new Student(new User("l12", "6"), "Петренко", "Олег", listOfTeams1.get(1)));
        studentDAO.add(new Student(new User("l13", "7"), "Кашарин", "Роман", listOfTeams1.get(1)));

        studentDAO.add(new Student(new User("l21", "8"), "Колбаскин", "Васек", listOfTeams2.get(0)));
        studentDAO.add(new Student(new User("l22", "9"), "Молочный", "Черныш", listOfTeams2.get(0)));
        studentDAO.add(new Student(new User("l23", "10"), "Жирабарсов", "Веслоух", listOfTeams2.get(0)));
        studentDAO.add(new Student(new User("l24", "11"), "Пушистохвостая", "Лола", listOfTeams2.get(0)));

        Teacher t1 = teacherDAO.get(new User("Smirnov", "1q2w3e4r"));
        Teacher t2 = teacherDAO.get(new User("KOT", "may-may"));

        LessonDAO lessonDAO = new LessonDAOImpl();
        Lesson lesson11 = new Lesson(1, listOfTeams1.get(0), t1, listOfScheduleProgram1.get(0), convert("2018-01-15"));
        logger.debug(lesson11.toString());
        lessonDAO.add(lesson11);
        lessonDAO.add(new Lesson(0, listOfTeams1.get(0), t2, listOfScheduleProgram1.get(1), convert("2018-02-02")));
        lessonDAO.add(new Lesson(0, listOfTeams1.get(0), t1, listOfScheduleProgram1.get(2), convert("2018-02-18")));
        lessonDAO.add(new Lesson(0, listOfTeams1.get(0), t2, listOfScheduleProgram1.get(3), convert("2018-05-29")));

        lessonDAO.add(new Lesson(0, listOfTeams1.get(1), t2, listOfScheduleProgram1.get(0), convert("2018-02-16")));
        lessonDAO.add(new Lesson(0, listOfTeams1.get(1), t1, listOfScheduleProgram1.get(1), convert("2018-04-22"), "глава 2 и 3", "самостоятельно разобрать СЛАУ"));
        lessonDAO.add(new Lesson(0, listOfTeams1.get(1), t1, listOfScheduleProgram1.get(2), convert("2018-04-28")));
        lessonDAO.add(new Lesson(0, listOfTeams1.get(1), t2, listOfScheduleProgram1.get(3), convert("2018-05-19")));

        lessonDAO.add(new Lesson(0, listOfTeams2.get(0), t2, listOfScheduleProgram1.get(0), convert("2018-03-11"), "покормить кота", null));
        lessonDAO.add(new Lesson(0, listOfTeams2.get(0), t1, listOfScheduleProgram1.get(1), convert("2018-04-12")));
        lessonDAO.add(new Lesson(0, listOfTeams2.get(0), t2, listOfScheduleProgram1.get(2), convert("2018-06-09"), "погладить кота", null));
        lessonDAO.add(new Lesson(0, listOfTeams2.get(0), t2, listOfScheduleProgram1.get(3), convert("2018-06-15"), "поиграть с котом", null));
        lessonDAO.add(new Lesson(0, listOfTeams2.get(0), t1, listOfScheduleProgram1.get(4), convert("2018-07-01")));
        ArrayList<Lesson> lessonsByTeacher1 = lessonDAO.get(t1);
        ArrayList<Lesson> lessonsByTeacher2 = lessonDAO.get(t2);


        StudyRecordDAO studyRecordDAO = new StudyRecordDAOImpl();
        for (int i = 27; i < 40; i++) {
            Lesson lesson = lessonDAO.get(i);
            Team team = lesson.getTeam();
            ArrayList<Student> list = studentDAO.get(team);
            Random random = new Random();
            int notOnLesson = random.nextInt(list.size());
            for (int s = 0; s < list.size(); s++) {
                if (s != notOnLesson) {
                    int m = (s - notOnLesson) % 2 != 0 ? 5 : 4;
                    studyRecordDAO.add(new StudyRecord(0, lesson, list.get(s), m));
                }
            }
        }

        
        int y = 0;
    }

    private static LocalDate convert(String s) {
        return Date.valueOf(s).toLocalDate();
    }
}
