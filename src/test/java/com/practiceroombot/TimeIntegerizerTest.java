package com.practiceroombot;

import com.practiceroombot.TimeIntegerizer;
import org.junit.Test;
import static org.junit.Assert.*;

public class TimeIntegerizerTest {

    @Test
    public void testIntegerizeTimeForFirstMondayEvening() {
        // Test for "7:30 PM" on "Monday" which is considered the start of the reservation week
        int index730pm = TimeIntegerizer.integerizeTime("7:30PM", "Monday");
        assertEquals(0, index730pm); // Should be 0, starting index for First Monday
    }

    @Test
    public void testIntegerizeTimeForLastMondayEvening() {
        // Test for "7:00 PM" on "Monday" which is the last slot before the 7:30 PM shift
        int index700pm = TimeIntegerizer.integerizeTime("7:00PM", "Monday");
        assertEquals(335, index700pm); // Should be 335, last index for Last Monday
    }

    @Test
    public void testIntegerizeTimeForLast430() {
        // Test for "7:00 PM" on "Monday" which is the last slot before the 7:30 PM shift
        int index700pm = TimeIntegerizer.integerizeTime("4:30PM", "Monday");
        assertEquals(330, index700pm); // Should be 335, last index for Last Monday
    }
}

