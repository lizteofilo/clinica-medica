package ui;

import excecao.ClinicaException;
import modelo.Paciente;
import modelo.Usuario;
import modelo.especialidade.CatalogoEspecialidades;
import modelo.especialidade.Especialidade;
import servico.CadastroService;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.Optional;

final class DialogoCadastro {

    private static final String PACIENTE = "Paciente";
    private static final String MEDICO = "Médico";
    private static final String[] PLANOS_SUGERIDOS = {Paciente.SEM_PLANO, "Unimed", "Hapvida", "Bradesco Saúde"};

    private DialogoCadastro() {
    }

    static Optional<Usuario> abrir(Component pai, CadastroService cadastro) {
        // ---------- campos comuns ----------
        JComboBox<String> tipo = new JComboBox<>(new String[]{PACIENTE, MEDICO});
        JTextField nome = new JTextField(22);
        JTextField login = new JTextField(22);
        JPasswordField senha = new JPasswordField(22);
        JPasswordField confirmacao = new JPasswordField(22);

        JPanel comuns = new JPanel(new GridLayout(0, 2, 6, 6));
        comuns.add(new JLabel("Eu sou:"));
        comuns.add(tipo);
        comuns.add(new JLabel("Nome completo:"));
        comuns.add(nome);
        comuns.add(new JLabel("Login:"));
        comuns.add(login);
        comuns.add(new JLabel("Senha:"));
        comuns.add(senha);
        comuns.add(new JLabel("Confirmar senha:"));
        comuns.add(confirmacao);

        // ---------- campos de paciente ----------
        JTextField idade = new JTextField(5);
        JComboBox<String> plano = new JComboBox<>(PLANOS_SUGERIDOS);
        plano.setEditable(true);                    
        JPanel camposPaciente = new JPanel(new GridLayout(0, 2, 6, 6));
        camposPaciente.add(new JLabel("Idade:"));
        camposPaciente.add(idade);
        camposPaciente.add(new JLabel("Plano de saúde:"));
        camposPaciente.add(plano);

        // ---------- campos de médico ----------
        JComboBox<Especialidade> especialidade =
                new JComboBox<>(CatalogoEspecialidades.todas().toArray(new Especialidade[0]));
        JTextField valor = new JTextField(8);
        JTextField planos = new JTextField(22);
        JLabel regras = new JLabel();
        Runnable mostrarRegras = () -> regras.setText("<html><small>"
                + ((Especialidade) especialidade.getSelectedItem()).descreverRegras() + "</small></html>");
        mostrarRegras.run();
        especialidade.addActionListener(e -> mostrarRegras.run());
        JPanel camposMedico = new JPanel(new GridLayout(0, 2, 6, 6));
        camposMedico.add(new JLabel("Especialidade:"));
        camposMedico.add(especialidade);
        camposMedico.add(new JLabel(""));
        camposMedico.add(regras);
        camposMedico.add(new JLabel("Valor-base da consulta (R$):"));
        camposMedico.add(valor);
        camposMedico.add(new JLabel("Planos atendidos (separe por vírgula):"));
        camposMedico.add(planos);

        // ---------- troca de campos conforme o tipo ----------
        JPanel especificos = new JPanel(new BorderLayout());
        especificos.add(camposPaciente, BorderLayout.NORTH);
        especificos.add(camposMedico, BorderLayout.SOUTH);
        camposMedico.setVisible(false);

        JPanel form = new JPanel(new BorderLayout(0, 12));
        form.add(comuns, BorderLayout.NORTH);
        form.add(especificos, BorderLayout.CENTER);

        tipo.addActionListener(e -> {
            boolean ehPaciente = PACIENTE.equals(tipo.getSelectedItem());
            camposPaciente.setVisible(ehPaciente);
            camposMedico.setVisible(!ehPaciente);
            Window janela = SwingUtilities.getWindowAncestor(form);
            if (janela != null) {
                janela.pack();                       
            }
        });

        // ---------- envio (reabre se houver erro, sem perder o que foi digitado) ----------
        while (true) {
            int r = JOptionPane.showConfirmDialog(pai, form, "Criar conta",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }
            try {
                Usuario criado;
                if (PACIENTE.equals(tipo.getSelectedItem())) {
                    criado = cadastro.cadastrarPaciente(nome.getText(), login.getText(),
                            new String(senha.getPassword()), new String(confirmacao.getPassword()),
                            idade.getText(), String.valueOf(plano.getSelectedItem()));
                } else {
                    criado = cadastro.cadastrarMedico(nome.getText(), login.getText(),
                            new String(senha.getPassword()), new String(confirmacao.getPassword()),
                            (Especialidade) especialidade.getSelectedItem(), valor.getText(), planos.getText());
                }
                Ui.info(pai, "Conta criada! Bem-vindo(a), " + criado.getNome() + ".");
                return Optional.of(criado);
            } catch (ClinicaException ex) {
                Ui.erro(pai, ex);
            }
        }
    }
}
