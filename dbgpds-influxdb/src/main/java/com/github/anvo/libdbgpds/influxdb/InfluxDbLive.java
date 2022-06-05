/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.influxdb;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.github.anvo.libdbgpds.live.TradingActivityPublisher;
import com.github.anvo.libdbgpds.live.TradingActivityPublisherBuilder;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.write.Point;
import com.influxdb.client.write.events.BackpressureEvent;
import com.influxdb.client.write.events.WriteErrorEvent;
import com.influxdb.client.write.events.WriteRetriableErrorEvent;

import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

public class InfluxDbLive {

    private final String server;
    private final String org;
    private final String bucket;
    private final String token;

    private final InfluxDbConverter converter = new InfluxDbConverter();

    public InfluxDbLive(String server, String org, String bucket, String token) {
        this.server = server;
        this.org = org;
        this.bucket = bucket;
        this.token = token;
    }

    public void stream() throws ConnectException {
        InfluxDBClient influxDbClient = InfluxDBClientFactory.create(server, token.toCharArray(), org, bucket);
        if (!influxDbClient.ping()) {
            throw new ConnectException("Failed to connect to influxdb " + server);
        }
        WriteApi influxDbApi = influxDbClient.makeWriteApi();
        influxDbApi.listenEvents(BackpressureEvent.class, System.err::println);
        influxDbApi.listenEvents(WriteErrorEvent.class, System.err::println);
        influxDbApi.listenEvents(WriteRetriableErrorEvent.class, System.err::println);

        TradingActivityPublisher publisher = new TradingActivityPublisherBuilder()
                .setPollingInterval(1, TimeUnit.MINUTES)
                .build();

        publisher.subscribe(t -> {
            if (t.type == XetraTradingActivity.Type.STOCK) {
                Point p = converter.tradingActivty2Point(t);
                influxDbApi.writePoint(p);
            }
        });

        System.out.println("Waiting for new trading activity");
        publisher.startBlocking();

        influxDbClient.close();
    }
}
