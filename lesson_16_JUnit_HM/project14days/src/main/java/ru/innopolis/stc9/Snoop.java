package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Класс предназначен для обработки множества ресурсов строк.
 * Основной метод - поиск в ресурсах предложений, в которых содержится хотя бы одно из требуемых слов.
 * Результаты обработки сохраняются в файл.
 * В выходном файле содержатся только уникальные предложения, повторы запрещены.
 * Перед каждым запуском выходной файл полностью очищается (если был) или создается новый.
 */
public class Snoop implements Task {
    final static Logger logger = Logger.getLogger(Snoop.class);
    /**
     * Очередь строк из ресурсов.
     */
    LinkedBlockingQueue<String> queueLines;
    /**
     * Очередь предложений, предназначенных для сохранения.
     */
    LinkedBlockingQueue<String> queueSentences;
    /**
     * пул потоков
     */
    ExecutorService threadPool;
    /**
     * счетчик блокировки для парсинг-потока
     */
    CountDownLatch latchParsing = new CountDownLatch(1);
    /**
     * счетчик блокировки для потока сохранения предложений
     */
    CountDownLatch latchWriter = new CountDownLatch(1);
    /**
     * Счетчик блокировки для потоков по чтению ресурсов.
     */
    CountDownLatch latchReaders;

    /**
     * Конструктор, который позволяет регулировать параметры реализации многопоточности.
     * Пул потоков должен содержать не менее трех потоков.
     * Если передаваемый параметр не отвечает данному требованию, его значение будет переопределено на 3.
     *
     * @param queueLinesSize     размер очереди строк из ресурсов.
     * @param queueSentencesSize размер очереди предложений для сохранения/передачи.
     * @param threadCount        максимальное число потоков для одновременного исполнения в пуле потоков.
     */
    public Snoop(int queueLinesSize, int queueSentencesSize, int threadCount) {
        if (threadCount < 3) {
            threadCount = 3;
        }
        queueLines = new LinkedBlockingQueue<>(queueLinesSize);
        queueSentences = new LinkedBlockingQueue<>(queueSentencesSize);
        threadPool = Executors.newFixedThreadPool(threadCount);
    }

    /**
     * Осуществляет поиск в источниках предложений, содержащих хотя бы одно слово из требуемых.
     * Если в процессе работы с ресурсами текста произойдет ошибка, то игнорируется толь
     *
     * @param sources Строковая запись источника текста.
     * @param words   Список требуемых в поиске слов.
     * @param res     Источник, куда заносятся результаты.
     * @throws IOException
     */
    @Override
    public void getOccurencies(String[] sources, final String[] words, String res) throws IOException {

        boolean regularWork = true;

        latchReaders = new CountDownLatch(sources.length);
        Thread p = new Thread(new ParsingThread(words, queueLines, latchReaders, queueSentences, latchParsing));
        Thread w = new Thread(new WriterThread(res, queueSentences, latchWriter, latchParsing));

        threadPool.execute(p);
        threadPool.execute(w);

        for (final String f : sources) {
            try {
                ReaderThread rt = new ReaderThread(latchReaders, queueLines);
                rt.connect(f);
                Thread t = new Thread(rt);
                threadPool.execute(t);
            } catch (NullPointerException e) {
                threadPool.shutdownNow();
                regularWork = false;
                logger.error(e.getMessage());
                break;
            }
        }
        if (regularWork) {
            closeThreads();
        } else {
            throw new IOException();
        }
    }

    /**
     * Штатное завершение работы
     */
    private void closeThreads() {
        threadPool.shutdown();
        try {
            latchReaders.await();
            latchParsing.await();
            latchWriter.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }
}


