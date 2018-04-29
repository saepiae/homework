package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class WriterThread implements Runnable {
    final static Logger logger = Logger.getLogger(WriterThread.class);
    private String res;
    private LinkedBlockingQueue<String> queue;
    private BufferedWriter writer;
    private CountDownLatch latchWriter;
    private CountDownLatch latchParsing;
    private HashMap<Integer, Integer> uniqueness = new HashMap<>();
    private int count = 0;

    public WriterThread(String res, LinkedBlockingQueue<String> queue, CountDownLatch latchWriter, CountDownLatch latchParsing) throws IOException {
        this.res = res;
        this.queue = queue;
        this.latchWriter = latchWriter;
        File out = new File(res);
        if (out.exists()) {
            out.delete();
        }
        out.createNewFile();
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
        this.latchParsing = latchParsing;
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while (latchParsing.getCount() >= 0 && !Thread.interrupted()) {
                line = queue.poll();
                if (line != null) {
                    writeToFile(line);
                } else {
                    if (latchParsing.getCount() == 0) {
                        writer.close();
                        latchWriter.countDown();
                        break;
                    }
                }
            }
            if (Thread.interrupted()){
                writer.close();
                queue.clear();
                latchWriter.countDown();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Определяет, была ли записана строка с таким же хэшкодом в файл.
     * Если была, то возвращвет номер строки записи, иначе возвращает -1.
     *
     * @param line проверяемая строка.
     * @return номер записи строки с таким же хэшкодом или -1, если такой строки не было.
     */
    private int findWrittenLine(String line, int hashcode) {
        if (!uniqueness.containsValue(line)) {
            return -1;
        }
        Set<Map.Entry<Integer, Integer>> writtenLines = uniqueness.entrySet();
        for (HashMap.Entry<Integer, Integer> pair : writtenLines) {
            if (pair.getValue().equals(hashcode)) {
                return pair.getKey();
            }
        }
        return -1;
    }

    /**
     * Производит запись в файл в случае, если такой строки в файле нет.
     * @param line
     * @throws IOException
     */
    private void writeToFile(String line) throws IOException {
        int hashcode = line.hashCode();
        int lineInFile = findWrittenLine(line, hashcode);
        if (lineInFile == -1) {
            writer.write(line);
            writer.newLine();
            uniqueness.put(++count, hashcode);
        } else {
            writer.close();
            String oldLine = readLineInFile(lineInFile);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(res, true)));
            if (!oldLine.equals(line)) {
                writer.write(line);
                writer.newLine();
                uniqueness.put(++count, hashcode);
            }
        }
    }

    /**
     * Возвращает строку из строки файла с указанным номером.
     *
     * @param lineInFile номер строки в файле, которую надо вернуть.
     * @return строку из файла с требуемым номером.
     * @throws IOException ошибка чтения файла.
     */
    private String readLineInFile(int lineInFile) throws IOException {
        String oldLine;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(res)))) {
            int tmp = 0;
            while (tmp < lineInFile) {
                reader.readLine();
            }
            oldLine = reader.readLine();
            reader.close();
        }
        return oldLine;
    }
}
