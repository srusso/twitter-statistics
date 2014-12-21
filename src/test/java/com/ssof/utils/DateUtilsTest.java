package com.ssof.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class DateUtilsTest {
    DateUtils dateUtils;
    
    @Before
    public void setUp() throws Exception {
        dateUtils = new DateUtils();
    }

    @Test
    public void testParseDate_validDateWithSlashes_parsedCorrectly() throws Exception {
        GregorianCalendar date = dateUtils.translateStringDate("21/12/2014");

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        assertEquals(2014, year);
        assertEquals(11, month);
        assertEquals(21, day);
    }

    @Test
    public void testParseDate_validDateWithDashes_parsedCorrectly() throws Exception {
        GregorianCalendar date = dateUtils.translateStringDate("21-12-2014");

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        assertEquals(2014, year);
        assertEquals(11, month);
        assertEquals(21, day);
    }

    @Test
    public void testParseDates_equivalentDates_parsedIntoEquivalentInstants() throws Exception {
        GregorianCalendar date1 = dateUtils.translateStringDate("21/12/2014");
        GregorianCalendar date2 = dateUtils.translateStringDate("21-12-2014");

        assertEquals(date1, date2);
    }
}