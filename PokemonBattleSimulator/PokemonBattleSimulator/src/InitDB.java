import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InitDB {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:pokemon.db";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS pokemon_base");

            stmt.execute("CREATE TABLE pokemon_base (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "type TEXT CHECK(type IN ('Fire','Water','Grass','Electric','Psychic'))," +
                    "base_hp INTEGER," +
                    "base_attack INTEGER," +
                    "speed INTEGER," +
                    "skill1 TEXT," +
                    "skill2 TEXT," +
                    "skill3 TEXT," +
                    "skill4 TEXT," +
                    "image_path TEXT" +
                    ")");

            stmt.executeUpdate("INSERT INTO pokemon_base VALUES " +
                    "(1,'妙蛙花','Grass',105,70,60,'飞叶快刀','寄生种子','催眠粉','日光束','images/妙蛙花.png')");
            stmt.executeUpdate("INSERT INTO pokemon_base VALUES " +
                    "(2,'喷火龙','Fire',100,75,85,'火焰旋涡','龙之怒','喷射火焰','大字爆炎','images/喷火龙.png')");
            stmt.executeUpdate("INSERT INTO pokemon_base VALUES " +
                    "(3,'水箭龟','Water',110,70,65,'水枪','咬住','水流尾','水炮','images/水箭龟.png')");
            stmt.executeUpdate("INSERT INTO pokemon_base VALUES " +
                    "(4,'皮卡丘','Electric',80,55,90,'电击','电磁波','电球','十万伏特','images/皮卡丘.png')");
            stmt.executeUpdate("INSERT INTO pokemon_base VALUES " +
                    "(5,'雷丘','Electric',95,80,100,'电击','高速移动','电球','打雷','images/雷丘.png')");
            stmt.executeUpdate("INSERT INTO pokemon_base VALUES " +
                    "(6,'超梦','Psychic',120,90,130,'念力','精神强念','自我再生','精神击破','images/超梦.png')");

            System.out.println("✅ 数据库初始化成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}