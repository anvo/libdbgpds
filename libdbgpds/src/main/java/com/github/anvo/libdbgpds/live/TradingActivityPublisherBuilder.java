/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.live;

import com.github.anvo.libdbgpds.common.provider.FileProvider;
import com.github.anvo.libdbgpds.common.provider.S3FileProvider;

import java.util.concurrent.TimeUnit;

public class TradingActivityPublisherBuilder {

    private FileProvider fileProvider = new S3FileProvider();
    private long pollingTime = 1;
    private TimeUnit pollingUnit = TimeUnit.MINUTES;

    public TradingActivityPublisher build() {
        return new XetraTradingActivityPublisher(fileProvider, pollingTime, pollingUnit);
    }

    public TradingActivityPublisherBuilder setFileProvider(FileProvider fileProvider) {
        this.fileProvider = fileProvider;
        return this;
    }

    public TradingActivityPublisherBuilder setPollingInterval(long time, TimeUnit unit) {
        this.pollingTime = time;
        this.pollingUnit = unit;
        return this;
    }
}
