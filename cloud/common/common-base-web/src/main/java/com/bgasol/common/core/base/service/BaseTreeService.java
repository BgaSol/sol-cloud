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

import java.util.*;
import java.util.stream.Collectors;

import static com.bgasol.common.constant.value.RedisConfigValues.DEFAULT_TIME_UNIT;
import static com.bgasol.common.constant.value.RedisConfigValues.randomizeTtl;

@Slf4j
@Service
public abstract class BaseTreeService<ENTITY extends BaseTreeEntity<ENTITY>, PAGE_DTO extends BasePageDto<ENTITY>> extends BaseService<ENTITY, PAGE_DTO> {

    /**
     * 根据id查询实体
     * 无关联查询
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findDirectByIds(String... idArray) {
        List<ENTITY> all = this.findAll();
        // 递归查询idArray所有节点
        List<ENTITY> result = new ArrayList<>();
        filterResult(all, idArray, result);
        return result;
    }

    private void filterResult(List<ENTITY> all, String[] idArray, List<ENTITY> result) {
        for (ENTITY entity : all) {
            if (Arrays.asList(idArray).contains(entity.getId())) {
                result.add(entity);
            }
            if (ObjectUtils.isNotEmpty(entity.getChildren())) {
                filterResult(entity.getChildren(), idArray, result);
            }
        }
    }

    /**
     * 查询所有实体
     * 如果实体是树形结构，查询所有根节点
     *
     * @return 实体列表
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findAll() {
        // 一次性全量查出
        List<ENTITY> entities = commonBaseMapper().selectList(new QueryWrapper<>());

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

        // id -> entity 映射
        Map<String, ENTITY> entityMap = entities.stream()
                .collect(Collectors.toMap(BaseEntity::getId, e -> e));

        // 组装树结构
        List<ENTITY> roots = new ArrayList<>();
        for (ENTITY entity : entities) {
            String parentId = entity.getParentId();
            if (ObjectUtils.isEmpty(parentId)) {
                roots.add(entity);
            } else {
                ENTITY parent = entityMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(entity);
                }
            }
        }
        return roots;
    }
}
