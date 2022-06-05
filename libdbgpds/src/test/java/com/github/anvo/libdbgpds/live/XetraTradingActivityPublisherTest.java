/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.live;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.github.anvo.libdbgpds.common.provider.LocalFileProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class XetraTradingActivityPublisherTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private final String TODAY = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

    @Test
    public void publishNewActivity() throws InterruptedException, IOException {
        BlockingQueue<XetraTradingActivity> queue = new LinkedBlockingQueue<>();

        TradingActivityPublisher publisher = new XetraTradingActivityPublisher(new LocalFileProvider(tempFolder.getRoot()), 1, TimeUnit.SECONDS);

        publisher.subscribe(queue::add);

        new Thread(
                publisher::startBlocking
        ).start();

        // Wait until first call to files has been done
        Thread.sleep(250);

        //Add data
        File newContent = tempFolder.newFile(TODAY + "newcontent.csv");

        FileWriter writer = new FileWriter(newContent);
        writer.append("DK0060534915,NOVC,\"NOVO-NORDISK NAM.B DK-,20\",Common stock,EUR,2505141,2022-04-07,15:19,110.1,110.1,109.8,109.8,104,3\n");
        writer.flush();

        XetraTradingActivity tradingActivity = queue.poll(1, TimeUnit.SECONDS);
        assertNotNull("No activity published", tradingActivity);
        assertEquals("DK0060534915", tradingActivity.isin);

        publisher.stop();
    }

    @Test
    public void unsubscribe() throws InterruptedException, IOException {
        BlockingQueue<XetraTradingActivity> queue = new LinkedBlockingQueue<>();

        TradingActivityPublisher publisher = new XetraTradingActivityPublisher(new LocalFileProvider(tempFolder.getRoot()), 1, TimeUnit.SECONDS);

        TradingActivityReceiver receiver = queue::add;
        publisher.subscribe(receiver);

        new Thread(
                publisher::startBlocking
        ).start();

        // Wait until first call to files has been done
        //Thread.sleep(250);

        publisher.unsubscribe(receiver);

        //Add data
        File newContent = tempFolder.newFile(TODAY + "newcontent.csv");
        FileWriter writer = new FileWriter(newContent);
        writer.append("DK0060534915,NOVC,\"NOVO-NORDISK NAM.B DK-,20\",Common stock,EUR,2505141,2022-04-07,15:19,110.1,110.1,109.8,109.8,104,3\n");
        writer.flush();

        XetraTradingActivity tradingActivity = queue.poll(1, TimeUnit.SECONDS);
        assertNull("No activity should be published", tradingActivity);

        publisher.stop();
    }
}
