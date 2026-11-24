package repositorio;

import excecao.ClinicaException;
import modelo.Avaliacao;
import modelo.Consulta;
import modelo.Medico;
import modelo.Paciente;
import modelo.RegistroAtendimento;
import modelo.especialidade.CatalogoEspecialidades;

import java.time.LocalDate;
import java.util.List;

public final class DadosIniciais {

    private DadosIniciais() {
    }

    public static void popular(DadosClinica d) throws ClinicaException {
        Medico ana = medico(d, "Dra. Ana Souza", "ana", "Clínica Geral", 150, "Unimed", "Hapvida");
        Medico bruno = medico(d, "Dr. Bruno Lima", "bruno", "Pediatria", 180, "Unimed", "Hapvida");
        Medico carla = medico(d, "Dra. Carla Mendes", "carla", "Cardiologia", 200, "Hapvida", "Bradesco Saúde");
        // Daniel cadastrou Hapvida, mas a Dermatologia não aceita esse plano (regra da especialidade)
        medico(d, "Dr. Daniel Rocha", "daniel", "Dermatologia", 220, "Unimed", "Hapvida", "Bradesco Saúde");
        medico(d, "Dra. Elisa Castro", "elisa", "Clínica Geral", 130);

        Paciente maria = paciente(d, "Maria Oliveira", "maria", 34, "Hapvida");
        Paciente joao = paciente(d, "João Pereira", "joao", 52, Paciente.SEM_PLANO);
        Paciente lucas = paciente(d, "Lucas Silva", "lucas", 8, "Unimed");
        paciente(d, "Fernanda Costa", "fernanda", 27, Paciente.SEM_PLANO);
        paciente(d, "Pedro Alves", "pedro", 41, Paciente.SEM_PLANO);

        // Histórico: consultas passadas já realizadas e avaliadas
        consultaAvaliada(d, ana, maria, 30, 5, "Muito atenciosa, explicou tudo.");
        consultaAvaliada(d, ana, joao, 20, 4, "Boa consulta, um pouco de atraso.");
        consultaAvaliada(d, carla, joao, 15, 5, "Excelente cardiologista!");
        consultaAvaliada(d, bruno, lucas, 10, 3, "Ok, mas a espera foi longa.");
    }

    private static Medico medico(DadosClinica d, String nome, String login, String especialidade,
                                 double valor, String... planos) throws ClinicaException {
        Medico m = new Medico(d.gerarId(), nome, login, "123",
                CatalogoEspecialidades.porNome(especialidade), valor, List.of(planos));
        d.adicionarMedico(m);
        return m;
    }

    private static Paciente paciente(DadosClinica d, String nome, String login, int idade, String plano)
            throws ClinicaException {
        Paciente p = new Paciente(d.gerarId(), nome, login, "123", idade, plano);
        d.adicionarPaciente(p);
        return p;
    }

    private static void consultaAvaliada(DadosClinica d, Medico m, Paciente p, int diasAtras,
                                         int nota, String texto) throws ClinicaException {
        Consulta c = new Consulta(d.gerarId(), m, p, LocalDate.now().minusDays(diasAtras));
        double valor = p.temPlano() ? 0 : m.getValorConsultaParticular();
        c.realizar(new RegistroAtendimento("Consulta de rotina", "Sem alterações", "", "", "", ""), valor);
        d.adicionarConsulta(c);
        m.receberAvaliacao(new Avaliacao(c, nota, texto));
    }
}
