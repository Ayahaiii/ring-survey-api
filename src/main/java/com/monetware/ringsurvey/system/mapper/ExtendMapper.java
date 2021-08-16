package com.monetware.ringsurvey.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * @author Simo
 * @date 2019-06-10
 */
@RegisterMapper
public interface ExtendMapper<T> {

    @UpdateProvider(type = ExtendProvider.class, method = "dynamicSQL")
    int createCustomTable(@Param("tableName") String tableName);

    @UpdateProvider(type = ExtendProvider.class, method = "dynamicSQL")
    int dropCustomTable(@Param("tableName") String tableName);

}
