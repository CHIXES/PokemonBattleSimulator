package model;

public class ElectricPokemon extends Pokemon {
    public ElectricPokemon(int id, String name, int hp, int attack, int speed, String[] skills, String imagePath) {
        super(id, name, hp, attack, speed, PokemonType.ELECTRIC, skills, imagePath);
    }
}