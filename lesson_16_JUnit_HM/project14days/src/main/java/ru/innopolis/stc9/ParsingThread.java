package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс предназначен для обработки строк из потокобезопасной очереди с последующим занесением результатов обработки
 * в другую потокобезопасную очередь. Под обработкой следует понимать проверку: содержит ли строка из источника слово из списка.
 * Если в строке имеется хотя бы одно слово, то предложение с этим словом помещается в очередь для последующей передачи
 * в поток сохранения строк в некий ресурс. При этом подразумевается, что в результатах все предложения будут уникальными.
 * Повторы недопускаются.
 */
public class ParsingThread implements Runnable {
    final static Logger logger = Logger.getLogger(ParsingThread.class);
    /**
     * Список слов, хотя бы одно из которого должно содержаться в предложении.
     */
    private static String[] words;
    /**
     * Шаблон поиска предложения в строке.
     */
    private static Pattern patternSentences;
    /**
     * Шаблон поиска слова в строке.
     */
    private static Pattern patternWords;
    /**
     * Очередь строк для парсинга.
     */
    private LinkedBlockingQueue<String> queueStrings;
    /**
     * Счетчик потоков чтения, не завершивших работу.
     */
    private CountDownLatch latchReaders;
    /**
     * Счетчик потока парсинга.
     */
    private CountDownLatch latchParsing;
    /**
     * Очередь предложений для сохранения.
     */
    private LinkedBlockingQueue<String> queueSentences;
    /**
     * Длина самого короткого слова из списка слов для поиска.
     */
    private int minimalLength;

    public ParsingThread() {
    }

    /**
     * Конструктор с полной инициализацией полей.
     *
     * @param words          список слов, хотя бы одно из которого должно содержаться в предложении.
     * @param queueStrings   очередь строк для проверки на содержание требуемых слов.
     * @param latchReaders   счетчик блокировок потоков чтения в пуле.
     * @param queueSentences очередь строк для сохранения.
     * @param latchParsing   идентификатор окончания работы потока.
     */
    public ParsingThread(String[] words, LinkedBlockingQueue<String> queueStrings, CountDownLatch latchReaders, LinkedBlockingQueue<String> queueSentences, CountDownLatch latchParsing) throws NullPointerException {
        if (words != null) {
            ParsingThread.words = words;
            patternSentences = Pattern.compile("([^.!?]+[.!?])");
            patternWords = Pattern.compile(generatePattern(words));
            minimalLength = getMinLength();
        } else {
            throw new NullPointerException("words is null");
        }

        if (queueStrings != null) {
            this.queueStrings = queueStrings;
        } else {
            throw new NullPointerException("queueStrings is null");
        }
        if (latchReaders != null) {
            this.latchReaders = latchReaders;
        } else {
            throw new NullPointerException("latchReaders is null");
        }
        if (queueSentences != null) {
            this.queueSentences = queueSentences;
        } else {
            throw new NullPointerException("queueSentences is null");
        }
        if (latchParsing != null) {
            this.latchParsing = latchParsing;
        } else {
            throw new NullPointerException("latchParsing is null");
        }
    }

    /**
     * Определяет длину самого короткого слова из списка слов.
     *
     * @return длину самого короткого слова из списка слов.
     */
    private int getMinLength() {
        int m = Integer.MAX_VALUE;
        for (String w : words) {
            if (m > w.length()) {
                m = w.length();
            }
        }
        return m;
    }

    /**
     * Осуществляет обработку строк из потокобезопасной очереди с последующим занесением результатов обработки
     * в другую потокобезопасную очередь. Под обработкой следует понимать проверку: содержит ли строка из источника слово из списка.
     * Если в строке имеется хотя бы одно слово, то предложение с этим словом помещается в очередь для последующей передачи
     * в поток сохранения предложений в некий ресурс.
     *
     * @param line проверяемая строка.
     * @throws InterruptedException прерывание потока.
     */
    private void parsing(String line) throws InterruptedException {
        if (doesContain(line)) {
            Matcher matcherSentences = patternSentences.matcher(line);
            while (matcherSentences.find()) {
                StringBuilder sentence = new StringBuilder(matcherSentences.group());
                if (matcherSentences.start() > 0) {
                    sentence.deleteCharAt(0);
                }
                Matcher matcherWords = patternWords.matcher(sentence);
                if (matcherWords.find()) {
                    queueSentences.put(sentence.toString());
                    continue;
                }
            }
        }
    }

    /**
     * Проверяет, содержит ли строка хотя  бы одно слово из массива слов
     *
     * @param line строка из источника
     * @return true если есть хотя бы одно слово, иначе - false.
     */
    private boolean doesContain(String line) {
        if (line.length() < minimalLength) {
            return false;
        }
        for (String w : words) {
            if (line.contains(w)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Генерирует паттерн для поиска слов в предложении.
     *
     * @param words Список всех требуемых слов.
     */
    private String generatePattern(String[] words) {
        StringBuffer p = new StringBuffer("\\b(");
        for (int i = 0; i < words.length - 1; i++) {
            p.append(words[i]).append("|");
        }
        p.append(words[words.length - 1]);
        p.append(")\\b");
        return p.toString();
    }

    /**
     * Осуществляет парсинг строк из очереди строк из ресурса
     * с занесением результатов парсинга в очередь строк для сохранения.
     * Внимание: при прерывании потока через исключение потокзавершает свою работу, обнуляя счетчик блокировки.
     */
    @Override
    public void run() {
        try {
            String line = null;
            logger.info("start of parsing");
            while (latchReaders.getCount() >= 0 && !Thread.interrupted()) {
                line = queueStrings.poll();
                if (line != null) {
                    parsing(line);
                } else {
                    if (latchReaders.getCount() == 0) {
                        break;
                    }
                }
            }
            if (Thread.interrupted()) {
                queueSentences.clear();
                queueStrings.clear();
            }
            latchParsing.countDown();
            logger.info("end of parsing");
        } catch (InterruptedException e) {
            latchParsing.countDown();
            logger.error("the thread was interrupted");
        }
    }
}
