public abstract class Equipamento {
    protected String id;
    protected String marca;
    protected double potencia; // Em kW
    protected double geracaoAtual; // Em kWh

    public Equipamento(String id, String marca, double potencia) {
        this.id = id;
        this.marca = marca;
        this.potencia = potencia;
        this.geracaoAtual = 0.0;
    }

    public String getId() { return id; }
    public String getMarca() { return marca; }
    public double getPotencia() { return potencia; }
    public double getGeracaoAtual() { return geracaoAtual; }

    public void registrarGeracao(double valor) {
        if (valor > 0) {
            this.geracaoAtual += valor;
        }
    }

    public abstract void exibirDetalhes();
}