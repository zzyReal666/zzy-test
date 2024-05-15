package com.zzy;


import org.junit.Test;

import java.sql.*;

public class MainTest {

    //测试JDBC连接ClickHouse
    @Test
    public void test() throws ClassNotFoundException, SQLException {
        //JDBC连接ClickHouse
        String host = "192.168.7.113";
        String port = "18123";
        String database = "default";
        String username = "default";
        String password = "123456";


        //加载驱动
        Class.forName("com.clickhouse.jdbc.ClickHouseDriver");



//        String url = "jdbc:clickhouse://" + host + ":" + port + "/" + database +"?compress_algorithm=gzip";  //不用lz压缩
        String url = "jdbc:clickhouse://" + host + ":" + port + "/" + database;
        System.out.println("url: " + url);

        //获取连接
        Connection connection = DriverManager.getConnection(url, username, password);

        assert connection != null;

        String sql = "CREATE TABLE IF NOT EXISTS t_order\n" +
                "(\n" +
                "    order_id   UUID DEFAULT generateUUIDv4(),\n" +
                "    order_type Int32,\n" +
                "    user_id    Int32,\n" +
                "    address_id UInt64,\n" +
                "    status     String\n" +
                ") ENGINE = MergeTree order by order_id";

        //获取Statement
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getString(4) + " ");
        }


    }


}