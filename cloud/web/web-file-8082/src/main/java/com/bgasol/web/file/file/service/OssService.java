package com.bgasol.web.file.file.service;

import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.model.file.file.entity.FileEntity;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OssService {
    private final MinioClient minioClient;

    private static final String FILE_SEPARATOR = ":";

    /**
     * 写入文件流到对象存储
     */
    public void writeFileStream(InputStream inputStream, FileEntity file) {
        try {
            LocalDate localDate = file.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String dateStr = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "/";

            String source = file.getSource();
            source = ObjectUtils.isEmpty(source) ? "" : source + "/";

            // 创建上传文件参数
            PutObjectArgs objectArgs = PutObjectArgs
                    .builder()
                    .bucket(file.getBucket())
                    .object(dateStr + source + file.getId() + FILE_SEPARATOR + file.getName())
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getType())
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
     * @return 文件流
     */
    @Transactional(readOnly = true)
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
        Date createTime = file.getCreateTime();
        LocalDate localDate = createTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String dateStr = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "/";

        String source = file.getSource();
        source = ObjectUtils.isEmpty(source) ? "" : source + "/";

        return dateStr + source + file.getId() + FILE_SEPARATOR + file.getName();
    }
}
