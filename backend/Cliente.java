public class Cliente extends Usuario {
    public Cliente(String nome, String login, String senha) {
        super(nome, login, senha, "VISUALIZADOR");
    }

    @Override
    public void exibirMenu() {
        System.out.println("Bem-vindo CLIENTE " + getNome() + ". Acesso restrito: Apenas visualização de relatórios.");
    }
}