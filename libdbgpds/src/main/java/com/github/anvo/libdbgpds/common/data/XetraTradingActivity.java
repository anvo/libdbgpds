/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.common.data;

import java.time.Instant;
import java.util.Currency;

public class XetraTradingActivity {

    public XetraTradingActivity(String isin, String symbol, String description, Currency currency, long id, Instant timestamp, float open, float high, float low, float close, float volume, int trades, Type type) {
        this.isin = isin;
        this.symbol = symbol;
        this.description = description;
        this.currency = currency;
        this.id = id;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.trades = trades;
        this.type = type;
    }

    public enum Type {
        STOCK,
        ETF,
        ETC,
        ETN
    }

    public final String isin;
    public final String symbol;
    public final String description;
    public final Currency currency;
    public final long id;
    public final Instant timestamp;
    public final float open;
    public final float high;
    public final float low;
    public final float close;
    public final float volume;
    public final int trades;
    public final Type type;

    @Override
    public String toString() {
        return "XetraTradingActivity{" +
                "isin='" + isin + '\'' +
                ", symbol='" + symbol + '\'' +
                ", description='" + description + '\'' +
                ", currency=" + currency +
                ", id=" + id +
                ", timestamp=" + timestamp +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", trades=" + trades +
                ", type=" + type +
                '}';
    }
}
