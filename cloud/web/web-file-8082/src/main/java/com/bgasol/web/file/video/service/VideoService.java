package com.bgasol.web.file.video.service;

import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.model.file.video.dto.VideoPageDto;
import com.bgasol.model.file.video.entity.VideoEntity;
import com.bgasol.web.file.file.service.FileService;
import com.bgasol.web.file.video.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService extends BaseService<VideoEntity, VideoPageDto> {
    private final VideoMapper videoMapper;

    private final FileService fileService;

    private final RedissonClient redissonClient;

    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

    @Override
    public VideoMapper commonBaseMapper() {
        return videoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void findOtherTable(List<VideoEntity> list) {
        Set<String> fileIds = list.stream()
                .map(VideoEntity::getFileId)
                .filter(ObjectUtils::isNotEmpty).collect(Collectors.toSet());

        Map<String, FileEntity> fileMap = fileService
                .findByIds(fileIds.toArray(String[]::new))
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));

        list.forEach(videoEntity -> {
            if (ObjectUtils.isNotEmpty(videoEntity.getFileId())) {
                videoEntity.setFile(fileMap.get(videoEntity.getFileId()));
            }
        });
    }

    /**
     * 删除视频
     */
    @Override
    @Transactional()
    public Integer delete(String id) {
        VideoEntity videoEntity = this.findById(id);
        if (ObjectUtils.isNotEmpty(videoEntity.getFile())) {
            FileEntity file = videoEntity.getFile();
            fileService.delete(file.getId());
        }
        return super.delete(id);
    }
}
