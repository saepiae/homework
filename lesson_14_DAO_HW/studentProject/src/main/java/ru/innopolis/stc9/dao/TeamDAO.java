package ru.innopolis.stc9.dao;

import ru.innopolis.stc9.pojo.Program;
import ru.innopolis.stc9.pojo.Schedule;
import ru.innopolis.stc9.pojo.Team;

import java.util.ArrayList;

public interface TeamDAO {
    boolean add(Team team);
    boolean update(Team oldTeam, Team newTeam);
    boolean delete(Team team);
    Team get(int id);
    ArrayList<Team> get(Program program);
}
