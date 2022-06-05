/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.influxdb.cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "dbgpds-influxdb",
        mixinStandardHelpOptions = true,
        subcommands = {CommandLine.HelpCommand.class, CliImport.class, CliLive.class},
        version = "0.1.0")
public class Cli implements Callable<Integer> {

    @CommandLine.Option(names = {"-b", "--bucket"},
            description = "InfluxDB bucket or use environment variable DBGPDS_INFLUXDB_BUCKET",
            defaultValue = "${env:DBGPDS_INFLUXDB_BUCKET}",
            required = true,
            scope = CommandLine.ScopeType.INHERIT)
    String bucket;

    @CommandLine.Option(names = {"-o", "--org"},
            description = "InfluxDB org or use environment variable DBGPDS_INFLUXDB_ORG",
            defaultValue = "${env:DBGPDS_INFLUXDB_ORG}",
            required = true,
            scope = CommandLine.ScopeType.INHERIT)
    String org;

    @CommandLine.Option(names = {"-s", "--server",},
            description = "InfluxDB server address or use environment variable DBGPDS_INFLUXDB_SERVER",
            defaultValue = "${env:DBGPDS_INFLUXDB_SERVER}",
            paramLabel = "http://<host>:<port>",
            required = true,
            scope = CommandLine.ScopeType.INHERIT)
    String server;

    @CommandLine.Option(names = {"-t", "--token"},
            description = "InfluxDB token or use environment variable DBGPDS_INFLUXDB_TOKEN",
            defaultValue = "${env:DBGPDS_INFLUXDB_TOKEN}",
            required = true,
            scope = CommandLine.ScopeType.INHERIT)
    String token;


    @Override
    public Integer call() throws Exception {
        return null;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Cli()).execute(args);
        System.exit(exitCode);
    }
}
