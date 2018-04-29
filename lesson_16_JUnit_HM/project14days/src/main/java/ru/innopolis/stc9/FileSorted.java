package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Класс для получения списка файлов из директории с возможностью сортировки по возрастанию размера файлов.
 */
public class FileSorted implements Comparator {
    final static Logger logger = Logger.getLogger(FileSorted.class);
    /**
     * Список файлов
     */
    ArrayList<File> list = new ArrayList<>();

    /**
     * По имени директории создает список вложенных в нее файлов.
     * @param directoryName полное имя директории
     * @throws FileNotFoundException ошибка работы с файлом (не существует)
     */
    public FileSorted(String directoryName) throws FileNotFoundException {
        if (directoryName != null) {
            File directory = new File(directoryName);
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if ((f != null) && (!f.isDirectory())) {
                            list.add(f);
                        }
                    }
                    if (files.length==0){
                        throw new NullPointerException();
                    }
                } else {
                    throw new NullPointerException();
                }
            }
        }else {
            throw new FileNotFoundException();
        }
    }

    /**
     * Получаем строкое представление об источниках текста
     * @return
     */
    public String[] sortList(){
        Collections.sort(list, this);
        String[] source = new String[list.size()];
        for (int i = 0; i<list.size(); i++){
            source[i]=list.get(i).getAbsolutePath();
        }
        return source;
    }

    /**
     * Сравнение по размеру файлов.
     * @param o1 первый файл для сравнения.
     * @param o2 второй файл для сравнения.
     * @return 0, если размеры файлов равны; 1, если первый файл тяжелее; -1 - если первый файл легче.
     */
    @Override
    public int compare(Object o1, Object o2) {
        long l1 = getLength(o1);
        long l2 = getLength(o2);
        int result = l1 == l2 ? 0 : l1 > l2 ? 1 : -1;
        return result;
    }

    /**
     * Узнаем размер файла.
     * @param o файл
     * @return размер файла
     */
    private long getLength(Object o) {
        File f = (File) o;
        long length = f.length();
        return length;
    }
}
