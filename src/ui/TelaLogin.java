package ui;

import servico.Sistema;
import excecao.LoginInvalidoException;
import modelo.Medico;
import modelo.Paciente;
import modelo.Usuario;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class TelaLogin extends JFrame {

    private final Sistema sistema;
    private final JTextField campoLogin = new JTextField(15);
    private final JPasswordField campoSenha = new JPasswordField(15);

    public TelaLogin(Sistema sistema) {
        super("Clínica Médica - Login");
        this.sistema = sistema;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Clínica Médica", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        painel.add(titulo, g);

        g.gridwidth = 1; g.gridy = 1;
        painel.add(new JLabel("Login:"), g);
        g.gridx = 1;
        painel.add(campoLogin, g);

        g.gridx = 0; g.gridy = 2;
        painel.add(new JLabel("Senha:"), g);
        g.gridx = 1;
        painel.add(campoSenha, g);

        JButton entrar = new JButton("Entrar");
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        painel.add(entrar, g);

        JButton criarConta = new JButton("Não tem conta? Cadastre-se");
        g.gridy = 4;
        painel.add(criarConta, g);

        JLabel dica = new JLabel("<html><small>Demonstração — médicos: ana, bruno, carla, daniel, elisa<br>"
                + "pacientes: maria, joao, lucas, fernanda, pedro (senha 123)</small></html>");
        g.gridy = 5;
        painel.add(dica, g);

        entrar.addActionListener(e -> entrar());
        criarConta.addActionListener(e ->
                DialogoCadastro.abrir(this, sistema.getCadastro()).ifPresent(this::abrirTelaDo));
        campoSenha.addActionListener(e -> entrar());
        getRootPane().setDefaultButton(entrar);

        setContentPane(painel);
        pack();
        setLocationRelativeTo(null);
    }

    private void entrar() {
        try {
            Usuario u = sistema.getAutenticacao().login(campoLogin.getText(), new String(campoSenha.getPassword()));
            abrirTelaDo(u);
        } catch (LoginInvalidoException ex) {
            Ui.erro(this, ex);
            campoSenha.setText("");
            campoSenha.requestFocus();
        }
    }

    private void abrirTelaDo(Usuario u) {
        JFrame proxima;
        if (u instanceof Medico m) {
            proxima = new TelaMedico(sistema, m);
        } else {
            proxima = new TelaPaciente(sistema, (Paciente) u);
        }
        proxima.setVisible(true);
        dispose();
    }
}
