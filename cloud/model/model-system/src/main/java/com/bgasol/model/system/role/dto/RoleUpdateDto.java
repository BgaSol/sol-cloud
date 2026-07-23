package com.bgasol.model.system.role.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.role.mapstruct.RoleMapstruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.bgasol.common.constant.value.SystemConfigValues.ADMIN_ROLE_ID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "更新角色实体")
public class RoleUpdateDto extends BaseUpdateDto<RoleEntity> {
    @NotBlank(message = "角色名不能为空")
    @Schema(description = "角色名")
    private String name;

    @Pattern(regexp = "^[^" + ADMIN_ROLE_ID + "]+$", message = "角色编码不能包含*")
    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "角色权限ID列表")
    private List<String> permissionIds;

    @Schema(description = "角色菜单ID列表")
    private List<String> menuIds;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public RoleEntity toEntity() {
        return RoleMapstruct.INSTANCE.toEntity(this);
    }
}
