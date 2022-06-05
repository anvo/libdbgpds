/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.common.provider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFileProvider implements FileProvider {
    private final File folder;

    public LocalFileProvider(File folder) {
        this.folder = folder;
    }

    @Override
    public List<FileMetaData> listFiles(String folder) {
        File[] files = this.folder.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files).filter(s -> s.getName().startsWith(folder)).map(this::toMetaData).collect(Collectors.toList());
    }

    @Override
    public String getContent(String file) throws IOException {
        return Files.readString(this.folder.toPath().resolve(file));
    }

    private FileMetaData toMetaData(File file) {
        long fileSize = 0;
        try {
            fileSize = Files.size(file.toPath());
        } catch (IOException e) {
            System.out.println("Failed to get size of file " + file);
            e.printStackTrace();
        }
        return new FileMetaData(file.getName(), fileSize);
    }
}
