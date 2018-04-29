package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class ReaderThread implements Runnable {
    final static Logger logger = Logger.getLogger(ReaderThread.class);
    /**
     * Поток чтения из ресурса.
     */
    private BufferedReader reader;
    /**
     * Счетчик блокироки по потокам чтения
     */
    private CountDownLatch latch;
    /**
     * Очередь строк из ресурсов
     */
    private LinkedBlockingQueue<String> queue;
    /**
     * Имя источника
     */
    private String source;

    /**
     * Создает объект с полной инициализацией полей класса.
     * При неудачной попытке открытия потока, счетчик уменьшается, а объект не создается.
     *
//     * @param source Список ресурсов в строковом представлении.
     * @param latch  Счетчик блокироки по потокам чтения
     * @param queue  Очередь строк из ресурсов.
     * @throws IOException Ошибка открытия потока чтения из ресурса.
     */
    public ReaderThread(CountDownLatch latch, LinkedBlockingQueue<String> queue) throws NullPointerException {
        if (latch != null) {
            this.latch = latch;
        } else {
            throw new NullPointerException("CountDownLatch = 0");
        }
        if (queue != null) {
            this.queue = queue;
        } else {
            throw new NullPointerException("queue is null");
        }
    }

    /**
     * Если поток еще не подсоединен, то подсоединяем его к источнику
     *
     * @param source
     * @return
     */
    public boolean connect(String source) {
        if (reader == null) {
            if (source != null && !source.equals("")) {
                try {
                    reader = new BufferedReader(new InputStreamReader(getInputStream(source)));
                    this.source = source;
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            return false;
        }
        return false;
    }

    /**
     * Исполнение потока чтения из ресурса.
     * Зачитывает строку из ресурса и направляет ее в очередь для последующей обработки.
     * После отсоединения от потока чтения уменьшает счетчик блокировки на единицу.
     * Внимание: При ошибке чтения счетчик уменьшается на единицу, но обработка файла прекращается без уведомления о сбое.
     * Если поток был прерван извне, то производится очищение очереди и обнуление блокировщика.
     */
    @Override
    public void run() {
        try {
            logger.info("Start " + source);
            String line;
            while ((line = reader.readLine()) != null && !Thread.interrupted()) {
                queue.put(line);
            }
            if (Thread.interrupted()) {
                reader.close();
                queue.clear();
                while (latch.getCount() > 0) {
                    latch.countDown();
                }
            } else {
                disconnect();
            }
        } catch (IOException e1) {
            logger.error(e1.getMessage());
            disconnect();
        } catch (InterruptedException e2) {
            logger.error(e2.getMessage());
            disconnect();
        }
    }

    /**
     * Завершение работы с потоком (с уменьшением счетчика блокировки).
     */
    private void disconnect() {
        latch.countDown();
        try {
            reader.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Открываем поток из строкового источника.
     * Если открытие потока произошло не удачно, то счетчик блокировки уменьшается на единицу, а ошибка пробрассывается вверх (в конструктор).
     *
     * @param f строковое представление источника
     * @return поток
     * @throws IOException
     */
    private InputStream getInputStream(String f) throws IOException {
        InputStream inputStream = (f.startsWith("ftp") || f.startsWith("http")) ? connectURL(f) : connectFile(f);
        if (inputStream == null) {
            throw new IOException();
        }
        return inputStream;
    }

    /**
     * Открытие удаленного текстового ресурса (FTP или Web)
     * Если при создании потока произойдет ошибка - счетчик блокировки уменьшится на единицу.
     *
     * @param s строковое представление источника
     * @return байтовый поток чтения.
     */
    private InputStream connectURL(String s) {
        InputStream stream = null;
        try {
            stream = new URL(s).openStream();
        } catch (IOException e) {
            latch.countDown();
            logger.error(e.getMessage());
        }
        return stream;
    }

    /**
     * Открытие локального текстового ресурса (файла)
     * Если при создании потока произойдет ошибка - счетчик блокировки уменьшится на единицу.
     *
     * @param f строковое представление источника (путь к файлу)
     * @return байтовый поток чтения.
     */
    private InputStream connectFile(String f) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            latch.countDown();
            logger.error(e.getMessage());
        }
        return stream;
    }
}
