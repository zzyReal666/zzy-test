package com.zzy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

/**
 * 事务验证  验证存量加密使用业务库添加一张表，本地同步该表的方式
 */
public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.7.113:13306/db_enc_test";
        String username = "root";
        String password = "123456";
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);

            if (connection != null) {
                System.out.println("成功连接到数据库！");
                connection.setAutoCommit(false); // 开启事务

                String insertQuery = "INSERT INTO error_test (id, col1, col2) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                for (int i = 1; i <= 1000; i++) {
                    preparedStatement.setInt(1, i);
                    preparedStatement.setString(2, "col1_value_" + i);
                    if (598 == i) {
                        preparedStatement.setString(3, "adfasfsadfsadfasfasdfasdfasddfsagagdgergerghdthrthdfhdfhtdfhttfdfhtfgtgfsdgdsg" + i);
                    } else {
                        preparedStatement.setString(3, "col2_value_" + i);

                    }
                    preparedStatement.addBatch(); // 添加到批处理中
                }

                int[] updateCounts = preparedStatement.executeBatch(); // 执行批处理
                // 添加数据到 transaction_status 表
                String insertStatusQuery = "INSERT INTO transaction_status (tableName, block_index, transaction_status) VALUES (?, ?, ?)";
                PreparedStatement statusStatement = connection.prepareStatement(insertStatusQuery);
                statusStatement.setString(1, "error_test");
                statusStatement.setInt(2, 1); // 第一条数据的 id
                statusStatement.setBoolean(3, true); // 事务状态
                statusStatement.executeUpdate();
                connection.commit(); // 提交事务
                System.out.println("成功插入 " + updateCounts.length + " 条数据！");
            } else {
                System.out.println("无法连接到数据库！");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("找不到MySQL JDBC驱动程序！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("连接数据库时出现错误！");
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // 出错时回滚事务
                } catch (SQLException ex) {
                    System.out.println("回滚事务时出现错误！");
                    ex.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}