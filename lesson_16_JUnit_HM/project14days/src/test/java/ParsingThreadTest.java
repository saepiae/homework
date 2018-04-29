import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.innopolis.stc9.ParsingThread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;

public class ParsingThreadTest extends Assert{
    private static Logger logger = Logger.getLogger(ParsingThreadTest.class);
    private ParsingThread parsingThread;
    private LinkedBlockingQueue<String> queueStrings;
    private CountDownLatch latchReaders;
    private LinkedBlockingQueue<String> queueSentences;
    private CountDownLatch latchParsing;

    @Before
    public void before(){
        logger.info("preparation for the next test.");
        LinkedBlockingQueue<String> queueStrings = new LinkedBlockingQueue<>(5);
        queueStrings.add("\tThe man in front of me was tall and strong, with thick dark hair. He sat in an expensive chair behind an expensive desk, and looked at me with cold grey eyes. He didn't have time to smile.");
        queueStrings.add("");
        queueStrings.add("\tI sat back in my chair and lit a cigarette slowly.");
        queueStrings.add("No");
        queueStrings.add("Somewhere a telephone was ringing. Howard could hear it in his dream, but it didn't wake him up. It was dark in his dream, dark and terrible.");
        this.queueStrings = queueStrings;
        this.queueSentences = new LinkedBlockingQueue<>(5);
        this.latchReaders = new CountDownLatch(0);
        this.latchParsing = new CountDownLatch(1);
        String[] words = {"telephone"};
        parsingThread = new ParsingThread(words, this.queueStrings, this.latchReaders, this.queueSentences, this.latchParsing);
    }
    @Test
    public void runTestNormal(){
        String[] expected = {"Somewhere a telephone was ringing."};
        parsingThread.run();
        String[] result = queueSentences.toArray(new String[queueSentences.size()]);
        assertArrayEquals("result array differ from expected one", expected, result);
    }
}
