package com.bgasol.common.core.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RMapCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.bgasol.common.constant.value.RedisConfigValues.DEFAULT_TIME_UNIT;
import static com.bgasol.common.constant.value.RedisConfigValues.randomizeTtl;
import static com.bgasol.common.core.base.entity.BaseTreeEntity.PARENT_ID;

@Transactional
@Slf4j
@Service
public abstract class BaseTreeService<ENTITY extends BaseTreeEntity<ENTITY>, PAGE_DTO extends BasePageDto<ENTITY>> extends BaseService<ENTITY, PAGE_DTO> {

    /**
     * 查询所有实体
     * 如果实体是树形结构，查询所有根节点
     *
     * @return 实体列表
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findAll() {
        return this.findTreeAll(null, null);
    }

    /**
     * 查询所有树形结构
     *
     * @param parentId 父id
     * @return 树形结构列表
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findTreeAll(String parentId, QueryWrapper<ENTITY> queryWrapper) {
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }
        if (parentId == null) {
            queryWrapper.isNull(PARENT_ID).or().eq(PARENT_ID, "");
        } else {
            queryWrapper.eq(PARENT_ID, parentId);
        }
        List<ENTITY> entities = commonBaseMapper().selectList(queryWrapper);

        // 缓存查询结果
        if (ObjectUtils.isNotEmpty(commonBaseRedissonClient())) {
            RMapCache<String, ENTITY> mapCache = getRMapCache();
            mapCache.putAll(
                    entities.stream().collect(Collectors.toMap(BaseEntity::getId, entity -> entity)),
                    randomizeTtl(), DEFAULT_TIME_UNIT);
        }

        this.findOtherTable(entities);
        for (ENTITY treeEntity : entities) {
            treeEntity.setChildren(this.findTreeAll(treeEntity.getId(), null));
        }
        return entities;
    }
}
