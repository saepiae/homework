package ru.innopolis.stc9;

import org.apache.log4j.Logger;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Общие инструменты, чтобы не писать их в каждом pojo
 */
public class Tools {
    final static Logger logger = Logger.getLogger(Tools.class);
    public static boolean isCorrect(int id){
        boolean result = id > 0;
        logger.debug("is validate integer NOT NULL data? " + result);
        return result;
    }
    public static boolean isCorrect(String string){
        boolean result = string!=null && !string.equals("");
        logger.debug("is validate String NOT NULL data? " + result);
        return result;
    }

    /**
     * Проверяет, что дата меньше или равна текущей дате
     * @param approvalDate
     * @return
     */
    public static boolean isCorrect(LocalDate approvalDate){
        boolean result = approvalDate != null;
        if (result) {
            int period = approvalDate.until(LocalDate.now()).getDays();
            result = result && period >= 0;
        }
        logger.debug("is valid date? " + result);
        return result;
    }
    public static boolean notNull(Object o){
        boolean result = o!=null;
        logger.debug("is null object? "+!result);
        return result;
    }
    public static LocalDate parseToDate(String date){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        LocalDate localDate = null;
        try {
            localDate = LocalDate.parse(date, dateFormat);
            logger.debug("LocalDate was parsed");
            return localDate;
        } catch (DateTimeParseException e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public static LocalDate dateFromSQL(Date date){
        return date.toLocalDate();
    }

}
