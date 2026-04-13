public abstract class Evento {
    private String dataHora;
    private String descricao;

    public Evento(String dataHora, String descricao) {
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
    public abstract void registrarLog();
}