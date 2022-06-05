/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.live.detector;

import com.github.anvo.libdbgpds.common.provider.FileMetaData;
import com.github.anvo.libdbgpds.common.provider.LocalFileProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class NewFileDetectorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void detectNewFile() throws IOException {

        final String folder = ""; //root
        NewFileDetector newFileDetector = new NewFileDetector(new LocalFileProvider(tempFolder.getRoot()));
        List<FileMetaData> newFiles = newFileDetector.listNewFiles(folder);
        assertEquals("No new files should be detected", 0, newFiles.size());

        tempFolder.newFile("newfile.test");

        newFiles = newFileDetector.listNewFiles(folder);
        assertEquals("New file should be detected", 1, newFiles.size());
        assertEquals("newfile.test", newFiles.get(0).name);

        newFiles = newFileDetector.listNewFiles(folder);
        assertEquals("File should no longer be detected", 0, newFiles.size());
    }

    @Test
    public void ignoreExistingFile() throws IOException {
        tempFolder.newFile("existing.file");

        final String folder = ""; //root
        NewFileDetector newFileDetector = new NewFileDetector(new LocalFileProvider(tempFolder.getRoot()));

        List<FileMetaData> newFiles = newFileDetector.listNewFiles(folder);
        assertEquals("Existing file should not be reported", 0, newFiles.size());

        tempFolder.newFile("new.file");

        newFiles = newFileDetector.listNewFiles(folder);
        assertEquals("New file should be detected", 1, newFiles.size());
        assertEquals("new.file", newFiles.get(0).name);
    }
}
