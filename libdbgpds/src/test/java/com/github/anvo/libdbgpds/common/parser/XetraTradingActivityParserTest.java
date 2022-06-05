/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.common.parser;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import org.junit.Test;

import java.time.Instant;
import java.util.Currency;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class XetraTradingActivityParserTest {

    @Test
    public void parseLineWithStock() throws InvalidDataException {
        final String line = "DE000A3E5D64,FPE3,FUCHS PETROLUB VZO NA ON,Common stock,EUR,6699158,2022-04-07,15:19,30.73,30.79,30.72,30.78,486,3";

        final XetraTradingActivity tradingActivity = new XetraTradingActivityParser().parseLine(line);

        assertEquals("DE000A3E5D64", tradingActivity.isin);
        assertEquals("FPE3", tradingActivity.symbol);
        assertEquals("FUCHS PETROLUB VZO NA ON", tradingActivity.description);
        assertEquals(XetraTradingActivity.Type.STOCK, tradingActivity.type);
        assertEquals(Currency.getInstance("EUR"), tradingActivity.currency);
        assertEquals(6699158L, tradingActivity.id);
        assertEquals(Instant.parse("2022-04-07T15:19:00Z"), tradingActivity.timestamp);
        assertEquals(30.73f, tradingActivity.open);
        assertEquals(30.79f, tradingActivity.high);
        assertEquals(30.72f, tradingActivity.low);
        assertEquals(30.78f, tradingActivity.close);
        assertEquals(486.0f, tradingActivity.volume);
        assertEquals(3, tradingActivity.trades);
    }

    @Test
    public void parseLineWithEtf() throws InvalidDataException {
        final String line = "IE00BKX55T58,VGVE,VANG.FTSE DEV.W.U.ETF DLD,ETF,EUR,2749237,2022-04-07,15:19,79.89,79.89,79.89,79.89,1905,1";

        final XetraTradingActivity tradingActivity = new XetraTradingActivityParser().parseLine(line);

        assertEquals("IE00BKX55T58", tradingActivity.isin);
        assertEquals("VGVE", tradingActivity.symbol);
        assertEquals("VANG.FTSE DEV.W.U.ETF DLD", tradingActivity.description);
        assertEquals(XetraTradingActivity.Type.ETF, tradingActivity.type);
        assertEquals(Currency.getInstance("EUR"), tradingActivity.currency);
        assertEquals(2749237, tradingActivity.id);
        assertEquals(Instant.parse("2022-04-07T15:19:00Z"), tradingActivity.timestamp);
        assertEquals(79.89f, tradingActivity.open);
        assertEquals(79.89f, tradingActivity.high);
        assertEquals(79.89f, tradingActivity.low);
        assertEquals(79.89f, tradingActivity.close);
        assertEquals(1905.0f, tradingActivity.volume);
        assertEquals(1, tradingActivity.trades);
    }

    @Test
    public void parseLineWithEtc() throws InvalidDataException {
        final String line = "IE00BLRPRG98,NGXL,WITR MU.AS.I.NTG ETP 2062,ETC,EUR,5902363,2022-04-07,15:19,261.9,261.9,261.9,261.9,1,1";
        final XetraTradingActivity tradingActivity = new XetraTradingActivityParser().parseLine(line);

        assertEquals("IE00BLRPRG98", tradingActivity.isin);
        assertEquals("NGXL", tradingActivity.symbol);
        assertEquals("WITR MU.AS.I.NTG ETP 2062", tradingActivity.description);
        assertEquals(XetraTradingActivity.Type.ETC, tradingActivity.type);
        assertEquals(Currency.getInstance("EUR"), tradingActivity.currency);
        assertEquals(5902363, tradingActivity.id);
        assertEquals(Instant.parse("2022-04-07T15:19:00Z"), tradingActivity.timestamp);
        assertEquals(261.9f, tradingActivity.open);
        assertEquals(261.9f, tradingActivity.high);
        assertEquals(261.9f, tradingActivity.low);
        assertEquals(261.9f, tradingActivity.close);
        assertEquals(1.0f, tradingActivity.volume);
        assertEquals(1, tradingActivity.trades);
    }

    @Test
    public void parseLineWithEtn() throws InvalidDataException {
        final String line = "IE00BLRPRL42,3QQQ,WITR MU.AS.I.NA100 3X 62,ETN,EUR,5902378,2022-04-11,07:27,136.81,136.9,136.81,136.9,2,2";
        final XetraTradingActivity tradingActivity = new XetraTradingActivityParser().parseLine(line);

        assertEquals("IE00BLRPRL42", tradingActivity.isin);
        assertEquals("3QQQ", tradingActivity.symbol);
        assertEquals("WITR MU.AS.I.NA100 3X 62", tradingActivity.description);
        assertEquals(XetraTradingActivity.Type.ETN, tradingActivity.type);
        assertEquals(Currency.getInstance("EUR"), tradingActivity.currency);
        assertEquals(5902378, tradingActivity.id);
        assertEquals(Instant.parse("2022-04-11T07:27:00Z"), tradingActivity.timestamp);
        assertEquals(136.81f, tradingActivity.open);
        assertEquals(136.9f, tradingActivity.high);
        assertEquals(136.81f, tradingActivity.low);
        assertEquals(136.9f, tradingActivity.close);
        assertEquals(2.0f, tradingActivity.volume);
        assertEquals(2, tradingActivity.trades);
    }

    @Test
    public void parseLineWithQuotes() throws InvalidDataException {
        final String line = "DK0060534915,NOVC,\"NOVO-NORDISK NAM.B DK-,20\",Common stock,EUR,2505141,2022-04-07,15:19,110.1,110.1,109.8,109.8,104,3";

        final XetraTradingActivity tradingActivity = new XetraTradingActivityParser().parseLine(line);

        assertEquals("NOVO-NORDISK NAM.B DK-,20", tradingActivity.description);
    }

    @Test
    public void parseMultiLine() throws InvalidDataException {
        final String lines = "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                "DE000A2NBX80,INS,INSTONE REAL EST.GRP O.N.,Common stock,EUR,3422276,2022-04-07,15:19,15.9,15.9,15.88,15.88,213,3\n" +
                "DE0006095003,ECV,ENCAVIS AG  INH. O.N.,Common stock,EUR,2505012,2022-04-07,15:19,20.81,20.85,20.81,20.85,2084,9";


        List<XetraTradingActivity> tradingActivities = new XetraTradingActivityParser().parseLines(lines);

        assertEquals("Should ignore first line", 2, tradingActivities.size());
        assertEquals("DE000A2NBX80", tradingActivities.get(0).isin);
        assertEquals("DE0006095003", tradingActivities.get(1).isin);
    }

    @Test
    public void invalidDateInMultiLinesShouldNotAbortAll() {
        final String lines = "ISIN,Mnemonic,SecurityDesc,SecurityType,Currency,SecurityID,Date,Time,StartPrice,MaxPrice,MinPrice,EndPrice,TradedVolume,NumberOfTrades\n" +
                "DE000A2NBX80,INS,INSTONE REAL EST.GRP O.N.,INVALID,EUR,3422276,2022-04-07,15:19,15.9,15.9,15.88,15.88,213,3\n" +
                "DE0006095003,ECV,ENCAVIS AG  INH. O.N.,Common stock,EUR,2505012,2022-04-07,15:19,20.81,20.85,20.81,20.85,2084,9";


        List<XetraTradingActivity> tradingActivities = new XetraTradingActivityParser().parseLines(lines);

        assertEquals("Should ignore first line", 1, tradingActivities.size());
        assertEquals("DE0006095003", tradingActivities.get(0).isin);
    }

}
