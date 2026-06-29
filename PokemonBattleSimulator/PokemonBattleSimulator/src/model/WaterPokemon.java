package model;

public class WaterPokemon extends Pokemon {
    public WaterPokemon(int id, String name, int hp, int attack, int speed, String[] skills, String imagePath) {
        super(id, name, hp, attack, speed, PokemonType.WATER, skills, imagePath);
    }
}