import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.innopolis.stc9.ParsingThread;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

public class ParsingThreadTest extends Assert{
    private static Logger logger = Logger.getLogger(ParsingThreadTest.class);
    private ParsingThread parsingThread;
    private LinkedBlockingQueue<String> queueStrings;
    private CountDownLatch latchReaders;
    private LinkedBlockingQueue<String> queueSentences;
    private CountDownLatch latchParsing;
    private String[] words;

    @Before
    public void before(){
        logger.info("preparation for the next test.");

        LinkedBlockingQueue<String> queueStrings = mock(LinkedBlockingQueue.class);
        this.queueStrings = queueStrings;
        when(queueStrings.poll()).thenReturn("The man in front of me was tall and strong, with thick dark hair. He sat in an expensive chair behind an expensive desk, and looked at me with cold grey eyes. He didn't have time to smile.")
                .thenReturn("")
                .thenReturn("No")
                .thenReturn("Somewhere a telephone was ringing. Howard could hear it in his dream, but it didn't wake him up. It was dark in his dream, dark and terrible.")
                .thenReturn(null);

        LinkedBlockingQueue<String> queueSentences = new LinkedBlockingQueue<>(5);
        this.queueSentences = queueSentences;

        CountDownLatch latchReaders = mock(CountDownLatch.class);
        when(latchReaders.getCount()).thenReturn((long) 7)
                .thenReturn((long) 6)
                .thenReturn((long) 5)
                .thenReturn((long) 4)
                .thenReturn((long) 3)
                .thenReturn((long) 2)
                .thenReturn((long) 1)
                .thenReturn((long) 0);
        this.latchReaders = latchReaders;

        CountDownLatch latchParsing = mock(CountDownLatch.class);
        when(latchParsing.getCount()).thenReturn((long) 1)
                .thenReturn((long) 1)
                .thenReturn((long) 1)
                .thenReturn((long) 0);
        doNothing().when(latchParsing).countDown();
        this.latchParsing = latchParsing;

        String[] words = {"in"};
        this.words = words;
    }
    @Test
    public void runTest_Normal(){
        logger.debug("Normal works of run");
        String[] expected = {"The man in front of me was tall and strong, with thick dark hair.",
                "He sat in an expensive chair behind an expensive desk, and looked at me with cold grey eyes.",
                "Howard could hear it in his dream, but it didn't wake him up.",
                "It was dark in his dream, dark and terrible."};
        LinkedBlockingQueue<String> queueSpy = mock(LinkedBlockingQueue.class); //Mockito.spy(queueSentences);
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, queueSentences, latchParsing);
        parsingThread.run();
        String[] result = queueSentences.toArray(new String[queueSpy.size()]);//queueSentences.toArray(new String[queueSentences.size()]);
        assertArrayEquals("result array differ from expected one", expected, result);
    }
    @Test
    public void constructorTest_Normal(){
        logger.debug("Normal works of single constructor");
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, queueSentences, latchParsing);
        assertNotNull(parsingThread);
    }
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullQueueStrings(){
        logger.debug("exception in the constructor: queueStrings = null");
        ParsingThread parsingThread = new ParsingThread(words, null, latchReaders, queueSentences, latchParsing);
    }
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullWords(){
        logger.debug("exception in the constructor: words = null");
        ParsingThread parsingThread = new ParsingThread(null, queueStrings, latchReaders, queueSentences, latchParsing);
    }
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullQueueSentences(){
        logger.debug("exception in the constructor: words = null");
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, null, latchParsing);
    }
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullLatchReaders(){
        logger.debug("exception in the constructor: words = null");
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, null, queueSentences, latchParsing);
    }
    @Test(expected = NullPointerException.class)
    public void constructorTest_NullLatchParsing(){
        logger.debug("exception in the constructor: words = null");
        ParsingThread parsingThread = new ParsingThread(words, queueStrings, latchReaders, queueSentences, null);
    }

    private Answer<String[]> answer = new Answer<String[]>() {
        @Override
        public String[] answer(InvocationOnMock invocation) throws Throwable
        {
            Object mock = invocation.getMock();
            System.out.println ("mock object : " + mock.toString());
            Object[] args = invocation.getArguments();
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i<args.length; i++){
                switch (i) {
                    case(0):
                        list.add("The man in front of me was tall and strong, with thick dark hair.");
                        break;
                    case (1):
                        list.add("He sat in an expensive chair behind an expensive desk, and looked at me with cold grey eyes.");
                        break;
                    case (2):
                        list.add("I sat back in my chair and lit a cigarette slowly.");
                        break;
                    case (3):
                        list.add("Howard could hear it in his dream, but it didn't wake him up.");
                        break;
                    case (4):
                        list.add("It was dark in his dream, dark and terrible.");
                        break;
                }
            }
            String[] array = list.toArray(new String[list.size()]);
            return array;
        }
    };

}
