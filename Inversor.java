public class Inversor extends Equipamento {
    public Inversor(String id, String marca, double potencia) {
        super(id, marca, potencia);
    }

    @Override
    public void exibirDetalhes() {
        System.out.println("[INVERSOR] ID: " + getId() + " | Potência: " + getPotencia() + "kW");
    }
}