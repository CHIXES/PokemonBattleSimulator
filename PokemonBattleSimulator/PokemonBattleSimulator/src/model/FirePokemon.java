package model;

public class FirePokemon extends Pokemon {
    public FirePokemon(int id, String name, int hp, int attack, int speed, String[] skills, String imagePath) {
        super(id, name, hp, attack, speed, PokemonType.FIRE, skills, imagePath);
    }
}