package repositorio;

import excecao.OperacaoNaoPermitidaException;
import modelo.Consulta;
import modelo.ListaEspera;
import modelo.Medico;
import modelo.Notificacao;
import modelo.Paciente;
import modelo.Usuario;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DadosClinica {

    private record ChaveAgenda(long medicoId, LocalDate data) {
    }

    private final Map<Long, Medico> medicos = new LinkedHashMap<>();
    private final Map<Long, Paciente> pacientes = new LinkedHashMap<>();
    private final Map<Long, Consulta> consultas = new LinkedHashMap<>();
    private final Map<Long, Notificacao> notificacoes = new LinkedHashMap<>();
    private final Map<ChaveAgenda, ListaEspera> listasEspera = new LinkedHashMap<>();
    private long proximoId = 1;

    public long gerarId() {
        return proximoId++;
    }

    // ---------------- usuários ----------------

    public void adicionarMedico(Medico m) throws OperacaoNaoPermitidaException {
        verificarLoginLivre(m.getLogin());
        medicos.put(m.getId(), m);
    }

    public void adicionarPaciente(Paciente p) throws OperacaoNaoPermitidaException {
        verificarLoginLivre(p.getLogin());
        pacientes.put(p.getId(), p);
    }

    private void verificarLoginLivre(String login) throws OperacaoNaoPermitidaException {
        if (buscarUsuarioPorLogin(login).isPresent()) {
            throw new OperacaoNaoPermitidaException("Já existe um usuário com o login '" + login + "'.");
        }
    }

    public Optional<Usuario> buscarUsuarioPorLogin(String login) {
        if (login == null) {
            return Optional.empty();
        }
        return Stream.<Usuario>concat(medicos.values().stream(), pacientes.values().stream())
                .filter(u -> u.getLogin().equalsIgnoreCase(login.trim()))
                .findFirst();
    }

    public Collection<Medico> getMedicos() {
        return Collections.unmodifiableCollection(medicos.values());
    }

    public Collection<Paciente> getPacientes() {
        return Collections.unmodifiableCollection(pacientes.values());
    }

    // ---------------- consultas ----------------

    public void adicionarConsulta(Consulta c) {
        consultas.put(c.getId(), c);
    }

    public Collection<Consulta> getConsultas() {
        return Collections.unmodifiableCollection(consultas.values());
    }

    public List<Consulta> consultasDoMedicoNaData(Medico m, LocalDate data) {
        return consultas.values().stream()
                .filter(c -> c.getMedico().equals(m) && c.getData().equals(data))
                .toList();
    }

    public List<Consulta> consultasDoMedico(Medico m) {
        return consultas.values().stream()
                .filter(c -> c.getMedico().equals(m))
                .sorted(Comparator.comparing(Consulta::getData))
                .toList();
    }

    public List<Consulta> consultasDoPaciente(Paciente p) {
        return consultas.values().stream()
                .filter(c -> c.getPaciente().equals(p))
                .sorted(Comparator.comparing(Consulta::getData))
                .toList();
    }

    // ---------------- notificações ----------------

    public void adicionarNotificacao(Notificacao n) {
        notificacoes.put(n.getId(), n);
    }

    public List<Notificacao> notificacoesDoPaciente(Paciente p) {
        return notificacoes.values().stream()
                .filter(n -> n.getPaciente().equals(p))
                .sorted(Comparator.comparing(Notificacao::getDataHora).reversed())
                .toList();
    }

    // ---------------- listas de espera ----------------

    /** Retorna a lista do médico na data, criando-a se ainda não existir. */
    public ListaEspera obterListaEspera(Medico m, LocalDate data) {
        return listasEspera.computeIfAbsent(new ChaveAgenda(m.getId(), data), k -> new ListaEspera(m, data));
    }

    public Optional<ListaEspera> buscarListaEspera(Medico m, LocalDate data) {
        return Optional.ofNullable(listasEspera.get(new ChaveAgenda(m.getId(), data)));
    }
}
