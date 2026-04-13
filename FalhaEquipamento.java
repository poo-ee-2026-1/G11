public class FalhaEquipamento extends Evento {
    private String idEquipamentoFalho;

    public FalhaEquipamento(String dataHora, String descricao, String idEquipamentoFalho) {
        super(dataHora, descricao);
        this.idEquipamentoFalho = idEquipamentoFalho;
    }

    @Override
    public void registrarLog() {
        System.out.println("⚠️ ALERTA CRÍTICO: Falha no equipamento " + idEquipamentoFalho + " -> " + getDescricao());
    }
}