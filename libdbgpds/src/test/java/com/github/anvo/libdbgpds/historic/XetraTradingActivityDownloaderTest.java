/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.historic;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.github.anvo.libdbgpds.common.provider.LocalFileProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;

public class XetraTradingActivityDownloaderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void downloadTradingActivity() throws IOException {
        XetraTradingActivityDownloader downloader = new XetraTradingActivityDownloader(new LocalFileProvider(tempFolder.getRoot()));

        createFile("2022-04-13_11.csv",
                "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                        "NL0015436031,5CV,CUREVAC N.V.   O.N.,Common stock,EUR,5550038,2022-04-13,11:57,16.438,16.438,16.438,16.438,257,1\n");
        createFile("2022-04-14_11.csv",
                "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                        "DE0007236101,SIE,SIEMENS AG  NA O.N.,Common stock,EUR,2505088,2022-04-14,11:00,115.74,115.74,115.72,115.72,871,8\n");

        LocalDate from = LocalDate.parse("2022-04-13");
        LocalDate to = LocalDate.parse("2022-04-14");
        List<XetraTradingActivity> tradingActivity = downloader.download(from, to).collect(Collectors.toList());

        assertEquals("Should download 2 entries", 2, tradingActivity.size());
        assertEquals("NL0015436031", tradingActivity.get(0).isin);
        assertEquals("DE0007236101", tradingActivity.get(1).isin);
    }

    @Test
    public void noFileAvailable() {
        XetraTradingActivityDownloader downloader = new XetraTradingActivityDownloader(new LocalFileProvider(tempFolder.getRoot()));

        Stream<XetraTradingActivity> tradingActivity = downloader.download(LocalDate.now(), LocalDate.now());

        assertEquals("Should contain no entries", 0, tradingActivity.count());
    }

    @Test
    public void twoYears() throws IOException {
        XetraTradingActivityDownloader downloader = new XetraTradingActivityDownloader(new LocalFileProvider(tempFolder.getRoot()));

        createFile("2022-04-12_11.csv",
                "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                        "NL0015436031,5CV,CUREVAC N.V.   O.N.,Common stock,EUR,5550038,2022-04-12,11:57,16.438,16.438,16.438,16.438,257,1\n");
        createFile("2023-04-12_11.csv",
                "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                        "DE000A3E5D56,FPE,FUCHS PETROLUB NA ST O.N.,Common stock,EUR,6699157,2023-04-12,11:19,24.75,24.75,24.75,24.75,465,5\n");
        createFile("2024-04-12_11.csv",
                "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                        "DE0007236101,SIE,SIEMENS AG  NA O.N.,Common stock,EUR,2505088,2022-04-12,11:00,115.74,115.74,115.72,115.72,871,8\n");

        LocalDate from = LocalDate.parse("2022-04-12");
        LocalDate to = LocalDate.parse("2024-04-12");
        List<XetraTradingActivity> tradingActivity = downloader.download(from, to).collect(Collectors.toList());

        assertEquals("Should download 3 entries", 3, tradingActivity.size());
        assertEquals("NL0015436031", tradingActivity.get(0).isin);
        assertEquals("DE000A3E5D56", tradingActivity.get(1).isin);
        assertEquals("DE0007236101", tradingActivity.get(2).isin);
    }

    @Test
    public void excludeWeekend() throws IOException {
        XetraTradingActivityDownloader downloader = new XetraTradingActivityDownloader(new LocalFileProvider(tempFolder.getRoot()));

        createFile("2022-04-09_11.csv",
                "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                        "DU0012345678,001,Dummy,Common stock,EUR,001,2022-04-09,11:57,1.1,1.1,1.1,1.1,1,1\n");
        createFile("2022-04-10_11.csv",
                "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                        "DU0012345679,001,Dummy,Common stock,EUR,001,2022-04-10,11:57,1.1,1.1,1.1,1.1,1,1\n");

        LocalDate from = LocalDate.parse("2022-04-09");
        LocalDate to = LocalDate.parse("2022-04-10");
        Stream<XetraTradingActivity> tradingActivity = downloader.download(from, to);

        assertEquals("Should contain no entries", 0, tradingActivity.count());
    }

    @Test
    public void excludeDaysBeforeDataWasUploaded() throws IOException {
        XetraTradingActivityDownloader downloader = new XetraTradingActivityDownloader(new LocalFileProvider(tempFolder.getRoot()));

        createFile("2017-06-23_11.csv",
                "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                        "DU0012345678,001,Dummy,Common stock,EUR,001,2017-06-25,11:57,1.1,1.1,1.1,1.1,1,1\n");
        createFile("2017-06-26_11.csv",
                "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                        "DU0012345679,001,Dummy,Common stock,EUR,001,2017-06-26,11:57,1.1,1.1,1.1,1.1,1,1\n");

        LocalDate from = LocalDate.parse("2017-06-23");
        LocalDate to = LocalDate.parse("2017-06-26");
        Stream<XetraTradingActivity> tradingActivity = downloader.download(from, to);

        assertEquals("Should contain one entry", 1, tradingActivity.count());
    }

    @Test
    // Empty files are defined as 136 bytes in size (only header)
    public void excludeEmptyFiles() throws IOException {
        XetraTradingActivityDownloader downloader = new XetraTradingActivityDownloader(new LocalFileProvider(tempFolder.getRoot()));

        // Header omitted
        createFile("2022-04-08_11.csv",
                "DU0012345679,001,Dummy,Common stock,EUR,001,2022-04-08,11:57,1.1,1.1,1.1,1.1,1,1\n");

        LocalDate from = LocalDate.parse("2022-04-08");
        LocalDate to = LocalDate.parse("2022-04-08");
        Stream<XetraTradingActivity> tradingActivity = downloader.download(from, to);

        assertEquals("Should contain no entries", 0, tradingActivity.count());
    }


    private void createFile(String name, String content) throws IOException {
        File newContent = tempFolder.newFile(name);
        FileWriter writer = new FileWriter(newContent);
        writer.append(content);
        writer.flush();
    }
}
