/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.influxdb.cli;

import com.github.anvo.libdbgpds.influxdb.InfluxDbLive;
import picocli.CommandLine;

import java.net.ConnectException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "live",
        description = "Streams live data into a InfluxDB")
public class CliLive implements Callable<Integer> {

    @CommandLine.ParentCommand
    Cli parent;

    @Override
    public Integer call() {
        final InfluxDbLive live = new InfluxDbLive(parent.server, parent.org, parent.bucket, parent.token);
        try {
            live.stream();
        } catch (ConnectException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}
