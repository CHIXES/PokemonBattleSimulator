package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PokemonBaseDAO {

    public List<Pokemon> loadAllBasePokemon() throws SQLException {
        List<Pokemon> pokemonList = new ArrayList<>();
        String sql = "SELECT * FROM pokemon_base";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                int baseHp = rs.getInt("base_hp");
                int baseAttack = rs.getInt("base_attack");
                int speed = rs.getInt("speed");   // 新增
                String skill1 = rs.getString("skill1");
                String skill2 = rs.getString("skill2");
                String skill3 = rs.getString("skill3");
                String skill4 = rs.getString("skill4");
                String imagePath = rs.getString("image_path");
                String[] skills = {skill1, skill2, skill3, skill4};

                Pokemon pokemon = null;
                switch (type) {
                    case "Fire":
                        pokemon = new FirePokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                        break;
                    case "Water":
                        pokemon = new WaterPokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                        break;
                    case "Grass":
                        pokemon = new GrassPokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                        break;
                    case "Electric":
                        pokemon = new ElectricPokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                        break;
                    case "Psychic":
                        pokemon = new PsychicPokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                        break;
                    default:
                        throw new IllegalArgumentException("未知类型: " + type);
                }
                pokemonList.add(pokemon);
            }
        } catch (SQLException e) {
            throw new SQLException("加载宝可梦数据失败: " + e.getMessage(), e);
        }
        return pokemonList;
    }

    public Pokemon loadPokemonById(int id) throws SQLException {
        String sql = "SELECT * FROM pokemon_base WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                int baseHp = rs.getInt("base_hp");
                int baseAttack = rs.getInt("base_attack");
                int speed = rs.getInt("speed");
                String skill1 = rs.getString("skill1");
                String skill2 = rs.getString("skill2");
                String skill3 = rs.getString("skill3");
                String skill4 = rs.getString("skill4");
                String imagePath = rs.getString("image_path");
                String[] skills = {skill1, skill2, skill3, skill4};

                switch (type) {
                    case "Fire":
                        return new FirePokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                    case "Water":
                        return new WaterPokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                    case "Grass":
                        return new GrassPokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                    case "Electric":
                        return new ElectricPokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                    case "Psychic":
                        return new PsychicPokemon(id, name, baseHp, baseAttack, speed, skills, imagePath);
                    default:
                        return null;
                }
            }
            return null;
        }
    }
}