package gui;

import dao.PokemonBaseDAO;
import model.Pokemon;
import service.BattleService;
import exception.PokemonFaintedException;
import util.FileUtil;
import util.GameSaveUtil;
import util.TypeEffectiveness;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class BattleFrame extends JFrame {
    private BattleService battleService;
    private List<Pokemon> playerTeam;
    private List<Pokemon> enemyTeam;
    private Pokemon player;
    private Pokemon enemy;
    private int playerIndex = 0;

    private JLabel playerNameLabel;
    private JLabel enemyNameLabel;
    private JProgressBar playerHpBar;
    private JProgressBar enemyHpBar;
    private JLabel playerHpText;
    private JLabel enemyHpText;
    private JTextArea logArea;
    private JButton[] skillButtons = new JButton[4];
    private JButton saveLogButton;
    private JButton restartButton;
    private JButton saveTeamButton;
    private JButton loadTeamButton;
    private JButton switchButton;

    private JLabel playerImageLabel;
    private JLabel enemyImageLabel;

    public BattleFrame(List<Pokemon> playerTeam) {
        this.playerTeam = playerTeam;
        this.enemyTeam = generateEnemyTeam();
        System.out.println("敌人队伍数量：" + enemyTeam.size());

        this.player = playerTeam.get(0);
        this.enemy = enemyTeam.get(0);
        this.battleService = new BattleService(player, enemy);

        setTitle("⚔️ 宝可梦对战 - " + player.getName() + " VS " + enemy.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        refreshUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (battleService.getBattleLog().size() > 1) {
                    FileUtil.saveBattleLog(battleService.getBattleLog(), null);
                }
            }
        });
    }

    private List<Pokemon> generateEnemyTeam() {
        try {
            PokemonBaseDAO dao = new PokemonBaseDAO();
            List<Pokemon> all = dao.loadAllBasePokemon();
            List<Pokemon> team = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                team.add(all.get(i % all.size()));
            }
            return team;
        } catch (Exception e) {
            return new ArrayList<>(playerTeam);
        }
    }

    // ===== 清除精灵所有异常状态 =====
    private void clearPokemonStatus(Pokemon p) {
        p.setLeechSeeded(false);
        p.setChargingSolar(false);
        p.setSleeping(false);
        p.setLeechSeedTurns(0);
        p.setSleepTurns(0);
    }

    // ===== 敌人被克制时换人 =====
    private boolean enemyActiveSwitch() {
        double playerMultiplier = TypeEffectiveness.getMultiplier(player.getType(), enemy.getType());
        if (playerMultiplier != 2.0) return false;

        List<Pokemon> candidates = new ArrayList<>();
        for (Pokemon p : enemyTeam) {
            if (p == enemy || !p.isAlive()) continue;
            double mult = TypeEffectiveness.getMultiplier(player.getType(), p.getType());
            if (mult == 0.5) candidates.add(p);
        }
        if (candidates.isEmpty()) {
            for (Pokemon p : enemyTeam) {
                if (p == enemy || !p.isAlive()) continue;
                double mult = TypeEffectiveness.getMultiplier(player.getType(), p.getType());
                if (mult != 2.0) candidates.add(p);
            }
        }
        if (candidates.isEmpty()) return false;

        Pokemon chosen = candidates.get(0);
        clearPokemonStatus(chosen);   // 清除新敌人状态
        enemy = chosen;
        battleService.updatePokemon(player, enemy, "敌人主动换上了 " + enemy.getName() + "！");
        battleService.setEnemySwitched(true);
        return true;
    }

    private Pokemon selectOptimalEnemy(Pokemon currentPlayer, List<Pokemon> availableEnemyTeam) {
        Pokemon best = null;
        double bestScore = -999.0;
        for (Pokemon p : availableEnemyTeam) {
            if (!p.isAlive()) continue;
            double attackScore = TypeEffectiveness.getMultiplier(p.getType(), currentPlayer.getType());
            double defenseScore = TypeEffectiveness.getMultiplier(currentPlayer.getType(), p.getType());
            double score = attackScore - defenseScore + p.getHp() / 100.0;
            if (score > bestScore) {
                bestScore = score;
                best = p;
            }
        }
        if (best == null) {
            for (Pokemon p : availableEnemyTeam) if (p.isAlive()) return p;
        }
        return best;
    }

    // ===== 敌人死亡后换人 =====
    private boolean switchEnemy() {
        List<Pokemon> available = new ArrayList<>();
        for (Pokemon p : enemyTeam) {
            if (p != enemy && p.isAlive()) {
                available.add(p);
            }
        }
        if (available.isEmpty()) return false;
        Pokemon chosen = selectOptimalEnemy(player, available);
        if (chosen != null) {
            clearPokemonStatus(chosen);   // 清除新敌人状态
            enemy = chosen;
            battleService.updatePokemon(player, enemy, "敌人派出了 " + enemy.getName() + "！");
            return true;
        }
        return false;
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel statusPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder("对战状态"));

        playerImageLabel = new JLabel();
        playerImageLabel.setPreferredSize(new Dimension(80, 80));
        playerNameLabel = new JLabel("玩家", SwingConstants.CENTER);
        playerNameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        playerHpBar = new JProgressBar(0, 100);
        playerHpBar.setStringPainted(true);
        playerHpText = new JLabel("HP: 0/0", SwingConstants.CENTER);

        enemyImageLabel = new JLabel();
        enemyImageLabel.setPreferredSize(new Dimension(80, 80));
        enemyNameLabel = new JLabel("敌人", SwingConstants.CENTER);
        enemyNameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        enemyHpBar = new JProgressBar(0, 100);
        enemyHpBar.setStringPainted(true);
        enemyHpText = new JLabel("HP: 0/0", SwingConstants.CENTER);

        statusPanel.add(playerImageLabel);
        statusPanel.add(playerNameLabel);
        statusPanel.add(playerHpBar);
        statusPanel.add(playerHpText);
        statusPanel.add(enemyImageLabel);
        statusPanel.add(enemyNameLabel);
        statusPanel.add(enemyHpBar);
        statusPanel.add(enemyHpText);

        add(statusPanel, BorderLayout.NORTH);

        logArea = new JTextArea(12, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("宋体", Font.PLAIN, 13));
        logArea.setLineWrap(true);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("战斗日志"));
        add(logScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        JPanel skillPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        for (int i = 0; i < 4; i++) {
            final int index = i;
            skillButtons[i] = new JButton("技能" + (i + 1));
            skillButtons[i].setFont(new Font("微软雅黑", Font.PLAIN, 13));
            skillButtons[i].addActionListener(e -> {
                try {
                    enemyActiveSwitch();
                    boolean finished = battleService.playerAttack(index);
                    refreshUI();
                    if (finished) {
                        FileUtil.saveBattleLog(battleService.getBattleLog(), null);
                        if (player.isAlive()) {
                            boolean allEnemiesFainted = true;
                            for (Pokemon p : enemyTeam) if (p.isAlive()) { allEnemiesFainted = false; break; }
                            if (allEnemiesFainted) {
                                JOptionPane.showMessageDialog(this, "你击败了所有敌人！胜利！");
                                refreshUI();
                                return;
                            }
                            if (!enemy.isAlive()) {
                                if (!switchEnemy()) {
                                    JOptionPane.showMessageDialog(this, "你击败了所有敌人！胜利！");
                                    refreshUI();
                                    return;
                                } else {
                                    refreshUI();
                                }
                            }
                        } else {
                            if (!switchPlayer()) {
                                JOptionPane.showMessageDialog(this, "你的队伍全军覆没...战败！");
                                refreshUI();
                            } else {
                                refreshUI();
                            }
                        }
                    }
                } catch (PokemonFaintedException ex) {
                    JOptionPane.showMessageDialog(BattleFrame.this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            });
            skillPanel.add(skillButtons[i]);
        }
        bottomPanel.add(skillPanel, BorderLayout.CENTER);

        JPanel helperPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        switchButton = new JButton("🔄 换人");
        switchButton.addActionListener(e -> {
            List<Pokemon> backups = getAvailableBackups();
            if (backups.isEmpty()) {
                JOptionPane.showMessageDialog(BattleFrame.this,
                        "没有可用的后备宝可梦！", "换人失败",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String[] options = new String[backups.size()];
            for (int i = 0; i < backups.size(); i++) {
                Pokemon p = backups.get(i);
                options[i] = p.getName() + " (Lv." + p.getLevel() + " HP:" + p.getHp() + "/" + p.getMaxHp() + ")";
            }
            int choice = JOptionPane.showOptionDialog(
                    BattleFrame.this,
                    "选择要上场的宝可梦：",
                    "换人",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (choice >= 0 && choice < backups.size()) {
                Pokemon selected = backups.get(choice);
                clearPokemonStatus(selected);   // 清除新上场精灵状态
                playerIndex = playerTeam.indexOf(selected);
                player = selected;
                battleService.playerSwitch(selected);
                refreshUI();
            }
        });

        saveLogButton = new JButton("💾 保存日志");
        saveLogButton.addActionListener(e -> {
            String path = FileUtil.saveBattleLog(battleService.getBattleLog(), null);
            JOptionPane.showMessageDialog(this, "日志已保存至: " + path);
        });

        restartButton = new JButton("🔄 重新开始");
        restartButton.addActionListener(e -> {
            for (Pokemon p : playerTeam) p.healFull();
            for (Pokemon p : enemyTeam) p.healFull();
            playerIndex = 0;
            player = playerTeam.get(0);
            enemy = enemyTeam.get(0);
            battleService.resetBattle();
            logArea.setText("");
            refreshUI();
            JOptionPane.showMessageDialog(this, "已重置，开始新战斗！");
        });

        saveTeamButton = new JButton("💾 保存队伍");
        saveTeamButton.addActionListener(e -> {
            boolean success = GameSaveUtil.savePokemon(player);
            JOptionPane.showMessageDialog(this, success ? "队伍已保存！" : "保存失败，请查看控制台错误。");
        });

        loadTeamButton = new JButton("📂 读取队伍");
        loadTeamButton.addActionListener(e -> {
            Pokemon loaded = GameSaveUtil.loadPokemon();
            if (loaded != null) {
                player.setHp(loaded.getHp());
                player.setMaxHp(loaded.getMaxHp());
                player.setAttack(loaded.getAttack());
                player.setExp(loaded.getExp());
                player.setLevel(loaded.getLevel());
                for (Pokemon p : enemyTeam) p.healFull();
                enemy = enemyTeam.get(0);
                battleService.resetBattle();
                battleService.updatePokemon(player, enemy, "读取存档，战斗重置");
                logArea.setText("");
                refreshUI();
                JOptionPane.showMessageDialog(this, "读取队伍成功！当前HP: " + player.getHp());
            } else {
                JOptionPane.showMessageDialog(this, "读取失败，可能没有存档文件。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        helperPanel.add(switchButton);
        helperPanel.add(saveLogButton);
        helperPanel.add(restartButton);
        helperPanel.add(saveTeamButton);
        helperPanel.add(loadTeamButton);
        bottomPanel.add(helperPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ===== 玩家主动换人 =====
    private boolean switchPlayer() {
        for (int i = playerIndex + 1; i < playerTeam.size(); i++) {
            Pokemon next = playerTeam.get(i);
            if (next.isAlive()) {
                clearPokemonStatus(player);   // 清除旧玩家状态
                clearPokemonStatus(next);     // 清除新玩家状态
                playerIndex = i;
                player = next;
                battleService.updatePokemon(player, enemy, player.getName() + " 上场！");
                return true;
            }
        }
        return false;
    }

    private List<Pokemon> getAvailableBackups() {
        List<Pokemon> backups = new ArrayList<>();
        for (int i = 0; i < playerTeam.size(); i++) {
            if (i != playerIndex && playerTeam.get(i).isAlive()) {
                backups.add(playerTeam.get(i));
            }
        }
        return backups;
    }

    private void refreshUI() {
        // 更新玩家信息
        playerNameLabel.setText(player.getName() + "  Lv." + player.getLevel());
        playerHpBar.setMaximum(player.getMaxHp());
        playerHpBar.setValue(player.getHp());
        playerHpBar.setString(player.getHp() + "/" + player.getMaxHp());
        updateHpBarColor(playerHpBar, player.getHp(), player.getMaxHp());
        playerHpText.setText("HP: " + player.getHp() + "/" + player.getMaxHp());

        // 更新敌人信息
        enemyNameLabel.setText(enemy.getName() + "  Lv." + enemy.getLevel());
        enemyHpBar.setMaximum(enemy.getMaxHp());
        enemyHpBar.setValue(enemy.getHp());
        enemyHpBar.setString(enemy.getHp() + "/" + enemy.getMaxHp());
        updateHpBarColor(enemyHpBar, enemy.getHp(), enemy.getMaxHp());
        enemyHpText.setText("HP: " + enemy.getHp() + "/" + enemy.getMaxHp());

        // 判断战斗是否结束
        boolean allEnemiesFainted = true;
        for (Pokemon p : enemyTeam) if (p.isAlive()) { allEnemiesFainted = false; break; }
        boolean allPlayersFainted = true;
        for (Pokemon p : playerTeam) if (p.isAlive()) { allPlayersFainted = false; break; }
        boolean battleOver = allEnemiesFainted || allPlayersFainted;

        // 技能按钮
        boolean canAttack = !battleService.isPlayerSwitched() && player.isAlive() && enemy.isAlive() && !battleOver;
        String[] skills = player.getSkills();
        for (int i = 0; i < 4; i++) {
            if (i < skills.length && skills[i] != null) {
                skillButtons[i].setText(skills[i]);
                skillButtons[i].setEnabled(canAttack);
            } else {
                skillButtons[i].setText("-");
                skillButtons[i].setEnabled(false);
            }
        }

        // 换人按钮
        boolean canSwitch = false;
        for (Pokemon p : playerTeam) if (p != player && p.isAlive()) { canSwitch = true; break; }
        switchButton.setEnabled(canSwitch && !battleOver);

        // 日志
        logArea.setText("");
        for (String line : battleService.getBattleLog()) {
            logArea.append(line + "\n");
        }
        logArea.setCaretPosition(logArea.getDocument().getLength());

        loadImage(playerImageLabel, player.getImagePath());
        loadImage(enemyImageLabel, enemy.getImagePath());

        setTitle("⚔️ 宝可梦对战 - " + player.getName() + " VS " + enemy.getName());
    }

    private void loadImage(JLabel label, String path) {
        if (path == null || path.isEmpty()) {
            label.setText("无图");
            return;
        }

        ImageIcon icon = null;

        try {
            // 方式1：直接用传入的路径加载（images/妙蛙花.png）
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(path);

            // 方式2：尝试只取文件名
            if (is == null) {
                String fileName = path;
                if (path.contains("/")) {
                    fileName = path.substring(path.lastIndexOf("/") + 1);
                } else if (path.contains("\\")) {
                    fileName = path.substring(path.lastIndexOf("\\") + 1);
                }
                is = getClass().getClassLoader().getResourceAsStream("images/" + fileName);
            }

            // 方式3：尝试直接加载文件名（不包含 images/ 前缀）
            if (is == null) {
                String fileName = path;
                if (path.contains("/")) {
                    fileName = path.substring(path.lastIndexOf("/") + 1);
                } else if (path.contains("\\")) {
                    fileName = path.substring(path.lastIndexOf("\\") + 1);
                }
                is = getClass().getClassLoader().getResourceAsStream(fileName);
            }

            if (is != null) {
                java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(is);
                if (img != null) {
                    icon = new ImageIcon(img.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH));
                }
                is.close();
            } else {
                System.err.println("图片加载失败，尝试了所有方式: " + path);
            }
        } catch (Exception e) {
            System.err.println("图片加载异常: " + path);
            e.printStackTrace();
        }

        if (icon != null) {
            label.setIcon(icon);
            label.setText(null);
        } else {
            label.setText("图片加载失败");
        }
    }

    private void updateHpBarColor(JProgressBar bar, int current, int max) {
        double ratio = (double) current / max;
        if (ratio > 0.5) bar.setForeground(new Color(0, 153, 0));
        else if (ratio > 0.2) bar.setForeground(Color.ORANGE);
        else bar.setForeground(new Color(204, 0, 0));
    }
}