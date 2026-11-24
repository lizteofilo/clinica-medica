import excecao.ClinicaException;
import repositorio.DadosClinica;
import repositorio.DadosIniciais;
import servico.Sistema;
import ui.TelaLogin;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Usando o visual padrão do Swing: " + e.getMessage());
        }

        DadosClinica dados = new DadosClinica();
        try {
            DadosIniciais.popular(dados);
        } catch (ClinicaException e) {
            JOptionPane.showMessageDialog(null, "Erro nos dados de exemplo: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Sistema sistema = new Sistema(dados);
        SwingUtilities.invokeLater(() -> new TelaLogin(sistema).setVisible(true));
    }
}
