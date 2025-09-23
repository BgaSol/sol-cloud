package com.bgasol.plugin.minio.service;

import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.model.file.file.entity.FileEntity;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class OssService {
    private final MinioClient minioClient;

    /// 写入文件流到对象存储
    public void writeFileStream(InputStream inputStream, FileEntity file) {
        try {
            // 创建上传文件参数
            PutObjectArgs.Builder builder = PutObjectArgs
                    .builder()
                    .bucket(file.getBucket())
                    .object(buildObjectPath(file));
            if (ObjectUtils.isNotEmpty(file.getSize())) {
                builder.stream(inputStream, file.getSize(), -1);
            } else {
                builder.stream(inputStream, -1, 64 * 1024 * 1024);
            }
            builder.contentType(file.getType());
            PutObjectArgs objectArgs = builder.build();
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
     * @return 文件流
     */
    public InputStream readFileStream(FileEntity file) {
        try {
            GetObjectArgs build = GetObjectArgs
                    .builder()
                    .bucket(file.getBucket())
                    .object(buildObjectPath(file))
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
    public void removeFile(FileEntity file) {
        try {
            RemoveObjectArgs build = RemoveObjectArgs
                    .builder()
                    .bucket(file.getBucket())
                    .object(buildObjectPath(file))
                    .build();
            minioClient.removeObject(build);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("文件删除失败", e);
            throw new BaseException("文件删除失败");
        }
    }

    /**
     * 构建对象存储路径
     */
    private String buildObjectPath(FileEntity file) {
        return "%s/%s/%s_%s".formatted(
                file.getSource(),
                file.getCreateTime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE),
                file.getId(),
                file.getName()
        );
    }

    /**
     * 获取文件HASH
     */
    public String getFileHash(InputStream inputStream) {
        String hash;
        try {
            hash = DigestUtils.sha256Hex(inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new BaseException("获取文件HASH失败");
        }
        return hash;
    }
}
