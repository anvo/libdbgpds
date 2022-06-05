/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.live.detector;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.github.anvo.libdbgpds.common.provider.FileProvider;
import com.github.anvo.libdbgpds.common.provider.LocalFileProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class NewTradingActivityDetectorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void detectNewTradingActivity() throws IOException {

        final FileProvider dataProvider = new LocalFileProvider(this.tempFolder.getRoot());

        NewTradingActivityDetector newDataDetector = new NewTradingActivityDetector(dataProvider);
        List<XetraTradingActivity> newData = newDataDetector.retrieveNewTradingActivity("");
        assertEquals("First call is always empty", 0, newData.size());

        File newContent = tempFolder.newFile("newcontent.csv");

        FileWriter writer = new FileWriter(newContent);
        writer.append("ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n");
        writer.append("DK0060534915,NOVC,\"NOVO-NORDISK NAM.B DK-,20\",Common stock,EUR,2505141,2022-04-07,15:19,110.1,110.1,109.8,109.8,104,3\n");
        writer.append("IE00BLRPRG98,NGXL,WITR MU.AS.I.NTG ETP 2062,ETC,EUR,5902363,2022-04-07,15:19,261.9,261.9,261.9,261.9,1,1\n");
        writer.flush();

        newData = newDataDetector.retrieveNewTradingActivity("");
        assertEquals("Should detect all new data", 2, newData.size());
        assertEquals("DK0060534915", newData.get(0).isin);
        assertEquals("IE00BLRPRG98", newData.get(1).isin);

        newData = newDataDetector.retrieveNewTradingActivity("");
        assertEquals("Should not report data twice", 0, newData.size());
    }
}
