/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.influxdb;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxDbConverter {
    public Point tradingActivty2Point(XetraTradingActivity tradingActivity) {
        return Point.measurement("1m")
                .addTag("currency", tradingActivity.currency.getCurrencyCode())
                .addTag("isin", tradingActivity.isin)
                .addTag("symbol", tradingActivity.symbol)
                .addTag("type", tradingActivity.type.name())
                .addField("open", tradingActivity.open)
                .addField("high", tradingActivity.high)
                .addField("low", tradingActivity.low)
                .addField("close", tradingActivity.close)
                .addField("volume", tradingActivity.volume)
                .addField("trades", tradingActivity.trades)
                .time(tradingActivity.timestamp, WritePrecision.S);
    }
}
