package com.ssof.utils;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {
    DateUtils dateUtils;
    
    @Before
    public void setUp() throws Exception {
        dateUtils = new DateUtils();
    }

    @Test
    public void testParseDate_validDateWithSlashes_parsedCorrectly() throws Exception {
        DateTime date = dateUtils.translateStringDate("21/12/2014");

        assertEquals(2014, date.getYear());
        assertEquals(12, date.getMonthOfYear());
        assertEquals(21, date.getDayOfMonth());
    }

    @Test
    public void testParseDate_validDateWithDashes_parsedCorrectly() throws Exception {
        DateTime date = dateUtils.translateStringDate("21-12-2014");

        assertEquals(2014, date.getYear());
        assertEquals(12, date.getMonthOfYear());
        assertEquals(21, date.getDayOfMonth());
    }

    @Test
    public void testParseDates_equivalentDates_parsedIntoEquivalentInstants() throws Exception {
        DateTime date1 = dateUtils.translateStringDate("21/12/2014");
        DateTime date2 = dateUtils.translateStringDate("21-12-2014");

        assertEquals(date1, date2);
    }
}