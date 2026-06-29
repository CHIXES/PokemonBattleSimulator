package model;

import util.TypeEffectiveness;

public abstract class Pokemon {
    private int id;
    private String name;
    private int hp;
    private int maxHp;
    private int attack;
    private int speed;
    private PokemonType type;
    private String[] skills = new String[4];
    private int level = 5;
    private int exp = 0;
    private String imagePath;

    protected int[] skillPowers = {30, 50, 70, 90};

    // 状态字段
    private boolean leechSeeded;
    private boolean chargingSolar;
    private int leechSeedTurns;
    private boolean sleeping;
    private int sleepTurns;

    public Pokemon(int id, String name, int hp, int attack, int speed, PokemonType type,
                   String[] skills, String imagePath) {
        this.id = id;
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.attack = attack;
        this.speed = speed;
        this.type = type;
        if (skills != null && skills.length == 4) {
            this.skills = skills;
        }
        this.imagePath = imagePath;
        this.leechSeeded = false;
        this.chargingSolar = false;
        this.sleeping = false;
        this.leechSeedTurns = 0;
        this.sleepTurns = 0;
    }

    public int calculateDamage(Pokemon opponent, int skillIndex) {
        if (skillIndex < 0 || skillIndex >= skillPowers.length) return 0;
        int power = skillPowers[skillIndex];
        double base = (double) this.attack * power / 50.0;
        double multiplier = TypeEffectiveness.getMultiplier(this.type, opponent.getType());
        double random = 0.85 + Math.random() * 0.15;
        return (int) (base * multiplier * random);
    }

    public boolean isAlive() { return hp > 0; }
    public void takeDamage(int damage) { hp = Math.max(0, hp - damage); }
    public void healFull() { hp = maxHp; }
    public void gainExp(int expGain) {
        exp += expGain;
        while (exp >= 100) {
            levelUp();
        }
    }
    private void levelUp() {
        level++;
        exp = 0;
        maxHp += 5;
        attack += 2;
        hp = maxHp;
    }

    // ----- Getter/Setter -----
    public int getId() { return id; }
    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
    public PokemonType getType() { return type; }
    public String[] getSkills() { return skills; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // 状态
    public boolean isLeechSeeded() { return leechSeeded; }
    public void setLeechSeeded(boolean leechSeeded) { this.leechSeeded = leechSeeded; }
    public boolean isChargingSolar() { return chargingSolar; }
    public void setChargingSolar(boolean chargingSolar) { this.chargingSolar = chargingSolar; }
    public int getLeechSeedTurns() { return leechSeedTurns; }
    public void setLeechSeedTurns(int leechSeedTurns) { this.leechSeedTurns = leechSeedTurns; }
    public boolean isSleeping() { return sleeping; }
    public void setSleeping(boolean sleeping) { this.sleeping = sleeping; }
    public int getSleepTurns() { return sleepTurns; }
    public void setSleepTurns(int sleepTurns) { this.sleepTurns = sleepTurns; }
}