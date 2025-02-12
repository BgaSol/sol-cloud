package com.bgasol.common.core.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MyBaseMapper<T> extends BaseMapper<T> {
    /**
     * 查询中间表数据
     *
     * @param tableName   中间表名
     * @param masterName  主表主键名
     * @param masterValue 主表主键值
     * @param slaveName   从表主键名
     */
    @Select("select ${slaveName} from ${tableName} where ${masterName} = #{masterValue}")
    List<String> findFromTable(@Param("tableName") String tableName,
                               @Param("masterName") String masterName,
                               @Param("masterValue") String masterValue,
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
