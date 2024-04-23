package com.sinocipher;

import java.sql.*;

// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    // JDBC连接信息
    static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    static final String DB_URL = "jdbc:oracle:thin:@192.168.7.158:1521:helowin"; // 这里的ORCL为数据库服务名

    // 数据库凭据
    static final String USER = "test";
    static final String PASS = "123456";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册JDBC驱动
            Class.forName(JDBC_DRIVER);

            // 打开连接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 执行查询
            System.out.println("实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, name, age FROM t_student";
            ResultSet rs = stmt.executeQuery(sql);

            // 处理结果集
            while (rs.next()) {
                // 通过字段检索
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");

                // 输出数据
                System.out.print("ID: " + id);
                System.out.print(", 名字: " + name);
                System.out.println(", 年龄: " + age);
            }

            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tStudent = metaData.getTables(null, null, "T_STUDENT", null);
            if (tStudent.next()) {
                System.out.println("表存在");

            } else {
                System.out.println("表不存在");
            }

            // 关闭资源
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理JDBC错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理Class.forName错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}