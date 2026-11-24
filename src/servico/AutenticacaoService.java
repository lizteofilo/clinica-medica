package servico;

import excecao.LoginInvalidoException;
import modelo.Usuario;
import repositorio.DadosClinica;

public class AutenticacaoService {

    private final DadosClinica dados;

    public AutenticacaoService(DadosClinica dados) {
        this.dados = dados;
    }

    public Usuario login(String login, String senha) throws LoginInvalidoException {
        return dados.buscarUsuarioPorLogin(login)
                .filter(u -> u.autenticar(senha))
                .orElseThrow(() -> new LoginInvalidoException("Login ou senha inválidos. Tente novamente."));
    }
}
