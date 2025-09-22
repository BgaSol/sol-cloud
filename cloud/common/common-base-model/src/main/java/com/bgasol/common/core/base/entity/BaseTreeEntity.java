package com.bgasol.common.core.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@Entity
public abstract class BaseTreeEntity<T extends BaseTreeEntity<T>> extends BaseEntity {
    public static final String PARENT_ID = "parent_id";
    @Schema(description = "父id")
    @TableField(PARENT_ID)
    @Transient
    private String parentId;

    @Schema(description = "父实体")
    @TableField(exist = false)
    @JoinColumn(name = "parent_id")
    @ManyToOne
    private T parent;

    @Schema(description = "子实体")
    @TableField(exist = false)
    @OneToMany(mappedBy = "parent")
    private List<T> children;
}