public abstract class Sensor {
    private final String id;
    protected double valorAtual; // O clima simulado no momento

    public Sensor(String id) {
        this.id = id;
        this.valorAtual = 0.0;
    }

    public String getId() { return id; }
    
    // Método para o usuário do sistema "simular o clima"
    public void simularAmbiente(double valor) {
        this.valorAtual = valor;
    }

    public abstract double lerDados();
}