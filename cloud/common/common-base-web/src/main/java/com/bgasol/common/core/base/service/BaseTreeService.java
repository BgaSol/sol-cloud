package com.bgasol.common.core.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bgasol.common.core.base.entity.BaseTreeTable.PARENT_ID;

@Slf4j
@Service
public abstract class BaseTreeService<ENTITY extends BaseTreeEntity<ENTITY>, PAGE_DTO extends BasePageDto<ENTITY>> extends BaseService<ENTITY, PAGE_DTO> {

    @Override
    @Transactional(readOnly = true)
    public Page<ENTITY> findByPage(Page<ENTITY> page, Wrapper<ENTITY> queryWrapper, boolean otherData) {
        Page<ENTITY> entityPage = super.findByPage(page, queryWrapper, otherData);
        addChildren(entityPage.getRecords(), otherData);
        return entityPage;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ENTITY> findAll(boolean otherData) {
        QueryWrapper<ENTITY> qw = Wrappers.<ENTITY>query()
                .nested(w -> w.isNull(PARENT_ID).or().eq(PARENT_ID, ""));
        return this.findAll(qw, otherData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ENTITY> findAll(Wrapper<ENTITY> wrapper, Boolean otherData) {
        List<ENTITY> all = super.findAll(wrapper, otherData);
        addChildren(all, otherData);
        return all;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ENTITY> findById(Set<String> ids, boolean otherData) {
        List<ENTITY> entityList = super.findById(ids, otherData);
        addChildren(entityList, otherData);
        return entityList;
    }

    @Transactional(readOnly = true)
    public void addChildren(List<ENTITY> entityList, boolean otherData) {
        if (ObjectUtils.isEmpty(entityList)) {
            return;
        }

        Set<String> parentIds = entityList.stream()
                .map(ENTITY::getId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());
        QueryWrapper<ENTITY> qw = Wrappers.<ENTITY>query().in(PARENT_ID, parentIds);

        List<ENTITY> entities = this.findAll(qw, otherData);

        if (ObjectUtils.isEmpty(entities)) {
            return;
        }

        Map<String, List<ENTITY>> childrenMap = entities
                .stream()
                .collect(Collectors.groupingBy(ENTITY::getParentId));

        entityList.forEach(entity -> {
            List<ENTITY> children = childrenMap.get(entity.getId());
            if (ObjectUtils.isNotEmpty(children)) {
                entity.setChildren(children);
            }
        });

        this.addChildren(entities, otherData);
    }
}
