package model;

public class GrassPokemon extends Pokemon {
    public GrassPokemon(int id, String name, int hp, int attack, int speed, String[] skills, String imagePath) {
        super(id, name, hp, attack, speed, PokemonType.GRASS, skills, imagePath);
    }
}