package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // 数据库文件路径（相对于项目根目录）
    private static final String URL = "jdbc:sqlite:pokemon.db";
    private static Connection connection = null;

    // 私有构造方法，防止外部实例化
    private DatabaseConnection() {}

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    // 关闭连接
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
