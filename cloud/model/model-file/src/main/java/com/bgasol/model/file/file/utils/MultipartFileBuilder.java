package com.bgasol.model.file.file.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFileBuilder {
    public static MultipartFile build(String fileName, byte[] bytes, String mediaType) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return fileName;
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return mediaType;
            }

            @Override
            public boolean isEmpty() {
                return bytes.length == 0;
            }

            @Override
            public long getSize() {
                return bytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return bytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayResource(bytes).getInputStream();
            }

            @Override
            public void transferTo(File dest) throws IllegalStateException {
                throw new UnsupportedOperationException("Not implemented");
            }
        };
    }
}
