package gui;

import model.Pokemon;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamSelectionFrame extends JFrame {
    private List<Pokemon> allPokemon;
    private List<Pokemon> selectedTeam = new ArrayList<>();
    private Set<Integer> selectedIds = new HashSet<>();
    private JPanel teamPanel;
    private JPanel pokemonPanel;
    private JLabel statusLabel;
    private JButton confirmButton;

    public TeamSelectionFrame(List<Pokemon> allPokemon) {
        this.allPokemon = allPokemon;
        setTitle("选择你的队伍 (最多6只)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("点击宝可梦添加到队伍，选满6只后点击确认", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        pokemonPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        pokemonPanel.setBorder(BorderFactory.createTitledBorder("所有宝可梦"));
        for (Pokemon p : allPokemon) {
            JButton btn = new JButton(p.getName() + " (" + p.getType() + ") HP:" + p.getMaxHp());
            btn.addActionListener(e -> addToTeam(p));
            pokemonPanel.add(btn);
        }
        JScrollPane leftScroll = new JScrollPane(pokemonPanel);
        centerPanel.add(leftScroll);

        teamPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        teamPanel.setBorder(BorderFactory.createTitledBorder("你的队伍 (0/6)"));
        JScrollPane rightScroll = new JScrollPane(teamPanel);
        centerPanel.add(rightScroll);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        statusLabel = new JLabel("请选择宝可梦 (点击左侧添加)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        bottomPanel.add(statusLabel, BorderLayout.CENTER);

        confirmButton = new JButton("确认队伍，开始战斗！");
        confirmButton.setEnabled(false);
        confirmButton.addActionListener((ActionEvent e) -> {
            if (selectedTeam.size() == 6) {
                dispose();
                startBattle();
            } else {
                JOptionPane.showMessageDialog(this, "请选满6只宝可梦！");
            }
        });
        bottomPanel.add(confirmButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void addToTeam(Pokemon p) {
        if (selectedIds.contains(p.getId())) {
            JOptionPane.showMessageDialog(this, p.getName() + " 已在队伍中！");
            return;
        }
        if (selectedTeam.size() >= 6) {
            JOptionPane.showMessageDialog(this, "队伍已满 (最多6只)！");
            return;
        }
        selectedTeam.add(p);
        selectedIds.add(p.getId());
        refreshTeamDisplay();
        if (selectedTeam.size() == 6) {
            confirmButton.setEnabled(true);
            statusLabel.setText("✅ 队伍已满！点击下方按钮开始战斗！");
        } else {
            statusLabel.setText("已选 " + selectedTeam.size() + "/6 只宝可梦");
        }
    }

    private void refreshTeamDisplay() {
        teamPanel.removeAll();
        teamPanel.setBorder(BorderFactory.createTitledBorder("你的队伍 (" + selectedTeam.size() + "/6)"));
        for (Pokemon p : selectedTeam) {
            JPanel entry = new JPanel(new BorderLayout());
            JLabel label = new JLabel(p.getName() + " (Lv." + p.getLevel() + ")");
            JButton removeBtn = new JButton("✕");
            removeBtn.addActionListener(e -> {
                selectedTeam.remove(p);
                selectedIds.remove(p.getId());
                confirmButton.setEnabled(false);
                refreshTeamDisplay();
                statusLabel.setText("已选 " + selectedTeam.size() + "/6 只宝可梦");
            });
            entry.add(label, BorderLayout.CENTER);
            entry.add(removeBtn, BorderLayout.EAST);
            teamPanel.add(entry);
        }
        teamPanel.revalidate();
        teamPanel.repaint();
    }

    // ===== 首发选择 =====
    private void startBattle() {
        // 所有宝可梦恢复满血
        for (Pokemon p : selectedTeam) {
            p.healFull();
        }

        // 弹出选择首发对话框
        String[] options = new String[selectedTeam.size()];
        for (int i = 0; i < selectedTeam.size(); i++) {
            options[i] = selectedTeam.get(i).getName() +
                    " (HP:" + selectedTeam.get(i).getMaxHp() +
                    " 攻击:" + selectedTeam.get(i).getAttack() + ")";
        }
        int choice = JOptionPane.showOptionDialog(
                this,
                "选择你的首发宝可梦：",
                "选择首发",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        // 如果用户取消，默认选择第一只
        if (choice < 0) {
            choice = 0;
        }

        // 将选中的首发放到队伍列表最前面
        Pokemon first = selectedTeam.remove(choice);
        selectedTeam.add(0, first);

        // 启动战斗
        SwingUtilities.invokeLater(() -> {
            BattleFrame battleFrame = new BattleFrame(selectedTeam);
            battleFrame.setVisible(true);
        });
    }
}