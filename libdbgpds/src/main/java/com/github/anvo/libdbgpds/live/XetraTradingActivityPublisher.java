/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.live;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.github.anvo.libdbgpds.common.provider.FileProvider;
import com.github.anvo.libdbgpds.live.detector.NewTradingActivityDetector;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

class XetraTradingActivityPublisher implements TradingActivityPublisher {

    private final NewTradingActivityDetector detector;
    private final long pollingTime;
    private final TimeUnit pollingUnit;

    private final Set<TradingActivityReceiver> receivers = Collections.synchronizedSet(new HashSet<>());
    private boolean isRunning = false;

    public XetraTradingActivityPublisher(FileProvider fileProvider, long pollingTime, TimeUnit pollingUnit) {
        this.detector = new NewTradingActivityDetector(fileProvider);
        this.pollingTime = pollingTime;
        this.pollingUnit = pollingUnit;
    }

    @Override
    public void subscribe(TradingActivityReceiver receiver) {
        this.receivers.add(receiver);
    }

    @Override
    public void unsubscribe(TradingActivityReceiver receiver) {
        this.receivers.remove(receiver);
    }

    @Override
    public void startBlocking() {
        if (this.isRunning) {
            return;
        }

        System.out.println("Deutsche Börse Public Dataset was accessed on " + LocalDate.now() + " from https://registry.opendata.aws/deutsche-boerse-pds.");
        System.out.println("Dataset License: Non-commercial (NC) - licensees may copy, distribute, display, and perform the work and make derivative works and remixes based on it only for non-commercial purposes.");


        this.isRunning = true;
        while (this.isRunning) {
            periodicWatch();
            try {
                System.out.println("Sleeping for " + pollingTime + " " + pollingUnit);
                Thread.sleep(pollingUnit.toMillis(pollingTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }


    @Override
    public void stop() {
        this.isRunning = false;
    }

    private void periodicWatch() {
        final String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<XetraTradingActivity> newActivity = detector.retrieveNewTradingActivity(today);
        for (TradingActivityReceiver receiver : this.receivers) {
            for (XetraTradingActivity activity : newActivity) {
                receiver.onNewTradingActivityReceived(activity);
            }
        }
    }
}
