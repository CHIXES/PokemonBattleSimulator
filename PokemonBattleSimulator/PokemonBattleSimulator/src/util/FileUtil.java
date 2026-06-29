package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileUtil {

    /**
     * 保存战斗日志到文件
     * @param logs 日志列表
     * @param fileName 文件名（可包含路径），如果为null则自动生成
     * @return 保存的文件路径
     */
    public static String saveBattleLog(List<String> logs, String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileName = "battle_log_" + timestamp + ".txt";
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("===== 宝可梦对战记录 =====");
            writer.println("时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println();
            for (String line : logs) {
                writer.println(line);
            }
            writer.println("===== 记录结束 =====");
            System.out.println("✅ 战斗日志已保存至: " + fileName);
        } catch (IOException e) {
            System.err.println("❌ 保存日志失败: " + e.getMessage());
            e.printStackTrace();
        }
        return fileName;
    }
}
