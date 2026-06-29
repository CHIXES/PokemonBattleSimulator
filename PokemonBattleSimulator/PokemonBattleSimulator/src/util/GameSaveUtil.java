package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Pokemon;
import model.FirePokemon;
import model.WaterPokemon;
import model.GrassPokemon;
import model.ElectricPokemon;
import model.PsychicPokemon;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameSaveUtil {

    private static final String SAVE_FILE = "pokemon_save.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean savePokemon(Pokemon pokemon) {
        PokemonData data = new PokemonData(
                pokemon.getId(),
                pokemon.getName(),
                pokemon.getHp(),
                pokemon.getMaxHp(),
                pokemon.getAttack(),
                pokemon.getSpeed(),
                pokemon.getType().name(),
                pokemon.getSkills(),
                pokemon.getLevel(),
                pokemon.getExp()
        );
        String json = gson.toJson(data);
        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            writer.write(json);
            System.out.println("✅ 存档成功：" + SAVE_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("❌ 保存失败：" + e.getMessage());
            return false;
        }
    }

    public static Pokemon loadPokemon() {
        try {
            String json = new String(Files.readAllBytes(Paths.get(SAVE_FILE)));
            PokemonData data = gson.fromJson(json, PokemonData.class);
            if (data == null) return null;

            Pokemon pokemon = createPokemonByType(
                    data.id, data.name, data.type,
                    data.maxHp, data.attack, data.speed,
                    data.skills
            );
            if (pokemon == null) return null;

            pokemon.setHp(data.hp);
            pokemon.setMaxHp(data.maxHp);
            pokemon.setAttack(data.attack);
            pokemon.setSpeed(data.speed);
            pokemon.setLevel(data.level);
            pokemon.setExp(data.exp);

            System.out.println("✅ 读档成功：" + SAVE_FILE);
            return pokemon;
        } catch (IOException e) {
            System.err.println("❌ 读档失败：" + e.getMessage());
            return null;
        }
    }

    private static Pokemon createPokemonByType(int id, String name, String type,
                                               int hp, int attack, int speed,
                                               String[] skills) {
        switch (type.toUpperCase()) {
            case "FIRE":     return new FirePokemon(id, name, hp, attack, speed, skills, null);
            case "WATER":    return new WaterPokemon(id, name, hp, attack, speed, skills, null);
            case "GRASS":    return new GrassPokemon(id, name, hp, attack, speed, skills, null);
            case "ELECTRIC": return new ElectricPokemon(id, name, hp, attack, speed, skills, null);
            case "PSYCHIC":  return new PsychicPokemon(id, name, hp, attack, speed, skills, null);
            default:         return null;
        }
    }

    private static class PokemonData {
        int id;
        String name;
        int hp;
        int maxHp;
        int attack;
        int speed;
        String type;
        String[] skills;
        int level;
        int exp;

        PokemonData(int id, String name, int hp, int maxHp, int attack, int speed,
                    String type, String[] skills, int level, int exp) {
            this.id = id;
            this.name = name;
            this.hp = hp;
            this.maxHp = maxHp;
            this.attack = attack;
            this.speed = speed;
            this.type = type;
            this.skills = skills;
            this.level = level;
            this.exp = exp;
        }
    }
}