package com.bgasol.web.file.image.service;

import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.file.file.api.FileApi;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.model.file.image.dto.ImagePageDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import com.bgasol.web.file.file.service.FileService;
import com.bgasol.web.file.file.service.OssService;
import com.bgasol.web.file.image.mapper.ImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageService extends BaseService<ImageEntity, ImagePageDto> {
    private final ImageMapper imageMapper;

    private final FileService fileService;

    private final OssService ossService;
    private final FileApi fileApi;

    @Override
    public ImageMapper commonBaseMapper() {
        return imageMapper;
    }

    @Override
    public ImageEntity save(ImageEntity entity) {
        // 获取图片文件详情
        FileEntity file = fileService.findById(entity.getFileId());
        // 获取图片文件流
        int[] imageWidthAndHeight = getImageWidthAndHeight(fileService.fileStreamFindById(file.getId()));
        entity.setWidth(imageWidthAndHeight[0]);
        entity.setHeight(imageWidthAndHeight[1]);

        return super.save(entity);
    }

    /// 获取图片宽高
    public static int[] getImageWidthAndHeight(InputStream imageStream) {
        BufferedImage image;
        try {
            image = ImageIO.read(imageStream);
            imageStream.close();
        } catch (IOException e) {
            throw new BaseException("图片读取错误");
        }
        // 设置图片宽高
        return new int[]{image.getWidth(), image.getHeight()};
    }

    @Override
    public ImageEntity update(ImageEntity imageEntity) {
        // 获取图片文件详情
        FileEntity file = fileService.findById(imageEntity.getFileId());
        // 获取图片宽高度
        int[] imageWidthAndHeight = getImageWidthAndHeight(fileService.fileStreamFindById(file.getId()));
        imageEntity.setWidth(imageWidthAndHeight[0]);
        imageEntity.setHeight(imageWidthAndHeight[1]);
        return super.update(imageEntity);
    }

    /**
     * 读取图片文件流
     */
    public InputStream imageStreamFindById(String id) {
        ImageEntity imageEntity = this.findById(id);
        FileEntity file = imageEntity.getFile();
        return ossService.readFileStream(file.getBucket(), file.getId(), file.getName());
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

    @Override
    @Transactional(readOnly = true)
    public void findOtherTable(ImageEntity entity) {
        FileEntity file = fileApi.findById(entity.getFileId()).getData();
        entity.setFile(file);
        super.findOtherTable(entity);
    }
}
