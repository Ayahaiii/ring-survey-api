package com.monetware.ringsurvey.system.base;

import com.monetware.ringsurvey.system.mapper.ExtendMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @Author: Cookie
 * @Date: 2019/1/15 10:37
 * @Description: 通用Mapper继承类
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T>, ExtendMapper<T> {
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
}
