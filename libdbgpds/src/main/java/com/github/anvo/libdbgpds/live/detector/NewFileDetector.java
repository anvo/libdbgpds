/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.live.detector;

import com.github.anvo.libdbgpds.common.provider.FileMetaData;
import com.github.anvo.libdbgpds.common.provider.FileProvider;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Detects new files using the provided {@link FileProvider}.
 * <p>
 * The class is stateful. The state is updated every time {@link #listNewFiles(String)} is called.
 * The first call to {@link #listNewFiles(String)} will not report any new file.
 */
class NewFileDetector {

    private final FileProvider dataProvider;
    private List<FileMetaData> knownFiles = null;

    public NewFileDetector(FileProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public synchronized List<FileMetaData> listNewFiles(String folder) {
        if (this.knownFiles == null) {
            this.knownFiles = this.dataProvider.listFiles(folder);
            return Collections.emptyList();
        }
        List<FileMetaData> currentFiles = this.dataProvider.listFiles(folder);
        List<FileMetaData> newFiles = currentFiles.stream().filter(s -> !knownFiles.contains(s)).collect(Collectors.toList());
        this.knownFiles = currentFiles;
        return newFiles;
    }
}
