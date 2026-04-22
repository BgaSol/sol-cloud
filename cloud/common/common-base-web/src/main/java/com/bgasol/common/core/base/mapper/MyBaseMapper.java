package com.bgasol.common.core.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bgasol.common.core.base.dto.RelationRow;
import com.bgasol.common.core.base.entity.BaseEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

/**
 * 通用基础 Mapper。
 * <p>
 * 在 {@link BaseMapper} 的基础上，补充了按表名、字段名执行通用查询/插入/删除的方法。
 * 适用于有中间表结构的操作
 * </p>
 *
 * @param <T> 实体类型，需继承 {@link BaseEntity}
 */
@Mapper
public interface MyBaseMapper<T extends BaseEntity> extends BaseMapper<T> {

    /**
     * 根据主字段值查询从字段值列表。
     *
     * @param tableName   目标表名
     * @param masterName  主字段名（条件字段）
     * @param masterValue 主字段值（等值查询）
     * @param slaveName   从字段名（返回字段）
     * @return 从字段值列表
     */
    @Select("select ${slaveName} from ${tableName} where ${masterName} = #{masterValue}")
    List<String> findFromTable(@Param("tableName") String tableName,
                               @Param("masterName") String masterName,
                               @Param("masterValue") String masterValue,
                               @Param("slaveName") String slaveName);

    /**
     * 批量根据主字段值集合查询键值对。
     *
     * @param tableName    目标表名
     * @param masterName   主字段名（作为 key）
     * @param masterValues 主字段值集合（IN 查询）
     * @param slaveName    从字段名（作为 value）
     * @return 键值对列表（key=主字段值，value=从字段值）
     */
    @Select("""
            <script>
                SELECT
                    ${masterName} AS sourceId,
                    ${slaveName}  AS targetId
                FROM ${tableName}
                WHERE ${masterName} IN
                <foreach collection='masterValues' item='val' open='(' separator=',' close=')'>
                   #{val}
                </foreach>
            </script>
            """)
    List<RelationRow> findFromTableBatch(
            @Param("tableName") String tableName,
            @Param("masterName") String masterName,
            @Param("masterValues") Set<String> masterValues,
            @Param("slaveName") String slaveName
    );

    /**
     * 批量插入主从字段键值对。
     *
     * @param tableName  目标表名
     * @param masterName 主字段名
     * @param slaveName  从字段名
     * @param pairList   键值对集合（key 对应主字段，value 对应从字段）
     */
    @Insert("""
            <script>
            INSERT INTO ${tableName} (${masterName}, ${slaveName}) VALUES
            <foreach collection='pairList' item='item' separator=','>
               (#{item.sourceId}, #{item.targetId})
            </foreach>
            </script>
            """)
    void insertIntoTableBatch(@Param("tableName") String tableName,
                              @Param("masterName") String masterName,
                              @Param("slaveName") String slaveName,
                              @Param("pairList") List<RelationRow> pairList);


    /**
     * 根据主字段值删除记录。
     *
     * @param tableName   目标表名
     * @param masterName  主字段名（条件字段）
     * @param masterValue 主字段值（等值删除）
     */
    @Delete("delete from ${tableName} where ${masterName} = #{masterValue}")
    void deleteFromTable(@Param("tableName") String tableName,
                         @Param("masterName") String masterName,
                         @Param("masterValue") String masterValue);

}
