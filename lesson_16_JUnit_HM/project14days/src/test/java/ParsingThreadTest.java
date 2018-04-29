import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.innopolis.stc9.ParsingThread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

public class ParsingThreadTest extends Assert {
    private static Logger logger = Logger.getLogger(ParsingThreadTest.class);
    private LinkedBlockingQueue<String> queueStrings;
    private CountDownLatch latchReaders;
    private LinkedBlockingQueue<String> queueSentences;
    private CountDownLatch latchParsing;
    private String[] words;

    @Before
    public void before() {
        logger.info("preparation for the next test.");

        LinkedBlockingQueue<String> queueStrings = mock(LinkedBlockingQueue.class);
        this.queueStrings = queueStrings;
        when(queueStrings.poll())
                .thenReturn("The man in front of me was tall and strong, with thick dark hair. He sat in an expensive chair behind an expensive desk, and looked at me with cold grey eyes. He didn't have time to smile.")
                .thenReturn("")
                .thenReturn("No")
                .thenReturn("Somewhere a telephone was ringing. Howard could hear it in his dream, but it didn't wake him up. It was dark in his dream, dark and terrible.")
                .thenReturn(null);

        this.queueSentences = spy(new LinkedBlockingQueue<>(5));

        CountDownLatch latchReaders = mock(CountDownLatch.class);
        when(latchReaders.getCount()).thenReturn((long) 4)
                .thenReturn((long) 3)
                .thenReturn((long) 2)
                .thenReturn((long) 1)
                .thenReturn((long) 0);
        this.latchReaders = latchReaders;

        CountDownLatch latchParsing = mock(CountDownLatch.class);
        doNothing().when(latchParsing).countDown();
        this.latchParsing = latchParsing;

        this.words = new String[]{"in"};
    }

    /**
     * Проверка стандартной работы без выделения отдельного потока.
     */
    @Test
    public void runTest_NormalWithoutThread() {
        logger.debug("Normal works of run (without new thread)");
        String[] expected = {"The man in front of me was tall and strong, with thick dark hair.",
                "He sat in an expensive chair behind an expensive desk, and looked at me with cold grey eyes.",
                "Howard could hear it in his dream, but it didn't wake him up.",
                "It was dark in his dream, dark and terrible."};
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, queueSentences, latchParsing);
        parsingThread.run();
        verify(latchReaders, atLeast(5)).getCount();
        verify(latchParsing, times(1)).countDown();
        verify(queueStrings, times(5)).poll();
        String[] result = queueSentences.toArray(new String[queueSentences.size()]);
        assertArrayEquals("result array differ from expected one", expected, result);
    }

    /**
     * Проверяем, что поток не завершает свою работу, если очередь из предложений переполнена,
     * но возможно добавление в нее новых строк.
     * Также проверяем прерывание потока извне.
     */
    @Test
    public void runTest_CapacityNotSufficiently() {
        logger.debug("Thread was interrupted in the test because queueSentences capacity is not sufficiently");
        String[] expected = {"The man in front of me was tall and strong, with thick dark hair.",
                "He sat in an expensive chair behind an expensive desk, and looked at me with cold grey eyes."};
        LinkedBlockingQueue<String> queueSpy = spy(new LinkedBlockingQueue<>(2));
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, queueSpy, latchParsing);
        Thread p = new Thread(parsingThread);
        p.start();
        try {
            assertTrue(p.isAlive());
            while (p.getState().equals(Thread.State.RUNNABLE)) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException 1");
        }
        assertTrue(p.getState().equals(Thread.State.WAITING));
        p.interrupt();
        assertTrue(p.isInterrupted());
        verify(latchReaders, atLeast(2)).getCount();
        verify(queueStrings, times(4)).poll();

        try {
            while (p.isAlive()) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException 2");
        }
        verify(latchParsing, atLeastOnce()).countDown();
        String[] result = queueSpy.toArray(new String[queueSpy.size()]);
        assertArrayEquals("result array differ from expected one", expected, result);
        logger.debug("thread is interrupted in the test");
        logger.info("successful test");
    }

    /**
     * Проверяем, что поток завершит свою работу, если очередь из предложений была переполнена
     * (но возможно добавление в нее новых строк), а затем очищена.
     */
    @Test
    public void runTest_CapacityNotSufficientlyClear() {
        logger.debug("Thread was interrupted in the test because queueSentences capacity is not sufficiently");
        String[] expected1 = {"The man in front of me was tall and strong, with thick dark hair.",
                "He sat in an expensive chair behind an expensive desk, and looked at me with cold grey eyes."};
        String[] expected2 = {"Howard could hear it in his dream, but it didn't wake him up.",
                "It was dark in his dream, dark and terrible."};
        LinkedBlockingQueue<String> queueSpy = spy(new LinkedBlockingQueue<>(2));
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, queueSpy, latchParsing);
        Thread p = new Thread(parsingThread);
        p.start();
        try {
            assertTrue(p.isAlive());
            while (p.getState().equals(Thread.State.RUNNABLE)) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException");
        }
        assertTrue(p.getState().equals(Thread.State.WAITING));
        String[] result1 = queueSpy.toArray(new String[queueSpy.size()]);
        assertArrayEquals("result array differ from expected one", expected1, result1);
        queueSpy.poll();
        queueSpy.poll();
        try {
            while (p.isAlive()) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException");
        }
        String[] result2 = queueSpy.toArray(new String[queueSpy.size()]);
        assertArrayEquals("result array differ from expected one", expected2, result2);
        verify(latchReaders, atLeast(2)).getCount();
        verify(queueStrings, atLeast(4)).poll();
        verify(latchParsing, atLeastOnce()).countDown();
        logger.info("successful test");
    }

    /**
     * Проверяем штатную работу потока, когда размера очереди хватает.
     */
    @Test
    public void runTest_NotInterrupted() {
        logger.debug("Thread is not interrupted, queueSentences capacity is sufficiently");
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, queueSentences, latchParsing);
        Thread p = new Thread(parsingThread);
        p.start();
        try {
            assertTrue(p.isAlive());
            while (p.getState().equals(Thread.State.RUNNABLE)) {
                Thread.sleep(10);
            }
            assertFalse(p.isAlive());
        } catch (InterruptedException e) {
            logger.error("InterruptedException");
        }
        logger.info("successful test");
    }

    /**
     * Выполнение штатного конструктора
     */
    @Test
    public void constructorTest_Normal() {
        logger.debug("Normal works of single constructor");
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, queueSentences, latchParsing);
        assertNotNull(parsingThread);
    }

    /**
     * Выброс исключения при выполнении конструктора при отсутствии очереди из строк из источников (например файлов)
     */
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullQueueStrings() {
        logger.debug("exception in the constructor: queueStrings = null");
        ParsingThread parsingThread = new ParsingThread(words, null, latchReaders, queueSentences, latchParsing);
    }

    /**
     * Выброс исключения при выполнении конструктора при отсутствии слов для поиска
     */
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullWords() {
        logger.debug("exception in the constructor: words = null");
        ParsingThread parsingThread = new ParsingThread(null, queueStrings, latchReaders, queueSentences, latchParsing);
    }

    /**
     * Выброс исключения при выполнении конструктора при отсутствии очереди из предложений
     * для дальнейшей обработки (например сохранения в файл)
     */
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullQueueSentences() {
        logger.debug("exception in the constructor: words = null");
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, null, latchParsing);
    }

    /**
     * Выброс исключения при выполнении конструктора при отсутствии счетчика блокировки по числу потоков,
     * заполняющих очереди из строк из источников (например файлов)
     */
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullLatchReaders() {
        logger.debug("exception in the constructor: words = null");
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, null, queueSentences, latchParsing);
    }

    /**
     * Выброс исключения при выполнении конструктора при отсутствии счетчика блокировки по числу потоков,
     * осуществляющих парсинг строк из очереди строк.
     */
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullLatchParsing() {
        logger.debug("exception in the constructor: words = null");
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, queueSentences, null);
    }
}
