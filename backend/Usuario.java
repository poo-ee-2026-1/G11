public abstract class Usuario {
    private final String nome;
    private final String login;
    private final String senha;
    private final String nivelAcesso;

    public Usuario(String nome, String login, String senha, String nivelAcesso) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
    }

    public String getNome() { return nome; }
    public String getNivelAcesso() { return nivelAcesso; }

    public boolean autenticar(String loginTentativa, String senhaTentativa) {
        return this.login.equals(loginTentativa) && this.senha.equals(senhaTentativa);
    }

    public abstract void exibirMenu();
}