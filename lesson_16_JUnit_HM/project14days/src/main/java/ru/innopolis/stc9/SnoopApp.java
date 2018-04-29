package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by ich on 10.04.2018.
 * Класс - обввязка для реализации ДЗ
 */
public class SnoopApp {
    final static Logger logger = Logger.getLogger(SnoopApp.class);
    /**
     * Имя папки, где лежат файлы для чтения (только английский)
     */
    private String dirFiles;
    /**
     * Путь к файлу, где есть список слов для поиска, из которого можно сформировать свой список (короткий)
     */
    private String fileWords;
    /**
     * Путь к файлу для вывода результатов.
     */
    private String fileOut;
    /**
     * Список источников
     */
    private String[] sources;
    /**
     * Список слов для поиска
     */
    private String[] words;
    /**
     * Время перед началом работы тестируемого метода.
     */
    private long start;

    /**
     * По параметру дома я или не дома инициализиреут имена дирректорий.
     * Также можно указать, какой пакет файлов будем тестировать (большой или меленький)
     *
     * @param amIAtHome
     * @param isSmallSourceNumber
     */
    public SnoopApp(boolean amIAtHome, boolean isSmallSourceNumber) {
        if (amIAtHome) {
            dirFiles = "c:\\small\\ресурсы\\english\\" + (isSmallSourceNumber ? "tests\\" : "EnglHugeSnoop\\");
            fileWords = "c:\\small\\ресурсы\\english\\found.txt";
            fileOut = "c:\\small\\ресурсы\\english\\ВыходнойФайл.txt";
        } else {
            dirFiles = "d:\\disc_D_Irina_PC\\Documents and Setting\\панова_личное\\Java-курсы\\создать Гигабайтник (с ресурсами)\\" + (isSmallSourceNumber ? "3files\\" : "EnglishLiterature\\");
            fileWords = "d:\\disc_D_Irina_PC\\Documents and Setting\\панова_личное\\Java-курсы\\создать Гигабайтник (с ресурсами)\\found.txt";
            fileOut = "d:\\disc_D_Irina_PC\\Documents and Setting\\панова_личное\\Java-курсы\\создать Гигабайтник (с ресурсами)\\ВыходнойФайл.txt";
        }
    }

    /**
     * Делаем смесь из 3 различных типов источников данных (объединим их)
     *
     * @param arrayOfFiles
     * @param arrayOfWebs
     * @param arrayOfFTP
     * @return
     */
    private static String[] mixSources(String[] arrayOfFiles, String[] arrayOfWebs, String[] arrayOfFTP) {
        LinkedList<String> mix = new LinkedList<>();
        if (arrayOfFiles != null) {
            mix.addAll(Arrays.asList(arrayOfFiles));
        }
        if (arrayOfWebs != null) {
            mix.addAll(Arrays.asList(arrayOfWebs));
        }
        if (arrayOfFTP != null) {
            mix.addAll(Arrays.asList(arrayOfFTP));
        }
        return mix.toArray(new String[mix.size()]);
    }

    /**
     * Метод инициализирует исходные данные для тестирования работы (массив адресов ресурсов).
     * Адреса массивов- файлов формируются из файлов, находящихся в указанной директории.
     *
     * @param directoryName Имя директории, в которой храняться только файлы для чтения.
     * @return Массив адресов- файлов.
     */
    private static String[] getFileSource(String directoryName) throws FileNotFoundException {
        return new FileSorted(directoryName).sortList();
    }

    /**
     * Здесь зафиксируем некоторые ftp-ссылки с учетом логина и пароля
     *
     * @return
     */
    private static String[] getFTPSource() {
        LinkedList<String> ftp = new LinkedList<>();
        ftp.add("ftp://ftp.intel.com/readme.txt");
        return ftp.toArray(new String[ftp.size()]);
    }

    /**
     * Зафиксирован некий набор веб-ссылок
     *
     * @return
     */
    private static String[] getWebPSource() {
        LinkedList<String> webs = new LinkedList<>();
        webs.add("https://docs.oracle.com/javase/8/docs/api/java/io/File.html");
        webs.add("https://main.java.ru.stackoverflow.com/questions/483603/%D0%9C%D0%BD%D0%BE%D0%B3%D0%BE%D0%BF%D0%BE%D1%82%D0%BE%D1%87%D0%BD%D0%B0%D1%8F-%D0%BE%D0%B1%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%BA%D0%B0-%D1%84%D0%B0%D0%B9%D0%BB%D0%BE%D0%B2-%D1%81-%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5%D0%BC-executorservice");
        webs.add("http://qaru.site/questions/12272/access-file-through-multiple-threads");
        webs.add("https://pikabu.main.java.ru/story/kak_myi_kvartiru_u_zastroyshchika_otvoevali_vazhnyiy_post_5826869");
        webs.add("http://javadevblog.com/primer-ispol-zovaniya-wait-notify-i-notifyall-v-java.html");
        return webs.toArray(new String[webs.size()]);
    }

    /**
     * Обвязка для разработанного класса.
     *
     * @param args
     */
    public static void main(String[] args) {
        SnoopApp snoopApp = new SnoopApp(true, true);

        String[] sources = null;
        try {
            sources = snoopApp.getSources(1);
        } catch (OtherExseption otherExseption) {
            logger.error("проблема со списком источников");
        }

        String[] words = null;
        try {
            words = snoopApp.getWords(0);
        } catch (OtherExseption otherExseption) {
            logger.error(" слова для поиска не определены");
        }

        String res = snoopApp.getFileOut();
        Snoop snoop = new Snoop(100, 100, 8);
        snoopApp.printInfoBefore();

        try {
            snoop.getOccurencies(sources, words, res);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        snoopApp.printInfoAfter();
    }

    /**
     * Инициализируем и возвращаем список источников текста
     *
     * @param i
     * @return
     * @throws OtherExseption
     */
    public String[] getSources(int... i) throws OtherExseption {
        sources = initializeSources(i);
        return sources;
    }

    /**
     * Инициализируем и возвращаем список слов для поиска
     *
     * @param count
     * @return
     * @throws OtherExseption
     */
    public String[] getWords(int count) throws OtherExseption {
        words = initializeWords(count);
        return words;
    }

    /**
     * Возвращает полное имя выходного файла
     *
     * @return
     */
    public String getFileOut() {
        return fileOut;
    }

    /**
     * Задаем ресурсы для чтения различных типов.
     *
     * @param attribute Массив из комбинаций индексов 1, 2 и 3, где 1 - источником являются файлы, 2 - web резурсы, 3 - ftp.
     * @return Строковое представление обо всех внешних источниках не зависимо от их типа.
     * @throws OtherExseption передан не верный аргумент. Приемлемо только 1, 2 и 3.
     */
    private String[] initializeSources(int[] attribute) throws OtherExseption {
        String[] fileSource = null;
        String[] webSource = null;
        String[] ftpSourse = null;
        for (int i : attribute) {
            switch (i) {
                case 1:
                    try {
                        fileSource = getFileSource(dirFiles);
                    } catch (FileNotFoundException e) {
                        logger.error(e.getMessage());
                    }
                    break;
                case 2:
                    webSource = getWebPSource();
                    break;
                case 3:
                    ftpSourse = getFTPSource();
                    break;
                default:
                    throw new OtherExseption();

            }
        }
        return mixSources(fileSource, webSource, ftpSourse);
    }

    /**
     * Определяем (рандомно) массив требуемых слов
     * Если count<=0, то выдается зафиксированный для теста список
     *
     * @param count
     * @return
     */
    private String[] initializeWords(int count) throws OtherExseption {
        if (count > 0) {
            ArrayList<String> wordsInFile = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileWords)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    wordsInFile.add(line);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage());

            } catch (IOException e) {
                logger.error(e.getMessage());
            }

            String[] result = new String[count];
            if (wordsInFile.size() >= count) {
                for (int i = 0; i < count; i++) {
                    int index = new Random().nextInt(wordsInFile.size());
                    result[i] = wordsInFile.get(index);
                    wordsInFile.remove(index);
                }
                wordsInFile.clear();
                return result;
            } else {
                wordsInFile.clear();
                throw new OtherExseption();
            }
        } else {
            return new String[]{"The", "sarcophagus", "Eyelesbarrow"};
        }
    }

    /**
     * Краткая справка по данным перед расчетом.
     */
    public void printInfoBefore() {
        StringBuffer s = new StringBuffer("Число источников: ");
        s.append(sources.length).append("; Пишем в ");
        s.append(fileOut).append("; Число слов для поиска: ");
        s.append(words.length).append("{");
        for (String w : words) {
            s.append(w).append(",");
        }
        s.append("}");
        start = System.nanoTime();

    }

    /**
     * Краткая информация по результатам работы.
     */
    public void printInfoAfter() {
        long end = System.nanoTime();
        StringBuffer s = new StringBuffer("Время выполнения программы: ");
        s.append((end - start) / 1_000_000).append(" мс; Размер файла с результатами ");
        s.append(new File(fileOut).length()).append(" байт; Число строк ");
        s.append(linesCount());
    }

    /**
     * Определяем число строк в результатирующем файле
     *
     * @return
     */
    private int linesCount() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileOut)));) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e1) {
            logger.error(e1.getMessage());
        }
        return count;
    }
}
