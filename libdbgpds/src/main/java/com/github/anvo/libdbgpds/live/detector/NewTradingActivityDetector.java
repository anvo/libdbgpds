/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.live.detector;

import com.github.anvo.libdbgpds.common.data.XetraTradingActivity;
import com.github.anvo.libdbgpds.common.parser.XetraTradingActivityParser;
import com.github.anvo.libdbgpds.common.provider.FileMetaData;
import com.github.anvo.libdbgpds.common.provider.FileProvider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class NewTradingActivityDetector {
    private final FileProvider dataProvider;
    private final NewFileDetector newFileDetector;
    private final XetraTradingActivityParser parser = new XetraTradingActivityParser();

    public NewTradingActivityDetector(FileProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.newFileDetector = new NewFileDetector(dataProvider);
    }

    public List<XetraTradingActivity> retrieveNewTradingActivity(String folder) {
        List<FileMetaData> newFiles = this.newFileDetector.listNewFiles(folder);

        List<XetraTradingActivity> results = new LinkedList<>();
        for (FileMetaData file : newFiles) {
            try {
                final String content = this.dataProvider.getContent(file.name);
                results.addAll(this.parser.parseLines(content));
            } catch (IOException e) {
                System.out.println("Failed to download " + file);
            }
        }
        return results;
    }
}
