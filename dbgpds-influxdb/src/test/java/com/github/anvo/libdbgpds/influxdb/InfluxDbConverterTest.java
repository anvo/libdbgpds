/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.influxdb;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.influxdb.client.write.Point;
import org.junit.Test;

import java.time.Instant;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InfluxDbConverterTest {

    @Test
    public void convertTradingActivity2Point() {
        final XetraTradingActivity tradingActivity = new XetraTradingActivity("ISIN", "Symbol", "Description", Currency.getInstance("EUR"), 0, Instant.now(), 1, 2, 3, 4, 5, 6, XetraTradingActivity.Type.STOCK);

        Point point = new InfluxDbConverter().tradingActivty2Point(tradingActivity);

        assertNotNull(point);
        assertEquals("1m,currency=EUR,isin=ISIN,symbol=Symbol,type=STOCK close=4.0,high=2.0,low=3.0,open=1.0,trades=6i,volume=5.0 " + Instant.now().getEpochSecond(), point.toLineProtocol());
    }

}
