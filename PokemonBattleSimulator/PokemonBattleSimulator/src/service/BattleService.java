package service;

import model.Pokemon;
import model.PokemonType;
import exception.PokemonFaintedException;
import util.TypeEffectiveness;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleService {
    private Pokemon playerPokemon;
    private Pokemon enemyPokemon;
    private List<String> battleLog;
    private boolean enemySwitchedThisTurn = false;
    private boolean playerSwitchedThisTurn = false;
    private Random random = new Random();

    private static final java.util.Map<String, Integer> ACCURACY_MAP = new java.util.HashMap<>();
    static {
        ACCURACY_MAP.put("飞叶快刀", 95);
        ACCURACY_MAP.put("寄生种子", 90);
        ACCURACY_MAP.put("催眠粉", 75);
        ACCURACY_MAP.put("日光束", 100);
        ACCURACY_MAP.put("火焰旋涡", 85);
        ACCURACY_MAP.put("龙之怒", 100);
        ACCURACY_MAP.put("喷射火焰", 100);
        ACCURACY_MAP.put("大字爆炎", 85);
        ACCURACY_MAP.put("水枪", 100);
        ACCURACY_MAP.put("咬住", 100);
        ACCURACY_MAP.put("水流尾", 90);
        ACCURACY_MAP.put("水炮", 80);
        ACCURACY_MAP.put("电击", 100);
        ACCURACY_MAP.put("电磁波", 90);
        ACCURACY_MAP.put("电球", 100);
        ACCURACY_MAP.put("十万伏特", 100);
        ACCURACY_MAP.put("高速移动", 100);
        ACCURACY_MAP.put("打雷", 70);
        ACCURACY_MAP.put("念力", 100);
        ACCURACY_MAP.put("精神强念", 100);
        ACCURACY_MAP.put("自我再生", 100);
        ACCURACY_MAP.put("精神击破", 90);
    }

    public BattleService(Pokemon player, Pokemon enemy) {
        this.playerPokemon = player;
        this.enemyPokemon = enemy;
        this.battleLog = new ArrayList<>();
        battleLog.add("⚔️ 战斗开始！");
        battleLog.add(player.getName() + " VS " + enemy.getName());
        battleLog.add("------------------------");
    }

    public void updatePokemon(Pokemon newPlayer, Pokemon newEnemy, String switchMessage) {
        if (newPlayer != null) this.playerPokemon = newPlayer;
        if (newEnemy != null) this.enemyPokemon = newEnemy;
        if (switchMessage != null && !switchMessage.isEmpty()) {
            battleLog.add(switchMessage);
        }
    }

    public void setEnemySwitched(boolean switched) { this.enemySwitchedThisTurn = switched; }
    public void setPlayerSwitched(boolean switched) { this.playerSwitchedThisTurn = switched; }
    public boolean isPlayerSwitched() { return playerSwitchedThisTurn; }

    private boolean isHit(String skillName) {
        Integer acc = ACCURACY_MAP.get(skillName);
        if (acc == null) acc = 100;
        return random.nextInt(100) < acc;
    }

    // ---------- 玩家攻击入口 ----------
    public boolean playerAttack(int skillIndex) throws PokemonFaintedException {
        if (!playerPokemon.isAlive()) {
            throw new PokemonFaintedException("你的宝可梦已经倒下了！");
        }
        if (playerSwitchedThisTurn) {
            throw new PokemonFaintedException("本回合已换人，不能再攻击！");
        }

        String skillName = playerPokemon.getSkills()[skillIndex];

        // 日光束蓄力
        if (skillName.equals("日光束") && !playerPokemon.isChargingSolar()) {
            playerPokemon.setChargingSolar(true);
            battleLog.add(playerPokemon.getName() + " 开始蓄力日光束！");
            enemyTurn();
            return false;
        }
        if (skillName.equals("日光束") && playerPokemon.isChargingSolar()) {
            playerPokemon.setChargingSolar(false);
            return performPlayerAction(skillIndex);
        }

        // 自我再生
        if (skillName.equals("自我再生")) {
            int healAmount = playerPokemon.getMaxHp() / 2;
            int oldHp = playerPokemon.getHp();
            playerPokemon.setHp(Math.min(playerPokemon.getMaxHp(), oldHp + healAmount));
            int actualHeal = playerPokemon.getHp() - oldHp;
            battleLog.add(playerPokemon.getName() + " 使用了自我再生，恢复了 " + actualHeal + " 点HP！");
            enemyTurn();
            return false;
        }

        return performPlayerAction(skillIndex);
    }

    // 玩家行动
    private boolean performPlayerAction(int skillIndex) throws PokemonFaintedException {
        // ---- 玩家睡眠检查 ----
        if (playerPokemon.isSleeping()) {
            battleLog.add(playerPokemon.getName() + " 正在睡觉，无法行动！");
            int remain = playerPokemon.getSleepTurns() - 1;
            playerPokemon.setSleepTurns(remain);
            if (remain <= 0) {
                playerPokemon.setSleeping(false);
                battleLog.add(playerPokemon.getName() + " 醒了！");
            }
            enemyTurn();
            return false;
        }

        boolean playerFirst = playerPokemon.getSpeed() >= enemyPokemon.getSpeed();

        if (playerFirst) {
            boolean ends = executePlayerAttack(skillIndex);
            if (ends) return true;
            enemyTurn();
            return false;
        } else {
            enemyTurn();
            if (!playerPokemon.isAlive()) return false;
            return executePlayerAttack(skillIndex);
        }
    }

    // 执行玩家攻击
    private boolean executePlayerAttack(int skillIndex) {
        String skillName = playerPokemon.getSkills()[skillIndex];

        // ---- 催眠粉 ----
        if (skillName.equals("催眠粉")) {
            if (!isHit(skillName)) {
                battleLog.add(playerPokemon.getName() + " 的催眠粉未命中！");
                return false;
            }
            // 草系免疫粉末类技能
            if (enemyPokemon.getType() == PokemonType.GRASS) {
                battleLog.add("对草系宝可梦 " + enemyPokemon.getName() + " 无效！");
                return false;
            }
            if (enemyPokemon.isSleeping()) {
                battleLog.add(enemyPokemon.getName() + " 已经睡着了！");
            } else {
                int turns = 2 + random.nextInt(4);
                enemyPokemon.setSleeping(true);
                enemyPokemon.setSleepTurns(turns);
                battleLog.add(enemyPokemon.getName() + " 睡着了！将持续 " + turns + " 回合");
            }
            return false;
        }

        // ---- 其他攻击技能（含寄生种子） ----
        if (!isHit(skillName)) {
            battleLog.add(playerPokemon.getName() + " 的 " + skillName + " 未命中！");
            return false;
        }

        int damage = playerPokemon.calculateDamage(enemyPokemon, skillIndex);
        enemyPokemon.takeDamage(damage);

        double mult = TypeEffectiveness.getMultiplier(playerPokemon.getType(), enemyPokemon.getType());
        battleLog.add(playerPokemon.getName() + " 使用 " + skillName + " 造成 " + damage + " 点伤害！ " +
                TypeEffectiveness.getDescription(mult));

        // 寄生种子
        if (skillName.equals("寄生种子")) {
            if (enemyPokemon.getType() == PokemonType.GRASS) {
                battleLog.add("对草系宝可梦 " + enemyPokemon.getName() + " 无效！");
            } else if (enemyPokemon.isLeechSeeded()) {
                battleLog.add("但 " + enemyPokemon.getName() + " 已经被寄生种子寄生了！");
            } else {
                enemyPokemon.setLeechSeeded(true);
                enemyPokemon.setLeechSeedTurns(5);
                battleLog.add(enemyPokemon.getName() + " 被寄生种子寄生了！");
            }
        }

        if (!enemyPokemon.isAlive()) {
            battleLog.add("🎉 " + enemyPokemon.getName() + " 倒下了！");
            playerPokemon.gainExp(20);
            battleLog.add(playerPokemon.getName() + " 获得 20 经验值");
            return true;
        }
        return false;
    }

    // ---------- 敌人回合 ----------
    public void enemyTurn() {
        // ---- 玩家寄生种子 ----
        if (playerPokemon.isLeechSeeded()) {
            int drain = playerPokemon.getMaxHp() / 8;
            if (drain < 1) drain = 1;
            playerPokemon.setHp(Math.max(0, playerPokemon.getHp() - drain));
            enemyPokemon.setHp(Math.min(enemyPokemon.getMaxHp(), enemyPokemon.getHp() + drain));
            battleLog.add("寄生种子吸取了 " + playerPokemon.getName() + " 的 " + drain + " 点HP！");
            playerPokemon.setLeechSeedTurns(playerPokemon.getLeechSeedTurns() - 1);
            if (playerPokemon.getLeechSeedTurns() <= 0) {
                playerPokemon.setLeechSeeded(false);
                battleLog.add("寄生种子效果消失了！");
            }
            if (!playerPokemon.isAlive()) {
                battleLog.add("💀 " + playerPokemon.getName() + " 倒下了！");
                return;
            }
        }

        // ---- 敌人睡眠检查 ----
        if (enemyPokemon.isSleeping()) {
            battleLog.add(enemyPokemon.getName() + " 正在睡觉，无法行动！");
            int remain = enemyPokemon.getSleepTurns() - 1;
            enemyPokemon.setSleepTurns(remain);
            if (remain <= 0) {
                enemyPokemon.setSleeping(false);
                battleLog.add(enemyPokemon.getName() + " 醒了！");
            }
            return;
        }

        // ---- 敌人行动 ----
        String skill = enemyPokemon.getSkills()[0];

        // 日光束
        if (skill.equals("日光束") && !enemyPokemon.isChargingSolar()) {
            enemyPokemon.setChargingSolar(true);
            battleLog.add(enemyPokemon.getName() + " 开始蓄力日光束！");
            return;
        }
        if (skill.equals("日光束") && enemyPokemon.isChargingSolar()) {
            enemyPokemon.setChargingSolar(false);
            if (!isHit(skill)) {
                battleLog.add(enemyPokemon.getName() + " 的日光束未命中！");
                return;
            }
            int damage = enemyPokemon.calculateDamage(playerPokemon, 0);
            playerPokemon.takeDamage(damage);
            double mult = TypeEffectiveness.getMultiplier(enemyPokemon.getType(), playerPokemon.getType());
            battleLog.add(enemyPokemon.getName() + " 使用 日光束 造成 " + damage + " 点伤害！ " +
                    TypeEffectiveness.getDescription(mult));
            if (!playerPokemon.isAlive()) {
                battleLog.add("💀 " + playerPokemon.getName() + " 倒下了！");
            }
            return;
        }

        // 普通攻击
        if (!isHit(skill)) {
            battleLog.add(enemyPokemon.getName() + " 的 " + skill + " 未命中！");
            return;
        }
        int damage = enemyPokemon.calculateDamage(playerPokemon, 0);
        playerPokemon.takeDamage(damage);
        double mult = TypeEffectiveness.getMultiplier(enemyPokemon.getType(), playerPokemon.getType());
        battleLog.add(enemyPokemon.getName() + " 使用 " + skill + " 造成 " + damage + " 点伤害！ " +
                TypeEffectiveness.getDescription(mult));
        if (!playerPokemon.isAlive()) {
            battleLog.add("💀 " + playerPokemon.getName() + " 倒下了！");
        }
    }

    // ---------- 玩家换人 ----------
    public void playerSwitch(Pokemon newPlayer) {
        if (newPlayer == null || !newPlayer.isAlive()) return;
        String oldName = playerPokemon.getName();
        this.playerPokemon = newPlayer;
        battleLog.add(oldName + " 换上了 " + newPlayer.getName() + "！");
        setPlayerSwitched(true);
        enemyTurn();
        setPlayerSwitched(false);
    }

    // ---------- 重置 ----------
    public void resetBattle() {
        clearStatus(playerPokemon);
        clearStatus(enemyPokemon);
        playerPokemon.healFull();
        enemyPokemon.healFull();
        battleLog.clear();
        battleLog.add("⚔️ 战斗重置！");
        battleLog.add(playerPokemon.getName() + " VS " + enemyPokemon.getName());
        battleLog.add("------------------------");
        enemySwitchedThisTurn = false;
        playerSwitchedThisTurn = false;
    }

    private void clearStatus(Pokemon p) {
        p.setLeechSeeded(false);
        p.setChargingSolar(false);
        p.setSleeping(false);
        p.setLeechSeedTurns(0);
        p.setSleepTurns(0);
    }

    public void addLog(String message) {
        if (message != null && !message.isEmpty()) {
            battleLog.add(message);
        }
    }

    public boolean isBattleOver() {
        return !playerPokemon.isAlive() || !enemyPokemon.isAlive();
    }

    public List<String> getBattleLog() {
        return battleLog;
    }

    public Pokemon getPlayerPokemon() {
        return playerPokemon;
    }

    public Pokemon getEnemyPokemon() {
        return enemyPokemon;
    }
}