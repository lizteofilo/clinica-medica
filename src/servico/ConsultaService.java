package servico;

import excecao.AcessoNegadoException;
import excecao.AgendaLotadaException;
import excecao.ClinicaException;
import excecao.DadosInvalidosException;
import excecao.OperacaoNaoPermitidaException;
import modelo.Consulta;
import modelo.EstadoConsulta;
import modelo.ListaEspera;
import modelo.Medico;
import modelo.Notificacao;
import modelo.Paciente;
import modelo.RegistroAtendimento;
import modelo.Usuario;
import repositorio.DadosClinica;

import java.time.LocalDate;
import java.util.Optional;

public class ConsultaService {

    private final DadosClinica dados;

    public ConsultaService(DadosClinica dados) {
        this.dados = dados;
    }

    public int vagasDisponiveis(Medico medico, LocalDate data) {
        long ocupadas = dados.consultasDoMedicoNaData(medico, data).stream().filter(Consulta::ocupaVaga).count();
        return Math.max(0, medico.getLimiteDiario() - (int) ocupadas);
    }

    // ============================================================ agendamento

    public Consulta agendar(Paciente paciente, Medico medico, LocalDate data) throws ClinicaException {
        validarSolicitacao(paciente, medico, data);
        if (vagasDisponiveis(medico, data) == 0) {
            throw new AgendaLotadaException("A agenda de " + medico.getNome() + " está lotada em "
                    + data.format(Consulta.FORMATO_DATA) + " (limite de " + medico.getLimiteDiario()
                    + " consultas para " + medico.getEspecialidade() + "). Você pode entrar na lista de espera.");
        }
        Consulta consulta = new Consulta(dados.gerarId(), medico, paciente, data);
        dados.adicionarConsulta(consulta);
        return consulta;
    }

    public int entrarNaListaEspera(Paciente paciente, Medico medico, LocalDate data) throws ClinicaException {
        validarSolicitacao(paciente, medico, data);
        if (vagasDisponiveis(medico, data) > 0) {
            throw new OperacaoNaoPermitidaException("Ainda há vagas nesta data. Faça o agendamento normalmente.");
        }
        return dados.obterListaEspera(medico, data).entrar(paciente);
    }

    private void validarSolicitacao(Paciente paciente, Medico medico, LocalDate data) throws ClinicaException {
        if (paciente == null || medico == null || data == null) {
            throw new DadosInvalidosException("Selecione o médico e informe a data.");
        }
        if (data.isBefore(LocalDate.now())) {
            throw new DadosInvalidosException("Não é possível agendar para uma data que já passou.");
        }
        if (paciente.temPlano() && !medico.atendePlano(paciente.getPlano())) {
            throw new OperacaoNaoPermitidaException(medico.getNome() + " não atende o plano " + paciente.getPlano() + ".");
        }
        boolean jaAgendado = dados.consultasDoMedicoNaData(medico, data).stream()
                .anyMatch(c -> c.getPaciente().equals(paciente) && c.getEstado() != EstadoConsulta.CANCELADA);
        if (jaAgendado) {
            throw new OperacaoNaoPermitidaException("Você já possui consulta com " + medico.getNome()
                    + " em " + data.format(Consulta.FORMATO_DATA) + ".");
        }
    }

    // ============================================================ cancelamento

    public Optional<Consulta> cancelar(Consulta consulta, Usuario solicitante) throws ClinicaException {
        if (!consulta.getPaciente().equals(solicitante) && !consulta.getMedico().equals(solicitante)) {
            throw new AcessoNegadoException("Apenas o paciente ou o médico da consulta podem cancelá-la.");
        }
        consulta.cancelar();
        return promoverPrimeiroDaFila(consulta.getMedico(), consulta.getData());
    }

    private Optional<Consulta> promoverPrimeiroDaFila(Medico medico, LocalDate data) {
        Optional<Paciente> primeiro = dados.buscarListaEspera(medico, data).flatMap(ListaEspera::removerPrimeiro);
        if (primeiro.isEmpty()) {
            return Optional.empty();
        }
        Paciente promovido = primeiro.get();
        Consulta nova = new Consulta(dados.gerarId(), medico, promovido, data);
        dados.adicionarConsulta(nova);
        dados.adicionarNotificacao(new Notificacao(dados.gerarId(), promovido,
                "Boa notícia! Abriu uma vaga e sua consulta com " + medico.getNome() + " em "
                        + nova.getDataFormatada() + " foi AGENDADA automaticamente (você estava na lista de espera)."));
        return Optional.of(nova);
    }

    // ============================================================ realização

    public void realizar(Consulta consulta, Medico medico, RegistroAtendimento registro) throws ClinicaException {
        if (!consulta.getMedico().equals(medico)) {
            throw new AcessoNegadoException("Apenas o médico responsável pode realizar esta consulta.");
        }
        Paciente paciente = consulta.getPaciente();
        double valor = paciente.temPlano() ? 0.0 : medico.getValorConsultaParticular();
        consulta.realizar(registro, valor);
    }

    // ============================================================ notificações

    public void marcarNotificacoesComoLidas(Paciente paciente) {
        dados.notificacoesDoPaciente(paciente).forEach(Notificacao::marcarComoLida);
    }
}
