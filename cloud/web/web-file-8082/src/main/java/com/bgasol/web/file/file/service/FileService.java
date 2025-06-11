package com.bgasol.web.file.file.service;

import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.file.file.dto.FilePageDto;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.plugin.minio.config.MinioConfig;
import com.bgasol.web.file.file.mapper.FileMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.activation.MimetypesFileTypeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileService extends BaseService<FileEntity, FilePageDto> {
    private final FileMapper fileMapper;

    private final MinioConfig minioConfig;

    private final MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

    private final OssService ossService;

    private final RedissonClient redissonClient;

    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

    @Override
    public FileMapper commonBaseMapper() {
        return fileMapper;
    }

    /**
     * 从本地路径保存文件
     *
     * @param file 文件
     * @return 文件实体
     */
    public FileEntity save(File file) throws IOException {

        // 初始化文件实体
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(file.getName());
        fileEntity.setSize(file.length());
        fileEntity.setType(fileTypeMap.getContentType(file.getName()));
        fileEntity.setBucket(minioConfig.getBucket());
        // 获取文件后缀
        fileEntity.setSuffix(this.getSuffix(fileEntity.getName()));
        // 获取文件HASH
        fileEntity.setHash(this.getFileHash(FileUtils.openInputStream(file)));

        // 保存文件实体
        fileEntity = this.save(fileEntity);
        // 上传文件
        ossService.writeFileStream(fileEntity.getBucket(), fileEntity.getId(), fileEntity.getName(), FileUtils.openInputStream(file), fileEntity.getSize(), fileEntity.getType());
        return fileEntity;
    }

    /**
     * 通过文件流保存文件
     */
    public FileEntity save(MultipartFile multipartFile, FileEntity fileEntity) {
        if (ObjectUtils.isEmpty(multipartFile)) {
            return this.save(fileEntity);
        }
        // 获取文件元数据
        getFileMateDate(multipartFile, fileEntity);
        // 保存文件实体
        fileEntity = this.save(fileEntity);
        // 上传文件
        try (InputStream inputStream = multipartFile.getInputStream()) {
            ossService.writeFileStream(fileEntity.getBucket(), fileEntity.getId(), fileEntity.getName(), inputStream, fileEntity.getSize(), fileEntity.getType());
        } catch (IOException e) {
            throw new BaseException("上传文件失败");
        }
        return fileEntity;
    }

    public FileEntity update(MultipartFile multipartFile, FileEntity fileEntity) {
        if (ObjectUtils.isEmpty(multipartFile)) {
            return this.update(fileEntity);
        }
        // 获取文件元数据
        getFileMateDate(multipartFile, fileEntity);
        // 更新文件实体
        fileEntity = this.update(fileEntity);
        // 上传文件
        try (InputStream inputStream = multipartFile.getInputStream()) {
            ossService.writeFileStream(fileEntity.getBucket(), fileEntity.getId(), fileEntity.getName(), inputStream, fileEntity.getSize(), fileEntity.getType());
        } catch (IOException e) {
            throw new BaseException("上传文件失败");
        }
        return fileEntity;
    }

    public void getFileMateDate(MultipartFile multipartFile, FileEntity fileEntity) {
        fileEntity.setName(multipartFile.getOriginalFilename());
        fileEntity.setSize(multipartFile.getSize());
        fileEntity.setType(multipartFile.getContentType());
        fileEntity.setBucket(minioConfig.getBucket());
        // 获取文件后缀
        fileEntity.setSuffix(this.getSuffix(fileEntity.getName()));
        // 获取文件HASH
        try (InputStream inputStream = multipartFile.getInputStream()) {
            fileEntity.setHash(this.getFileHash(inputStream));
        } catch (IOException e) {
            throw new BaseException("获取文件HASH失败");
        }
    }

    /**
     * 获取文件名后缀
     */
    public String getSuffix(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        int i = fileName.lastIndexOf(".");
        if (i == -1) {
            return null;
        }
        return fileName.substring(i + 1);
    }

    /**
     * 生成文件名
     */
    public String getFileName(FileEntity fileEntity) {
        String fileName = fileEntity.getName();
        if (ObjectUtils.isEmpty(fileName)) {
            fileName = fileEntity.getId();
        }
        if (!fileName.contains(".")) {
            fileName = fileName + "." + fileEntity.getSuffix();
        }
        return fileName;
    }

    /**
     * 删除文件
     *
     * @param id 文件id
     * @return 删除数量
     */
    @Override
    public Integer delete(String id) {
        FileEntity fileEntity = this.findById(id);
        if (fileEntity == null) {
            throw new BaseException("文件不存在");
        }
        ossService.removeFile(fileEntity.getBucket(), fileEntity.getId());
        return super.delete(id);
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

    /**
     * 读取文件流
     */
    public InputStream fileStreamFindById(String id) {
        FileEntity file = this.findById(id);
        return ossService.readFileStream(file.getBucket(), file.getId(), file.getName());
    }
}
