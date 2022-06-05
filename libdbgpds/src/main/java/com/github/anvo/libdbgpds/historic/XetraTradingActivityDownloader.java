/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.historic;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.github.anvo.libdbgpds.common.parser.XetraTradingActivityParser;
import com.github.anvo.libdbgpds.common.provider.FileMetaData;
import com.github.anvo.libdbgpds.common.provider.FileProvider;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class XetraTradingActivityDownloader {
    private static final LocalDate XETRA_UPLOAD_START = LocalDate.parse("2017-06-26");
    private static final long XETRA_EMPTY_FILE_SIZE = 136;

    private final FileProvider fileProvider;
    private final XetraTradingActivityParser parser = new XetraTradingActivityParser();

    public XetraTradingActivityDownloader(FileProvider fileProvider) {
        this.fileProvider = fileProvider;
    }

    public Stream<XetraTradingActivity> download(LocalDate from, LocalDate to) {
        System.out.println("Deutsche Börse Public Dataset was accessed on " + LocalDate.now() + " from https://registry.opendata.aws/deutsche-boerse-pds.");
        System.out.println("Dataset License: Non-commercial (NC) - licensees may copy, distribute, display, and perform the work and make derivative works and remixes based on it only for non-commercial purposes.");

        return LocalDateStream.until(from, to)
                .filter(d -> !d.isBefore(XETRA_UPLOAD_START))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .flatMap(this::listFiles)
                .filter(f -> f.size > XETRA_EMPTY_FILE_SIZE)
                .map(this::downloadContent)
                .flatMap(this::parseContent);
    }

    private Stream<FileMetaData> listFiles(LocalDate date) {
        return this.fileProvider.listFiles(date.format(DateTimeFormatter.ISO_LOCAL_DATE)).stream();
    }

    private String downloadContent(FileMetaData hourFile) {
        try {
            return fileProvider.getContent(hourFile.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Stream<XetraTradingActivity> parseContent(String lines) {
        return this.parser.parseLines(lines).stream();
    }
}
