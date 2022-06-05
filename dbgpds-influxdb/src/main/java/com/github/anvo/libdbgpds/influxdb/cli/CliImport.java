/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.influxdb.cli;

import com.github.anvo.libdbgpds.influxdb.InfluxDbImport;
import picocli.CommandLine;

import java.net.ConnectException;
import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "import", description = "Imports historic data into a InfluxDB")
public class CliImport implements Callable<Integer> {

    private static final String now = LocalDate.now().toString();

    @CommandLine.ArgGroup(exclusive = true)
    private RangeOrDates rangeOrDates = new RangeOrDates();

    static class RangeOrDates {
        @CommandLine.Option(names = {"-r", "--range"},
                description = "Range until now to import data. ISO-8601 period format. Example: P1M",
                paramLabel = " PnYnMnD|PnW")
        private Period range;


        @CommandLine.ArgGroup(exclusive = false)
        ExactDates exactDates = new ExactDates();

        static class ExactDates {
            @CommandLine.Option(names = {"-f", "--from"},
                    description = "From which date to start importing data. Default: ${DEFAULT-VALUE}",
                    paramLabel = "yyyy-MM-dd")
            private LocalDate from = LocalDate.now();

            @CommandLine.Option(names = {"-u", "--until"},
                    description = "Until which date to import data. Default: ${DEFAULT-VALUE}",
                    paramLabel = "yyyy-MM-dd")
            private LocalDate until = LocalDate.now();
        }
    }

    @CommandLine.ParentCommand
    private
    Cli parent;

    @Override
    public Integer call() {
        final InfluxDbImport importer = new InfluxDbImport(parent.server, parent.org, parent.bucket, parent.token);
        try {
            if (rangeOrDates.range != null) {
                importer.importData(LocalDate.now().minus(rangeOrDates.range), LocalDate.now());
            } else {
                importer.importData(rangeOrDates.exactDates.from, rangeOrDates.exactDates.until);
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}
