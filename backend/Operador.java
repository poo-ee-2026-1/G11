public class Operador extends Usuario {
    public Operador(String nome, String login, String senha) {
        super(nome, login, senha, "OPERADOR");
    }

    @Override
    public void exibirMenu() {
        System.out.println("Bem-vindo OPERADOR " + getNome() + ". Acesso padrão: Lançar medições e verificar alertas.");
    }
}