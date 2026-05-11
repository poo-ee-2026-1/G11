public class ModuloFotovoltaico extends Equipamento {
    public ModuloFotovoltaico(String id, String marca, double potencia) {
        super(id, marca, potencia);
    }

    @Override
    public void exibirDetalhes() {
        System.out.println("[MÓDULO] Painel " + getId() + " de " + getPotencia() + "W");
    }
}