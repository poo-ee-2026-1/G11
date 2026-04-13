public abstract class Equipamento {
    // ID, marca e potência não mudam depois de fabricados (final)
    private final String id;
    private final String marca;
    private final double potencia;
    
    private boolean statusAtivo; 

    public Equipamento(String id, String marca, double potencia) {
        this.id = id;
        this.marca = marca;
        this.potencia = potencia;
        this.statusAtivo = true; 
    }

    public String getId() { return id; }
    public String getMarca() { return marca; }
    public double getPotencia() { return potencia; }
    public boolean isAtivo() { return statusAtivo; }
    
    public void setStatus(boolean status) { this.statusAtivo = status; }

    public abstract void exibirDetalhes();
}