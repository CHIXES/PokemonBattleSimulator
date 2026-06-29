import dao.PokemonBaseDAO;
import model.Pokemon;
import gui.TeamSelectionFrame;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            PokemonBaseDAO dao = new PokemonBaseDAO();
            List<Pokemon> allPokemon = dao.loadAllBasePokemon();

            if (allPokemon.size() < 6) {
                JOptionPane.showMessageDialog(null, "数据库宝可梦不足6只，无法组队！");
                return;
            }

            SwingUtilities.invokeLater(() -> new TeamSelectionFrame(allPokemon));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库连接失败：" + e.getMessage());
        }
    }
}