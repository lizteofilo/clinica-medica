package ui;

import servico.Sistema;
import excecao.ClinicaException;
import modelo.Consulta;
import modelo.EstadoConsulta;
import modelo.ListaEspera;
import modelo.Medico;
import modelo.Paciente;
import modelo.RegistroAtendimento;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TelaMedico extends JFrame {

    private final Sistema sistema;
    private final Medico medico;
    private final JLabel cabecalho = new JLabel();
    private final DefaultTableModel modelo =
            Ui.modelo("#", "Data", "Paciente", "Idade", "Plano", "Estado", "Lista de espera do dia");
    private final JTable tabela = Ui.tabela(modelo);
    private final List<Consulta> listadas = new ArrayList<>();
    private final JCheckBox somenteAgendadas = new JCheckBox("Mostrar só AGENDADAS", true);

    public TelaMedico(Sistema sistema, Medico medico) {
        super("Clínica Médica - Médico");
        this.sistema = sistema;
        this.medico = medico;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        topo.add(cabecalho, BorderLayout.WEST);
        JPanel botoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton meusDados = new JButton("Meus dados");
        JButton sair = new JButton("Sair");
        botoesTopo.add(meusDados);
        botoesTopo.add(sair);
        topo.add(botoesTopo, BorderLayout.EAST);
        meusDados.addActionListener(e -> {
            if (DialogoMeusDados.abrir(this, medico)) {
                atualizar();
            }
        });
        sair.addActionListener(e -> {
            new TelaLogin(sistema).setVisible(true);
            dispose();
        });

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton realizar = new JButton("Realizar consulta");
        JButton cancelar = new JButton("Cancelar consulta");
        botoes.add(somenteAgendadas);
        botoes.add(realizar);
        botoes.add(cancelar);
        somenteAgendadas.addActionListener(e -> atualizar());
        realizar.addActionListener(e -> selecionada().ifPresent(this::realizar));
        cancelar.addActionListener(e -> selecionada().ifPresent(this::cancelar));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createTitledBorder("Minha agenda"));
        centro.add(new JScrollPane(tabela), BorderLayout.CENTER);

        add(topo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
        setSize(980, 520);
        setLocationRelativeTo(null);
        atualizar();
    }

    private void atualizar() {
        String media = medico.getAvaliacoes().isEmpty() ? "sem avaliações"
                : String.format("%.1f ★", medico.getMediaAvaliacoes());
        cabecalho.setText("<html><b>" + medico.getNome() + "</b> — " + medico.getEspecialidade()
                + " (" + medico.getEspecialidade().descreverRegras() + ")<br>Planos: "
                + (medico.getPlanosAtendidos().isEmpty() ? "nenhum" : String.join(", ", medico.getPlanosAtendidos()))
                + "  |  Particular: " + Ui.dinheiro(medico.getValorConsultaParticular())
                + "  |  Média: " + media + "</html>");

        listadas.clear();
        sistema.getDados().consultasDoMedico(medico).stream()
                .filter(c -> !somenteAgendadas.isSelected() || c.getEstado() == EstadoConsulta.AGENDADA)
                .forEach(listadas::add);
        modelo.setRowCount(0);
        for (Consulta c : listadas) {
            String fila = sistema.getDados().buscarListaEspera(medico, c.getData())
                    .map(ListaEspera::getPacientes)
                    .filter(l -> !l.isEmpty())
                    .map(l -> l.stream().map(Paciente::getNome).collect(Collectors.joining(", ")))
                    .orElse("-");
            modelo.addRow(new Object[]{c.getId(), c.getDataFormatada(), c.getPaciente().getNome(),
                    c.getPaciente().getIdade(), c.getPaciente().getPlanoDescricao(), c.getEstado(), fila});
        }
    }

    private Optional<Consulta> selecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Ui.info(this, "Selecione uma consulta na agenda.");
            return Optional.empty();
        }
        return Optional.of(listadas.get(linha));
    }

    private void realizar(Consulta c) {
        if (c.getData().isAfter(LocalDate.now())
                && !Ui.confirmar(this, "Esta consulta está marcada para " + c.getDataFormatada()
                + ". Deseja registrá-la mesmo assim?")) {
            return;
        }
        JTextArea sintomas = area(), diagnostico = area(), tratamento = area(),
                medicamentos = area(), exames = area(), observacoes = area();
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 3, 3, 3);
        g.fill = GridBagConstraints.BOTH;
        String[] rotulos = {"Sintomas*", "Diagnóstico*", "Tratamento", "Medicamentos", "Exames solicitados", "Observações"};
        JTextArea[] campos = {sintomas, diagnostico, tratamento, medicamentos, exames, observacoes};
        for (int i = 0; i < campos.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0;
            form.add(new JLabel(rotulos[i]), g);
            g.gridx = 1; g.weightx = 1;
            form.add(new JScrollPane(campos[i]), g);
        }

        while (true) { 
            int r = JOptionPane.showConfirmDialog(this, form, "Atendimento - " + c.getPaciente().getNome(),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                RegistroAtendimento reg = new RegistroAtendimento(sintomas.getText(), diagnostico.getText(),
                        tratamento.getText(), medicamentos.getText(), exames.getText(), observacoes.getText());
                sistema.getConsultas().realizar(c, medico, reg);
                Ui.info(this, "Consulta marcada como REALIZADA.");
                atualizar();
                return;
            } catch (ClinicaException ex) {
                Ui.erro(this, ex);
            }
        }
    }

    private void cancelar(Consulta c) {
        if (!Ui.confirmar(this, "Cancelar a consulta de " + c.getPaciente().getNome() + " em " + c.getDataFormatada() + "?")) {
            return;
        }
        try {
            Optional<Consulta> promovida = sistema.getConsultas().cancelar(c, medico);
            Ui.info(this, "Consulta cancelada." + promovida
                    .map(p -> "\n" + p.getPaciente().getNome() + " foi promovido(a) da lista de espera para a vaga.")
                    .orElse(""));
            atualizar();
        } catch (ClinicaException ex) {
            Ui.erro(this, ex);
        }
    }

    private static JTextArea area() {
        JTextArea a = new JTextArea(2, 35);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        return a;
    }
}
