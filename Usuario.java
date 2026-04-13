public abstract class Usuario {
    // Adicionamos o 'final' porque o nome, login e senha não mudam depois de criados
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

    // Ter os Getters faz o aviso de "never read" sumir!
    public String getNome() { return nome; }
    public String getNivelAcesso() { return nivelAcesso; }

    public boolean autenticar(String loginTentativa, String senhaTentativa) {
        return this.login.equals(loginTentativa) && this.senha.equals(senhaTentativa);
    }

    public abstract void exibirMenu();
}