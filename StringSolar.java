public class StringSolar extends Equipamento {
    private int quantidadeModulos;

    public StringSolar(String id, String marca, double potencia, int quantidadeModulos) {
        super(id, marca, potencia);
        this.quantidadeModulos = quantidadeModulos;
    }

    @Override
    public void exibirDetalhes() {
        System.out.println("[STRING] ID: " + getId() + " | Módulos: " + quantidadeModulos);
    }
}