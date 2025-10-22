package com.bgasol.web.file.image.service;

import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.model.file.image.dto.ImagePageDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import com.bgasol.plugin.minio.service.OssService;
import com.bgasol.web.file.file.service.FileService;
import com.bgasol.web.file.image.mapper.ImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageService extends BaseService<ImageEntity, ImagePageDto> {
    private final ImageMapper imageMapper;

    private final FileService fileService;

    private final OssService ossService;

    private final RedissonClient redissonClient;

    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

    @Override
    public ImageMapper commonBaseMapper() {
        return imageMapper;
    }

    @Override
    public ImageEntity save(ImageEntity entity) {
        // 获取图片文件详情
        if (ObjectUtils.isNotEmpty(entity.getFileId())) {
            FileEntity file = fileService.findById(entity.getFileId());
            try {
                getImageWidthAndHeight(file, entity);
            } catch (IOException e) {
                entity.setDescription(e.getMessage());
            }
        }
        return super.save(entity);
    }

    /// 获取图片宽高
    public void getImageWidthAndHeight(FileEntity fileEntity, ImageEntity imageEntity) throws IOException {
        // 获取图片文件流
        InputStream imageStream = ossService.readFileStream(fileEntity);
        BufferedImage image = ImageIO.read(imageStream);
        imageStream.close();
        imageEntity.setWidth(image.getWidth());
        imageEntity.setHeight(image.getHeight());
    }

    @Override
    public ImageEntity update(ImageEntity entity) {
        // 获取图片文件详情
        if (ObjectUtils.isNotEmpty(entity.getFileId())) {
            FileEntity file = fileService.findById(entity.getFileId());
            try {
                getImageWidthAndHeight(file, entity);
            } catch (IOException e) {
                entity.setDescription(e.getMessage());
            }
        }
        return super.update(entity);
    }

    /**
     * 读取图片文件流
     */
    public InputStream imageStreamFindById(String id) {
        ImageEntity imageEntity = this.findById(id);
        FileEntity file = imageEntity.getFile();
        return ossService.readFileStream(file);
    }

    @Override
    public void findOtherTable(List<ImageEntity> list) {
        Set<String> fileIds = list.stream()
                .map(ImageEntity::getFileId)
                .filter(ObjectUtils::isNotEmpty).collect(Collectors.toSet());

        Map<String, FileEntity> fileMap = fileService
                .findByIds(fileIds.toArray(String[]::new))
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));

        list.forEach(imageEntity -> {
            if (ObjectUtils.isNotEmpty(imageEntity.getFileId())) {
                imageEntity.setFile(fileMap.get(imageEntity.getFileId()));
            }
        });
    }

    /**
     * 删除图片
     */
    @Override
    public Integer delete(String id) {
        ImageEntity imageEntity = this.findById(id);
        FileEntity file = imageEntity.getFile();

        Integer delete = super.delete(id);
        fileService.delete(file.getId());
        return delete;
    }
}
