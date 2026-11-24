package servico;

import excecao.ClinicaException;
import excecao.DadosInvalidosException;
import excecao.OperacaoNaoPermitidaException;
import modelo.Medico;
import modelo.Paciente;
import modelo.especialidade.Especialidade;
import repositorio.DadosClinica;

import java.util.Arrays;
import java.util.List;

public class CadastroService {

    public static final int TAMANHO_MINIMO_SENHA = 3;

    private final DadosClinica dados;

    public CadastroService(DadosClinica dados) {
        this.dados = dados;
    }

    public Paciente cadastrarPaciente(String nome, String login, String senha, String confirmacaoSenha,
                                      String idadeTexto, String plano) throws ClinicaException {
        validarComuns(nome, login, senha, confirmacaoSenha);
        int idade = lerIdade(idadeTexto);
        try {
            Paciente p = new Paciente(dados.gerarId(), nome, login, senha, idade, plano);
            dados.adicionarPaciente(p);          
            return p;
        } catch (IllegalArgumentException e) {  
            throw new DadosInvalidosException(e.getMessage());
        }
    }

    public Medico cadastrarMedico(String nome, String login, String senha, String confirmacaoSenha,
                                  Especialidade especialidade, String valorTexto, String planosTexto)
            throws ClinicaException {
        validarComuns(nome, login, senha, confirmacaoSenha);
        if (especialidade == null) {
            throw new DadosInvalidosException("Escolha a especialidade.");
        }
        double valor = lerValor(valorTexto);
        List<String> planos = planosTexto == null || planosTexto.isBlank()
                ? List.of()
                : Arrays.stream(planosTexto.split(",")).map(String::trim).filter(p -> !p.isEmpty()).toList();
        try {
            Medico m = new Medico(dados.gerarId(), nome, login, senha, especialidade, valor, planos);
            dados.adicionarMedico(m);
            return m;
        } catch (IllegalArgumentException e) {
            throw new DadosInvalidosException(e.getMessage());
        }
    }

    // ------------------------------------------------------------ validações

    private void validarComuns(String nome, String login, String senha, String confirmacao)
            throws ClinicaException {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("Informe o nome.");
        }
        if (login == null || !login.trim().matches("[A-Za-z0-9._-]{3,20}")) {
            throw new DadosInvalidosException("O login deve ter de 3 a 20 caracteres, sem espaços "
                    + "(letras, números, ponto, hífen ou sublinhado).");
        }
        if (dados.buscarUsuarioPorLogin(login).isPresent()) {
            throw new OperacaoNaoPermitidaException("O login '" + login.trim() + "' já está em uso. Escolha outro.");
        }
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new DadosInvalidosException("A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        if (!senha.equals(confirmacao)) {
            throw new DadosInvalidosException("A confirmação não é igual à senha.");
        }
    }

    private static int lerIdade(String texto) throws DadosInvalidosException {
        try {
            int idade = Integer.parseInt(texto.trim());
            if (idade < 0 || idade > 130) {
                throw new DadosInvalidosException("Idade inválida: informe um valor entre 0 e 130.");
            }
            return idade;
        } catch (NumberFormatException | NullPointerException e) {
            throw new DadosInvalidosException("Idade inválida: \"" + texto + "\". Digite apenas números.");
        }
    }

    private static double lerValor(String texto) throws DadosInvalidosException {
        try {
            double valor = Double.parseDouble(texto.trim().replace("R$", "").trim().replace(",", "."));
            if (valor <= 0) {
                throw new DadosInvalidosException("O valor da consulta deve ser maior que zero.");
            }
            return valor;
        } catch (NumberFormatException | NullPointerException e) {
            throw new DadosInvalidosException("Valor inválido: \"" + texto + "\". Exemplo: 150 ou 150,00");
        }
    }
}
