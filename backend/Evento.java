public abstract class Evento {
    private final String dataHora;
    private final String descricao;

    public Evento(String dataHora, String descricao) {
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    public String getDataHora() { return dataHora; }
    public String getDescricao() { return descricao; }
    
    public abstract void registrarLog();
}