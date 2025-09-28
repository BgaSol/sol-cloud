package com.bgasol.common.core.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bgasol.common.core.base.entity.BaseEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface MyBaseMapper<T extends BaseEntity> extends BaseMapper<T> {
    /**
     * 获取中间表 被查询主键值 列表
     *
     * @param tableName   中间表名
     * @param masterName  查询主键名
     * @param masterValue 查询主键值
     * @param slaveName   被查询主键名
     */
    @Select("select ${slaveName} from ${tableName} where ${masterName} = #{masterValue}")
    List<String> findFromTable(@Param("tableName") String tableName,
                               @Param("masterName") String masterName,
                               @Param("masterValue") String masterValue,
                               @Param("slaveName") String slaveName);

    /**
     * 获取中间表 被查询主键值 列表（支持多个 masterValue）
     * <p>
     * 返回值：每一行是一个 Map，包含 master -> 值, slave -> 值
     *
     * @param tableName    中间表名
     * @param masterName   查询主键名
     * @param masterValues 查询主键值列表
     * @param slaveName    被查询主键名
     */
    @Select({
            "<script>",
            "select ${masterName}, ${slaveName}",
            "from ${tableName}",
            "where ${masterName} in",
            "<foreach collection='masterValues' item='val' open='(' separator=',' close=')'>",
            "   #{val}",
            "</foreach>",
            "</script>"
    })
    List<Map<String, String>> findFromTableBatch(@Param("tableName") String tableName,
                                                 @Param("masterName") String masterName,
                                                 @Param("masterValues") List<String> masterValues,
                                                 @Param("slaveName") String slaveName);

    /**
     * 插入中间表数据
     *
     * @param tableName   中间表名
     * @param masterName  主表主键名
     * @param masterValue 主表主键值
     * @param slaveName   从表主键名
     * @param slaveValue  从表主键值
     */
    @Insert("insert into ${tableName} (${masterName}, ${slaveName}) values (#{masterValue}, #{slaveValue})")
    void insertIntoTable(@Param("tableName") String tableName,
                         @Param("masterName") String masterName,
                         @Param("masterValue") String masterValue,
                         @Param("slaveName") String slaveName,
                         @Param("slaveValue") String slaveValue);

    /**
     * 批量插入中间表数据
     *
     * @param tableName  中间表名
     * @param masterName 主表主键名
     * @param slaveName  从表主键名
     * @param values     每个元素是 {masterValue, slaveValue}
     */
    @Insert({

            "<script>",
            "INSERT INTO ${tableName} (${masterName}, ${slaveName})",
            "VALUES",
            "<foreach collection='values' item='item' separator=','>",
            "   (#{item.masterValue}, #{item.slaveValue})",
            "</foreach>",
            "</script>"
    })
    void insertIntoTableBatch(@Param("tableName") String tableName,
                              @Param("masterName") String masterName,
                              @Param("slaveName") String slaveName,
                              @Param("values") List<Map<String, String>> values);

    /**
     * 删除中间表数据
     *
     * @param tableName  中间表名
     * @param masterName 主表主键名
     * @param id         主表主键值
     */
    @Delete("delete from ${tableName} where ${masterName} = #{id}")
    void deleteFromTable(@Param("tableName") String tableName,
                         @Param("masterName") String masterName,
                         @Param("id") String id);

    /**
     * 清空中间表
     *
     * @param tableName 中间表名
     */
    @Delete("TRUNCATE TABLE ${tableName} CASCADE")
    void truncateTable(@Param("tableName") String tableName);
}
