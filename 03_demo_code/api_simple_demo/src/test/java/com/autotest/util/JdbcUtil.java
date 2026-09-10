package com.autotest.util;

import com.autotest.config.ConfigYamlUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class JdbcUtil {

    // 从yml读取数据库配置
    private static final String DB_URL = ConfigYamlUtil.getValue("db.url");
    private static final String DB_USER = ConfigYamlUtil.getValue("db.username");
    private static final String DB_PASSWORD = ConfigYamlUtil.getValue("db.password");

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * 执行单条SELECT SQL，返回第一条记录，封装成Map
     * 仅支持查询，不能执行增删改
     * @param sql select语句
     * @return 单行数据map key=字段名，value=字段值
     */
    public static Map<String, Object> queryOne(String sql) {
        Map<String, Object> resultMap = new HashMap<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    resultMap.put(columnName, rs.getObject(i));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("数据库查询异常，sql：" + sql, e);
        }
        return resultMap;
    }

    /**
     * 预编译查询，防止SQL注入
     * @param sql select语句，占位符用 ?
     * @param params 占位参数
     * @return 单行结果Map
     */
    public static Map<String, Object> queryOne(String sql, Object... params) {
        Map<String, Object> resultMap = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置占位参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rs.getMetaData().getColumnName(i);
                        resultMap.put(columnName, rs.getObject(i));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("数据库查询异常，sql：" + sql, e);
        }
        return resultMap;
    }
}
