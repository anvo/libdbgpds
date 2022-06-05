/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.influxdb;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.github.anvo.libdbgpds.common.provider.S3FileProvider;
import com.github.anvo.libdbgpds.historic.XetraTradingActivityDownloader;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.write.events.BackpressureEvent;
import com.influxdb.client.write.events.WriteErrorEvent;
import com.influxdb.client.write.events.WriteRetriableErrorEvent;

import java.net.ConnectException;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

public class InfluxDbImport {

    private final String server;
    private final String org;
    private final String bucket;
    private final String token;

    private final InfluxDbConverter converter = new InfluxDbConverter();

    public InfluxDbImport(String server, String org, String bucket, String token) {
        this.server = server;
        this.org = org;
        this.bucket = bucket;
        this.token = token;
    }

    public void importData(LocalDate from, LocalDate until) throws ConnectException {
        InfluxDBClient influxDbClient = InfluxDBClientFactory.create(server, token.toCharArray(), org, bucket);
        if (!influxDbClient.ping()) {
            throw new ConnectException("Failed to connect to influxdb " + server);
        }
        WriteApi influxDbApi = influxDbClient.makeWriteApi();
        influxDbApi.listenEvents(BackpressureEvent.class, System.err::println);
        influxDbApi.listenEvents(WriteErrorEvent.class, System.err::println);
        influxDbApi.listenEvents(WriteRetriableErrorEvent.class, System.err::println);

        System.out.println("Downloading from " + from + " until " + until);
        AtomicLong amount = new AtomicLong();

        new XetraTradingActivityDownloader(new S3FileProvider())
                .download(from, until)
                .filter(t -> t.type == XetraTradingActivity.Type.STOCK)
                .map(converter::tradingActivty2Point)
                .forEach(t -> {
                    influxDbApi.writePoint(t);
                    amount.getAndIncrement();
                    if (amount.longValue() % 100000 == 0) {
                        System.out.println(amount + " items imported");
                    }
                });
        System.out.println("Total: " + amount + " items imported");
        influxDbClient.close();
    }
}
