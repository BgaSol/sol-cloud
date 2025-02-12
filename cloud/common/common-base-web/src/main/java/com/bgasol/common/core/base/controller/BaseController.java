package com.bgasol.common.core.base.controller;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public abstract class BaseController<
        ENTITY extends BaseEntity,
        PAGE_DTO extends BasePageDto<ENTITY>,
        CREATE_DTO extends BaseCreateDto<ENTITY>,
        UPDATE_DTO extends BaseUpdateDto<ENTITY>
        > {
    abstract public BaseService<ENTITY, PAGE_DTO> commonBaseService();

    public BaseVo<PageVo<ENTITY>> findByPage(@Valid PAGE_DTO pageDto) {
        PageVo<ENTITY> byPage = commonBaseService().findByPage(pageDto);
        return BaseVo.success(byPage);
    }

    public BaseVo<ENTITY> save(@Valid CREATE_DTO createDto) {
        ENTITY save = commonBaseService().save(createDto.toEntity());
        return BaseVo.success(save, "保存成功");
    }

    public BaseVo<ENTITY> update(@Valid UPDATE_DTO updateDto) {
        ENTITY update = commonBaseService().update(updateDto.toEntity());
        return BaseVo.success(update, "更新成功");
    }

    public BaseVo<Integer[]> delete(@Valid @NotBlank String ids) {
        String[] idsArr = ids.split(",");
        Integer[] delete = commonBaseService().delete(idsArr);
        return BaseVo.success(delete, "删除成功");
    }

    public BaseVo<ENTITY> findById(@Valid @NotBlank String id) {
        ENTITY byId = commonBaseService().findById(id);
        return BaseVo.success(byId);
    }

    public BaseVo<List<ENTITY>> findAll() {
        List<ENTITY> all = commonBaseService().findAll();
        return BaseVo.success(all);
    }

}
