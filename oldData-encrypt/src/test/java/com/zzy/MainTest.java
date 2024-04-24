package com.zzy;


import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainTest {

    String url = "jdbc:mysql://192.168.7.113:13306/db_enc_test";
    String username = "root";
    String password = "123456";
    Connection connection = null;


    //环境搭建
    @Before
    public void before() throws Exception {
        // 环境准备 -- 创建业务表student --业务表添加数据
        environment();
        //创建 业务库中的事务表
        Statement statement = connection.createStatement();
        String createTransactionTableSQL = "create table transaction_status  (\n" + "    id            int primary key,\n" + "    tableName     varchar(50),\n" + "    block_index   varchar(100),\n" + "    transaction_status boolean\n" + ")";
        statement.execute(createTransactionTableSQL);
        System.out.println("事务表创建成功");

        //新增临时字段 name_cipher,address_cipher,字段类型text block_index int 类型，并且添加索引
        String addColumnsSql = "ALTER TABLE student ADD COLUMN name_cipher TEXT, ADD COLUMN address_cipher TEXT,ADD COLUMN block_index int,ADD INDEX idx_block_index (block_index)";
        statement.execute(addColumnsSql);

    }

    private void environment() throws ClassNotFoundException, SQLException {
        //连接数据库
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, username, password);
        //创建原始表
        String createTable = "create table student\n" + "(\n" + "    id      int primary key,\n" + "    name    varchar(50),\n" + "    age     int,\n" + "    address varchar(50),\n" + "    phone   varchar(50)\n" + ")";
        Statement statement = connection.createStatement();
        statement.execute(createTable);
        //添加1000条测试数据
        String insertSql = "insert into student(id,name,age,address,phone) values(?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(insertSql);
        for (int i = 0; i < 1000; i++) {
            ps.setInt(1, i);
            ps.setString(2, "name" + i);
            ps.setInt(3, i % 55);
            ps.setString(4, "address" + i);
            ps.setString(5, "phone+i");
        }
        ps.execute();

        System.out.println("环境准备成功");
    }


    // 模拟分块加密
    @Test
    public void blockEncrypt() throws Exception {
        //预先分块  每批1000条 5个线程
        int batchSize = 10;
        int thread = 5;

        //查询student数据条数
        String countSql = "select count(*) from student";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(countSql);
        resultSet.next();
        int count = resultSet.getInt(1);

        //计算分块数 最后剩余的作为一批
        int block = count % batchSize == 0 ? count / batchSize : count / batchSize + 1;

        //添加数据到 transaction_status 表 格式为 id + tableName + block_index + transaction_status   block_index 为分块的索引




        //分块加密
        ExecutorService executor = Executors.newFixedThreadPool(5);



    }
}