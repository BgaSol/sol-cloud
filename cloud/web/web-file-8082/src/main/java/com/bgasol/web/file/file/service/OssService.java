package com.bgasol.web.file.file.service;

import com.bgasol.common.core.base.exception.BaseException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OssService {
    private final MinioClient minioClient;

    /**
     * 写入文件流到对象存储
     */
    public void writeFileStream(String bucket, String id, String name, InputStream inputStream, Long size, String type) {
        try {
            // 创建上传文件参数
            PutObjectArgs objectArgs = PutObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(id + ":" + name)
                    .stream(inputStream, size, -1)
                    .contentType(type)
                    .build();
            // 上传文件到minio id 相同会覆盖
            minioClient.putObject(objectArgs);
            inputStream.close();
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("文件写入失败", e);
            throw new BaseException("文件写入失败");
        }
    }

    /**
     * 从对象存储获取文件流
     *
     * @param id 文件id
     * @return 文件流
     */
    @Transactional(readOnly = true)
    public InputStream readFileStream(String bucket, String id, String name) {
        try {
            GetObjectArgs build = GetObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(id + ":" + name)
                    .build();
            // 获取文件流
            return minioClient.getObject(build);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("文件读取失败", e);
            throw new BaseException("文件读取失败");
        }
    }

    /**
     * 从对象存储中移除文件
     */
    public void removeFile(String bucket, String id) {
        try {
            RemoveObjectArgs build = RemoveObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(id)
                    .build();
            minioClient.removeObject(build);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("文件删除失败", e);
            throw new BaseException("文件删除失败");
        }
    }
}
