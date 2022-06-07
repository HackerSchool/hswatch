package io.hackerschool.hswatch_connection_module.connection_objects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;

public class HSWConnectionTest {

    HSWConnection hswConnection = new HSWConnection();

    @Test
    public void dayOfTheWeekForConnectionTestSunday() {
        LocalDate sundayTest = LocalDate.of(2022, 06, 12);
        assertEquals(
                hswConnection.getDayOfTheWeekNumber(sundayTest.getDayOfWeek().toString()),
                "1"
        );
    }

    @Test
    public void dayOfTheWeekForConnectionTestMonday() {
        LocalDate mondayTest = LocalDate.of(2022, 6, 13);
        assertEquals(
                hswConnection.getDayOfTheWeekNumber(mondayTest.getDayOfWeek().toString()),
                "2"
        );
    }

    @Test
    public void dayOfTheWeekForConnectionTestTuesday() {
        LocalDate tuesdayTest = LocalDate.of(2022, 6, 14);
        assertEquals(
                hswConnection.getDayOfTheWeekNumber(tuesdayTest.getDayOfWeek().toString()),
                "3"
        );
    }

    @Test
    public void dayOfTheWeekForConnectionTestWednesday() {
        LocalDate wednesdayTest = LocalDate.of(2022, 6, 15);
        assertEquals(
                hswConnection.getDayOfTheWeekNumber(wednesdayTest.getDayOfWeek().toString()),
                "4"
        );
    }

    @Test
    public void dayOfTheWeekForConnectionTestThursday() {
        LocalDate thursdayTest = LocalDate.of(2022, 6, 16);
        assertEquals(
                hswConnection.getDayOfTheWeekNumber(thursdayTest.getDayOfWeek().toString()),
                "5"
        );
    }

    @Test
    public void dayOfTheWeekForConnectionTestFriday() {
        LocalDate fridayTest = LocalDate.of(2022, 6, 17);
        assertEquals(
                hswConnection.getDayOfTheWeekNumber(fridayTest.getDayOfWeek().toString()),
                "6"
        );
    }

    @Test
    public void dayOfTheWeekForConnectionTestSaturday() {
        LocalDate saturdayTest = LocalDate.of(2022, 6, 18);
        assertEquals(
                hswConnection.getDayOfTheWeekNumber(saturdayTest.getDayOfWeek().toString()),
                "7"
        );
    }
}
