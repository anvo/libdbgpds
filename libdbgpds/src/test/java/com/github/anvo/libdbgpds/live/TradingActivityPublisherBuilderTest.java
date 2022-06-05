/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.live;

import com.github.anvo.libdbgpds.common.provider.S3FileProvider;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

public class TradingActivityPublisherBuilderTest {

    @Test
    public void usage() {
        TradingActivityPublisher publisher = new TradingActivityPublisherBuilder()
                .setFileProvider(new S3FileProvider())
                .setPollingInterval(1, TimeUnit.MINUTES)
                .build();

        assertNotNull(publisher);
    }
}
