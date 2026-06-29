package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Pokemon;
import model.PokemonType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveManager {
    private static final String SAVE_FILE = "save_data.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 存档数据类
    public static class SaveData {
        public int pokemonId;
        public String name;
        public String type;
        public int hp;
        public int maxHp;
        public int attack;
        public int level;
        public int exp;
        public String[] skills;
    }

    // 保存当前宝可梦状态
    public static boolean savePokemon(Pokemon p) {
        try {
            SaveData data = new SaveData();
            data.pokemonId = p.getId();
            data.name = p.getName();
            data.type = p.getType().name();
            data.hp = p.getHp();
            data.maxHp = p.getMaxHp();
            data.attack = p.getAttack();
            data.level = p.getLevel();
            data.exp = p.getExp();
            data.skills = p.getSkills();

            String json = gson.toJson(data);
            Files.write(Paths.get(SAVE_FILE), json.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 读取存档，返回 SaveData 对象
    public static SaveData loadSaveData() {
        try {
            String json = new String(Files.readAllBytes(Paths.get(SAVE_FILE)));
            return gson.fromJson(json, SaveData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 恢复宝可梦状态
    public static boolean restorePokemon(Pokemon p, SaveData data) {
        if (data == null) return false;
        // 检查 ID 是否匹配，防止加载错误的数据
        if (p.getId() != data.pokemonId) {
            System.err.println("宝可梦ID不匹配！");
            return false;
        }
        p.setHp(data.hp);
        p.setMaxHp(data.maxHp);
        p.setAttack(data.attack);
        p.setLevel(data.level);
        p.setExp(data.exp);
        // 技能假设不变
        return true;
    }
}
