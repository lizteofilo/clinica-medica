package ui;

import excecao.AcessoNegadoException;
import modelo.Paciente;
import modelo.Usuario;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridLayout;

final class DialogoMeusDados {

    private DialogoMeusDados() {
    }

    static boolean abrir(Component pai, Usuario logado) {
        JTextField nome = new JTextField(logado.getNome(), 20);
        JPasswordField senha = new JPasswordField(20);
        JTextField plano = null;

        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Nome:"));
        form.add(nome);
        form.add(new JLabel("Nova senha (deixe em branco para manter):"));
        form.add(senha);
        if (logado instanceof Paciente p) {
            plano = new JTextField(p.getPlanoDescricao(), 20);
            form.add(new JLabel("Plano de saúde (ou \"" + Paciente.SEM_PLANO + "\"):"));
            form.add(plano);
        }

        while (true) {  
            int r = JOptionPane.showConfirmDialog(pai, form, "Meus dados", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) {
                return false;
            }
            try {
                logado.alterarNome(logado, nome.getText());
                String novaSenha = new String(senha.getPassword());
                if (!novaSenha.isBlank()) {
                    logado.alterarSenha(logado, novaSenha);
                }
                if (logado instanceof Paciente p && plano != null) {
                    p.alterarPlano(logado, plano.getText());
                }
                Ui.info(pai, "Dados atualizados.");
                return true;
            } catch (AcessoNegadoException ex) {
                Ui.erro(pai, ex);
                return false;
            } catch (IllegalArgumentException ex) {    
                JOptionPane.showMessageDialog(pai, ex.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
