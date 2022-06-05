/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.historic;

import java.time.LocalDate;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Provides a stream that iterates from the given {@link LocalDate} to the
 * given {@link LocalDate}. Both dates are included in the stream.
 */
public class LocalDateStream {
    public static Stream<LocalDate> until(LocalDate from, LocalDate toIncl) {
        final long days = from.until(toIncl, DAYS);
        return LongStream.rangeClosed(0, days).mapToObj(from::plusDays);
    }
}
