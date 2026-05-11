public class StringSolar extends Equipamento {
    private final int numeroPaineis;
    private final double potenciaPorPainel;

    public StringSolar(String id, String marca, int numeroPaineis, double potenciaPorPainelW) {
        // Calcula a potência total em kW (Qtd * Potência em Watts / 1000)
        super(id, marca, (numeroPaineis * potenciaPorPainelW) / 1000.0);
        this.numeroPaineis = numeroPaineis;
        this.potenciaPorPainel = potenciaPorPainelW;
    }

    @Override
    public void exibirDetalhes() {
        System.out.println("[STRING] ID: " + getId() + " | Marca: " + getMarca() + 
                           " | Painéis: " + numeroPaineis + "x de " + potenciaPorPainel + "W" +
                           " | Potência Total: " + getPotencia() + " kW" +
                           " | Geração Atual: " + getGeracaoAtual() + " kWh");
    }
}