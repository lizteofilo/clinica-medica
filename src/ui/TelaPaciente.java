package ui;

import servico.Sistema;
import excecao.AgendaLotadaException;
import excecao.ClinicaException;
import modelo.Avaliacao;
import modelo.Consulta;
import modelo.Medico;
import modelo.Notificacao;
import modelo.Paciente;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TelaPaciente extends JFrame {

    private static final int ABA_NOTIFICACOES = 2;

    private final Sistema sistema;
    private final Paciente paciente;
    private final JTabbedPane abas = new JTabbedPane();
    private final JLabel saudacao = new JLabel();
    private final JLabel avisoPlano = new JLabel();

    private final JTextField campoBusca = new JTextField(20);
    private final JTextField campoData = new JTextField(LocalDate.now().plusDays(1).format(Ui.FORMATO_DATA), 10);
    private final DefaultTableModel modeloMedicos =
            Ui.modelo("Nome", "Especialidade", "Média", "Valor particular", "Avaliações recentes");
    private final List<Medico> medicosListados = new ArrayList<>();

    private final DefaultTableModel modeloConsultas = Ui.modelo("#", "Data", "Médico", "Especialidade", "Estado");
    private final JTable tabelaConsultas = Ui.tabela(modeloConsultas);
    private final List<Consulta> consultasListadas = new ArrayList<>();

    private final DefaultListModel<Notificacao> modeloNotificacoes = new DefaultListModel<>();

    public TelaPaciente(Sistema sistema, Paciente paciente) {
        super("Clínica Médica - Paciente");
        this.sistema = sistema;
        this.paciente = paciente;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        topo.add(saudacao, BorderLayout.WEST);
        JPanel botoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton meusDados = new JButton("Meus dados");
        JButton sair = new JButton("Sair");
        botoesTopo.add(meusDados);
        botoesTopo.add(sair);
        topo.add(botoesTopo, BorderLayout.EAST);
        meusDados.addActionListener(e -> {
            if (DialogoMeusDados.abrir(this, paciente)) {
                buscar();       
                atualizarTudo();
            }
        });
        sair.addActionListener(e -> {
            new TelaLogin(sistema).setVisible(true);
            dispose();
        });

        abas.addTab("Buscar médicos / Agendar", criarAbaBusca());
        abas.addTab("Minhas consultas", criarAbaConsultas());
        abas.addTab("Notificações", criarAbaNotificacoes());

        add(topo, BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);
        setSize(980, 540);
        setLocationRelativeTo(null);

        buscar();
        atualizarTudo();
        avisarNotificacoesNovas();
    }

    // ======================================================== Busca / agendamento

    private JPanel criarAbaBusca() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Nome ou especialidade:"));
        filtros.add(campoBusca);
        JButton buscar = new JButton("Buscar");
        filtros.add(buscar);
        filtros.add(avisoPlano);
        buscar.addActionListener(e -> buscar());
        campoBusca.addActionListener(e -> buscar());

        JTable tabela = Ui.tabela(modeloMedicos);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(340);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoes.add(new JLabel("Data desejada (dd/mm/aaaa):"));
        acoes.add(campoData);
        JButton agendar = new JButton("Agendar com o médico selecionado");
        acoes.add(agendar);
        agendar.addActionListener(e -> agendar(tabela.getSelectedRow()));

        p.add(filtros, BorderLayout.NORTH);
        p.add(new JScrollPane(tabela), BorderLayout.CENTER);
        p.add(acoes, BorderLayout.SOUTH);
        return p;
    }

    private void buscar() {
        avisoPlano.setText(paciente.temPlano()
                ? "  (mostrando apenas médicos que atendem " + paciente.getPlano() + ")" : "");
        medicosListados.clear();
        medicosListados.addAll(sistema.getPesquisa().pesquisar(campoBusca.getText(), paciente));
        modeloMedicos.setRowCount(0);
        for (Medico m : medicosListados) {
            String media = m.getAvaliacoes().isEmpty() ? "sem avaliações"
                    : String.format("%.1f ★ (%d)", m.getMediaAvaliacoes(), m.getAvaliacoes().size());
            String valor = paciente.temPlano() ? "coberto pelo plano" : Ui.dinheiro(m.getValorConsultaParticular());
            String recentes = m.getAvaliacoesRecentes(2).stream().map(Avaliacao::toString)
                    .collect(Collectors.joining("  |  "));
            modeloMedicos.addRow(new Object[]{m.getNome(), m.getEspecialidade(), media, valor, recentes});
        }
    }

    private void agendar(int linha) {
        if (linha < 0) {
            Ui.info(this, "Selecione um médico na tabela.");
            return;
        }
        Medico medico = medicosListados.get(linha);
        try {
            LocalDate data = Ui.lerData(campoData.getText());
            Consulta c = sistema.getConsultas().agendar(paciente, medico, data);
            Ui.info(this, "Consulta agendada com " + medico.getNome() + " em " + c.getDataFormatada() + ".");
            atualizarTudo();
        } catch (AgendaLotadaException lotada) {
            oferecerListaEspera(lotada, medico);
        } catch (ClinicaException ex) {
            Ui.erro(this, ex);
        }
    }

    private void oferecerListaEspera(AgendaLotadaException lotada, Medico medico) {
        if (!Ui.confirmar(this, lotada.getMessage() + "\n\nDeseja entrar na lista de espera?")) {
            return;
        }
        try {
            LocalDate data = Ui.lerData(campoData.getText());
            int pos = sistema.getConsultas().entrarNaListaEspera(paciente, medico, data);
            Ui.info(this, "Você entrou na lista de espera na posição " + pos
                    + ".\nSe uma vaga abrir, sua consulta será agendada automaticamente e você verá uma notificação.");
        } catch (ClinicaException ex) {
            Ui.erro(this, ex);
        }
    }

    // ======================================================== Minhas consultas 

    private JPanel criarAbaConsultas() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton cancelar = new JButton("Cancelar consulta selecionada");
        cancelar.addActionListener(e -> cancelarSelecionada());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(cancelar);
        p.add(new JScrollPane(tabelaConsultas), BorderLayout.CENTER);
        p.add(botoes, BorderLayout.SOUTH);
        return p;
    }

    private void cancelarSelecionada() {
        int linha = tabelaConsultas.getSelectedRow();
        if (linha < 0) {
            Ui.info(this, "Selecione uma consulta.");
            return;
        }
        Consulta c = consultasListadas.get(linha);
        if (!Ui.confirmar(this, "Cancelar a consulta de " + c.getDataFormatada() + " com " + c.getMedico().getNome() + "?")) {
            return;
        }
        try {
            sistema.getConsultas().cancelar(c, paciente);
            Ui.info(this, "Consulta cancelada.");
            atualizarTudo();
        } catch (ClinicaException ex) {
            Ui.erro(this, ex);       
        }
    }

    // ======================================================== Notificações

    private JPanel criarAbaNotificacoes() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton marcar = new JButton("Marcar todas como lidas");
        marcar.addActionListener(e -> {
            sistema.getConsultas().marcarNotificacoesComoLidas(paciente);
            atualizarTudo();
        });
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(marcar);
        p.add(new JScrollPane(new JList<>(modeloNotificacoes)), BorderLayout.CENTER);
        p.add(botoes, BorderLayout.SOUTH);
        return p;
    }

    private void avisarNotificacoesNovas() {
        long novas = sistema.getDados().notificacoesDoPaciente(paciente).stream().filter(n -> !n.isLida()).count();
        if (novas > 0) {
            abas.setSelectedIndex(ABA_NOTIFICACOES);
            Ui.info(this, "Você tem " + novas + " notificação(ões) nova(s).");
        }
    }

    // ======================================================== Atualização

    private void atualizarTudo() {
        saudacao.setText("Olá, " + paciente.getNome() + "  |  Plano: " + paciente.getPlanoDescricao());

        consultasListadas.clear();
        consultasListadas.addAll(sistema.getDados().consultasDoPaciente(paciente));
        modeloConsultas.setRowCount(0);
        for (Consulta c : consultasListadas) {
            modeloConsultas.addRow(new Object[]{c.getId(), c.getDataFormatada(), c.getMedico().getNome(),
                    c.getMedico().getEspecialidade(), c.getEstado()});
        }

        modeloNotificacoes.clear();
        List<Notificacao> notificacoes = sistema.getDados().notificacoesDoPaciente(paciente);
        notificacoes.forEach(modeloNotificacoes::addElement);
        long novas = notificacoes.stream().filter(n -> !n.isLida()).count();
        abas.setTitleAt(ABA_NOTIFICACOES, novas > 0 ? "Notificações (" + novas + ")" : "Notificações");
    }
}
