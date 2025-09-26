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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bgasol.common.constant.value.RedisConfigValues.DEFAULT_TIME_UNIT;
import static com.bgasol.common.constant.value.RedisConfigValues.randomizeTtl;

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
     * 查询所有树形结构（一次性查全量 + 内存组装）
     *
     * @param parentId 父id，可为空；为空时返回所有根节点树
     * @return 树形结构列表
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findTreeAll(String parentId, QueryWrapper<ENTITY> queryWrapper) {
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }

        // 一次性全量查出
        List<ENTITY> entities = commonBaseMapper().selectList(queryWrapper);

        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        // 缓存查询结果（整批存）
        if (ObjectUtils.isNotEmpty(commonBaseRedissonClient())) {
            RMapCache<String, ENTITY> mapCache = getRMapCache();
            mapCache.putAll(
                    entities.stream().collect(Collectors.toMap(BaseEntity::getId, e -> e)),
                    randomizeTtl(), DEFAULT_TIME_UNIT
            );
        }

        this.findOtherTable(entities);

        // id -> entity 映射
        Map<String, ENTITY> entityMap = entities.stream()
                .collect(Collectors.toMap(BaseEntity::getId, e -> e));

        // 组装树结构
        List<ENTITY> roots = new ArrayList<>();
        for (ENTITY entity : entities) {
            String pid = entity.getParentId();
            if (pid == null || pid.isEmpty()) {
                roots.add(entity);
            } else {
                ENTITY parent = entityMap.get(pid);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(entity);
                }
            }
        }

        if (parentId != null && !parentId.isEmpty()) {
            return entityMap.containsKey(parentId)
                    ? entityMap.get(parentId).getChildren()
                    : Collections.emptyList();
        }

        return roots;
    }
}
