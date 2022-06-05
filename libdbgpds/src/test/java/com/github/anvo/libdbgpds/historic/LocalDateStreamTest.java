/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.historic;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class LocalDateStreamTest {

    @Test
    public void rangeBetween() {
        Stream<LocalDate> dateStream = LocalDateStream.until(LocalDate.parse("2022-04-01"), LocalDate.parse("2022-04-03"));
        List<LocalDate> dateList = dateStream.collect(Collectors.toList());
        assertEquals("3 days expected", 3, dateList.size());
        assertEquals(LocalDate.parse("2022-04-01"), dateList.get(0));
        assertEquals(LocalDate.parse("2022-04-02"), dateList.get(1));
        assertEquals(LocalDate.parse("2022-04-03"), dateList.get(2));
    }

    @Test
    public void oneYear() {
        Stream<LocalDate> dateStream = LocalDateStream.until(LocalDate.parse("2022-04-01"), LocalDate.parse("2023-04-01"));
        assertEquals(365 + 1 /*incl first and last day*/, dateStream.count());
    }

    @Test
    public void negativeDifference() {
        Stream<LocalDate> dateStream = LocalDateStream.until(LocalDate.parse("2022-04-01"), LocalDate.parse("2022-03-01"));
        assertEquals("No dates expected", 0, dateStream.count());
    }

    @Test
    public void extremeValues() {
        Stream<LocalDate> dateStream = LocalDateStream.until(LocalDate.parse("0001-01-01"), LocalDate.parse("9999-12-31"));
        assertTrue("At least some days expected", dateStream.count() > 0);
    }

    @Test
    public void sameDay() {
        Stream<LocalDate> dateStream = LocalDateStream.until(LocalDate.parse("2022-04-01"), LocalDate.parse("2022-04-01"));
        assertEquals(1, dateStream.count());
    }
}
