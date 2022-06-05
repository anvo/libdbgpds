/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.common.provider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class LocalFileDataProviderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void listSingleItem() throws IOException {
        File file = temporaryFolder.newFile("test.csv");
        Files.writeString(file.toPath(), "test");

        LocalFileProvider dataProvider = new LocalFileProvider(temporaryFolder.getRoot());
        List<FileMetaData> items = dataProvider.listFiles("");
        assertEquals(1, items.size());
        assertEquals("test.csv", items.get(0).name);
        assertEquals(4, items.get(0).size);
    }

    @Test
    public void getDataFromItem() throws IOException {
        final String fileContent = "Some random text";
        File file = temporaryFolder.newFile("content.csv");
        Files.writeString(file.toPath(), fileContent);

        LocalFileProvider dataProvider = new LocalFileProvider(temporaryFolder.getRoot());
        String readContent = dataProvider.getContent("content.csv");
        assertEquals(readContent, fileContent);
    }
}
