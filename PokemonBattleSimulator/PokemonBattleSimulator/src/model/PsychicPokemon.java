package model;

public class PsychicPokemon extends Pokemon {
    public PsychicPokemon(int id, String name, int hp, int attack, int speed, String[] skills, String imagePath) {
        super(id, name, hp, attack, speed, PokemonType.PSYCHIC, skills, imagePath);
    }
}