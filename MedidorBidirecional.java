public class MedidorBidirecional extends Equipamento {
    public MedidorBidirecional(String id, String marca) {
        super(id, marca, 0.0); // Medidor não tem potência própria
    }

    @Override
    public void exibirDetalhes() {
        System.out.println("[MEDIDOR] ID: " + getId() + " monitorando a rede.");
    }
}