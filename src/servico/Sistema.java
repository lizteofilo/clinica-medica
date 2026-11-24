package servico;

import repositorio.DadosClinica;

public class Sistema {

    private final DadosClinica dados;
    private final AutenticacaoService autenticacao;
    private final PesquisaService pesquisa;
    private final ConsultaService consultas;
    private final CadastroService cadastro;

    public Sistema(DadosClinica dados) {
        this.dados = dados;
        this.autenticacao = new AutenticacaoService(dados);
        this.pesquisa = new PesquisaService(dados);
        this.consultas = new ConsultaService(dados);
        this.cadastro = new CadastroService(dados);
    }

    public DadosClinica getDados() {
        return dados;
    }

    public AutenticacaoService getAutenticacao() {
        return autenticacao;
    }

    public PesquisaService getPesquisa() {
        return pesquisa;
    }

    public ConsultaService getConsultas() {
        return consultas;
    }

    public CadastroService getCadastro() {
        return cadastro;
    }
}
