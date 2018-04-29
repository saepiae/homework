package ru.innopolis.stc9;

import java.io.IOException;

/**
 * Created by ich on 10.04.2018.
 * Интерфейс, реализацию которого надо осуществить
 */
public interface Task {
    void getOccurencies(String[] sources, String[] words, String res) throws IOException;
}
