public class Administrador extends Usuario {
    public Administrador(String nome, String login, String senha) {
        super(nome, login, senha, "MASTER");
    }

    @Override
    public void exibirMenu() {
        System.out.println("Bem-vindo MASTER " + getNome() + ". Acesso total concedido: Alterar dados, gerenciar equipamentos.");
    }
}