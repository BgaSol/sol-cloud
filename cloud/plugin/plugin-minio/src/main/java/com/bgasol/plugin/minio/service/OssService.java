package com.bgasol.plugin.minio.service;

import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.model.file.file.api.FileApi;
import com.bgasol.model.file.file.dto.FileUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import io.minio.*;
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
    private final FileApi fileApi;

    /// 写入文件流到对象存储
    public void writeFileStream(InputStream inputStream, FileEntity file) {
        // 创建上传文件参数
        PutObjectArgs.Builder pubBuilder = PutObjectArgs
                .builder()
                .bucket(file.getBucket())
                .object(buildObjectPath(file))
                .contentType(file.getType());
        if (ObjectUtils.isEmpty(file.getSize())) {
            pubBuilder.stream(inputStream, -1, 64 * 1024 * 1024);
        } else {
            pubBuilder.stream(inputStream, file.getSize(), -1);
        }
        PutObjectArgs putObj = pubBuilder.build();
        // 上传文件到minio id 相同会覆盖
        try {
            minioClient.putObject(putObj);
            inputStream.close();
        } catch (ErrorResponseException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException |
                 InsufficientDataException e) {
            throw new BaseException("文件写入失败", e);
        }

        // 回填文件大小
        if (ObjectUtils.isNotEmpty(file.getSize())) {
            return;
        }
        // 上传完成后获取对象信息
        StatObjectResponse stat = statFile(file);

        fileApi.apply(FileUpdateDto.builder()
                .id(file.getId())
                .size(stat.size())
                .build());

    }

    public StatObjectResponse statFile(FileEntity file) {
        StatObjectArgs build = StatObjectArgs
                .builder()
                .bucket(file.getBucket())
                .object(buildObjectPath(file))
                .build();
        try {
            return minioClient.statObject(build);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new BaseException("获取文件信息失败", e);
        }
    }

    /**
     * 从对象存储获取文件流
     *
     * @return 文件流
     */
    public InputStream readFileStream(FileEntity file) throws IOException {
        GetObjectArgs build = GetObjectArgs
                .builder()
                .bucket(file.getBucket())
                .object(buildObjectPath(file))
                .build();
        try {
            return minioClient.getObject(build);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new BaseException("文件读取失败 id: " + file.getId(), e);
        }
    }

    /**
     * 从对象存储中移除文件
     */
    public void removeFile(FileEntity file) {
        RemoveObjectArgs build = RemoveObjectArgs
                .builder()
                .bucket(file.getBucket())
                .object(buildObjectPath(file))
                .build();
        try {
            minioClient.removeObject(build);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new BaseException("文件删除失败", e);
        }
    }

    /**
     * 构建对象存储路径
     */
    public String buildObjectPath(FileEntity file) {
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
            throw new BaseException("获取文件HASH失败", e);
        }
        return hash;
    }
}
