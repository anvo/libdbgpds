/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.common.parser;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;

import java.time.Instant;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class XetraTradingActivityParser {

    //Lines in a file that start with the following prefix, usually the first one, will be ignored.
    private static final String CSV_FIELD_DEF_PREFIX = "ISIN,";

    public XetraTradingActivity parseLine(String line) throws InvalidDataException {

        Scanner scanner = new Scanner(line);
        scanner.useDelimiter(",");

        String isin = scanner.next();
        String symbol = scanner.next();

        String description;
        if (",\"".equals(scanner.findWithinHorizon(",\"", 2))) {
            scanner.useDelimiter("\",");
            description = scanner.next();
            scanner.skip("\"");
            scanner.useDelimiter(",");
        } else {
            description = scanner.next();
        }

        XetraTradingActivity.Type type = parseType(scanner.next());
        Currency currency = Currency.getInstance(scanner.next());
        long id = scanner.nextLong();
        Instant timestamp = Instant.parse(scanner.next() + "T" + scanner.next() + ":00Z");
        float open = scanner.nextFloat();
        float high = scanner.nextFloat();
        float low = scanner.nextFloat();
        float close = scanner.nextFloat();
        float volume = scanner.nextFloat();
        int trades = scanner.nextInt();

        return new XetraTradingActivity(isin, symbol, description, currency, id, timestamp, open, high, low, close, volume, trades, type);
    }

    private XetraTradingActivity.Type parseType(String type) throws InvalidDataException {
        if ("Common stock".equals(type)) {
            return XetraTradingActivity.Type.STOCK;
        } else if ("ETF".equals(type)) {
            return XetraTradingActivity.Type.ETF;
        } else if ("ETC".equals(type)) {
            return XetraTradingActivity.Type.ETC;
        } else if ("ETN".equals(type)) {
            return XetraTradingActivity.Type.ETN;
        }
        throw new InvalidDataException("Invalid type found: " + type);
    }

    public List<XetraTradingActivity> parseLines(String lines) {
        Scanner sc = new Scanner(lines);
        List<XetraTradingActivity> result = new LinkedList<>();

        while (sc.hasNextLine()) {
            final String line = sc.nextLine();
            if (line.startsWith(CSV_FIELD_DEF_PREFIX)) {
                continue;
            }
            try {
                result.add(this.parseLine(line));
            } catch (InvalidDataException e) {
                System.out.println("Failed to parse: " + line);
                e.printStackTrace();
            }
        }

        return result;
    }
}
