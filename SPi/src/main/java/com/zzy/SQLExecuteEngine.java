package com.zzy;

import spi.typed.TypedSPI;

import java.sql.Connection;

/**
 * @author zzypersonally@gmail.com
 * @description        SQL执行引擎
 * @since 2024/4/9 16:38
 */
public interface SQLExecuteEngine extends TypedSPI {

    //获取连接
    Connection getConnection();

    //判断是否存在主键
    boolean isExistPrimaryKey();

    //获取主键字段
    String getPrimaryKey();

    //新建临时字段
    void createTempField();

    //删除指定字段
    void deleteField(String fieldName);

    //分页查询
    void queryByPage(int pageNum, int pageSize);

    //

}
