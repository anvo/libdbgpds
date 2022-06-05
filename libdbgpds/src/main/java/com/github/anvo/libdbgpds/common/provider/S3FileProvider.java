/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.common.provider;

import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class S3FileProvider implements FileProvider {

    private final static String BUCKET_NAME = "deutsche-boerse-xetra-pds";

    private final S3Client s3Client = S3Client.builder()
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(AnonymousCredentialsProvider.create())
            .build();

    @Override
    public List<FileMetaData> listFiles(String folder) {
        final ListObjectsRequest objectsRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME)
                .prefix(folder)
                .build();
        final ListObjectsResponse response = this.s3Client.listObjects(objectsRequest);
        return response.contents().stream().map(this::toMetaData).collect(Collectors.toList());
    }

    @Override
    public String getContent(String file) throws IOException {
        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(file)
                .build();
        final ResponseInputStream<GetObjectResponse> inputStream = this.s3Client.getObject(getObjectRequest);
        String response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        inputStream.close();
        return response;
    }

    private FileMetaData toMetaData(S3Object s3Object) {
        return new FileMetaData(s3Object.key(), s3Object.size());
    }
}
