import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.innopolis.stc9.pojo.Specialty;

import static org.mockito.Mockito.*;

public class SpecialtyTest extends Assert {
    final static Logger logger = Logger.getLogger(SpecialtyTest.class);
    private Specialty defaultSpecialtyMock;
    private Specialty specialtyMock;
//    private Specialty specialty = new Specialty(5, "Java Core", "JC");;

    @Before
    public void before() {
        this.defaultSpecialtyMock = mock(Specialty.class);
        when(defaultSpecialtyMock.getId()).thenReturn(0);
        when(defaultSpecialtyMock.getName()).thenReturn(null);
        when(defaultSpecialtyMock.getAbbreviation()).thenReturn(null);
        when(defaultSpecialtyMock.toString()).thenReturn("Specialty{}");
        this.specialtyMock = mock(Specialty.class);
        when(specialtyMock.getId()).thenReturn(5);
        when(specialtyMock.getName()).thenReturn("Java Core");
        when(specialtyMock.getAbbreviation()).thenReturn("JC");
        when(specialtyMock.toString()).thenReturn("Specialty{5;Java Core;JC}");
    }

    @Test
    public void SpecialtyTest_Normal() {
        logger.debug("start");
        Specialty s = new Specialty(5, "Java Core", "JC");
        assertEquals(specialtyMock.getId(), s.getId());
        verify(specialtyMock, times(1)).getId();
        assertEquals(specialtyMock.getName(), s.getName());
        verify(specialtyMock, times(1)).getName();
        assertEquals(specialtyMock.getAbbreviation(), s.getAbbreviation());
        verify(specialtyMock, times(1)).getAbbreviation();
        logger.debug("success");
    }

    @Test
    public void SpecialtyTest_IDnotRight() {
        logger.debug("start");
        Specialty s = new Specialty(0, "Java Core", "JC");
        assertEquals(defaultSpecialtyMock.getId(), s.getId());
        verify(defaultSpecialtyMock, times(1)).getId();
        assertEquals(defaultSpecialtyMock.getName(), s.getName());
        verify(defaultSpecialtyMock, times(1)).getName();
        assertEquals(defaultSpecialtyMock.getAbbreviation(), s.getAbbreviation());
        verify(defaultSpecialtyMock, times(1)).getAbbreviation();
        logger.debug("success");
    }

    @Test
    public void SpecialtyTest_CodeNotRight() {
        logger.debug("start");
        Specialty s = new Specialty(0, "Java Core", null);
        assertEquals(defaultSpecialtyMock.getId(), s.getId());
        verify(defaultSpecialtyMock, times(1)).getId();
        assertEquals(defaultSpecialtyMock.getName(), s.getName());
        verify(defaultSpecialtyMock, times(1)).getName();
        assertEquals(defaultSpecialtyMock.getAbbreviation(), s.getAbbreviation());
        verify(defaultSpecialtyMock, times(1)).getAbbreviation();
        logger.debug("success");
    }

    @Test
    public void SpecialtyTest_NameNotRight() {
        logger.debug("start");
        Specialty s = new Specialty(5, "", "JC");
        assertEquals(defaultSpecialtyMock.getId(), s.getId());
        verify(defaultSpecialtyMock, times(1)).getId();
        assertEquals(defaultSpecialtyMock.getName(), s.getName());
        verify(defaultSpecialtyMock, times(1)).getName();
        assertEquals(defaultSpecialtyMock.getAbbreviation(), s.getAbbreviation());
        verify(defaultSpecialtyMock, times(1)).getAbbreviation();
        logger.debug("success");
    }

    @Test
    public void testToString() {
        logger.debug("start");
        Specialty s = new Specialty(5, "Java Core", "JC");
        String result = s.toString();
        String expected = specialtyMock.toString();
        assertEquals(expected, result);
        logger.debug("success");
    }
    @Test
    public void testToString_NotInit() {
        logger.debug("start");
        Specialty s = new Specialty(0, "Java Core", "JC");
        String result = s.toString();
        String expected = defaultSpecialtyMock.toString();
        assertEquals(expected, result);
        logger.debug("success");
    }
}
