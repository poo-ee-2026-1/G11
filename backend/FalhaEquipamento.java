public class FalhaEquipamento extends Evento {
    private final String idEquipamentoFalho;

    public FalhaEquipamento(String dataHora, String descricao, String idEquipamentoFalho) {
        super(dataHora, descricao);
        this.idEquipamentoFalho = idEquipamentoFalho;
    }

    @Override
    public void registrarLog() {
        System.out.println("[" + getDataHora() + "] ⚠️ ALERTA CRÍTICO: Falha no equipamento " 
                           + idEquipamentoFalho + " -> " + getDescricao());
    }
}