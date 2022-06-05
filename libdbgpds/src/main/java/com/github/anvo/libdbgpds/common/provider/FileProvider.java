/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.common.provider;

import java.io.IOException;
import java.util.List;

public interface FileProvider {
    List<FileMetaData> listFiles(String folder);

    String getContent(String file) throws IOException;

}
