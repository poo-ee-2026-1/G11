public class Inversor extends Equipamento {
    public Inversor(String id, String marca, double potencia) {
        super(id, marca, potencia);
    }

    @Override
    public void exibirDetalhes() {
        System.out.println("[INVERSOR] ID: " + getId() + " | Marca: " + getMarca() + 
                           " | Potência: " + getPotencia() + " kW" +
                           " | Geração Atual: " + getGeracaoAtual() + " kWh");
    }
}