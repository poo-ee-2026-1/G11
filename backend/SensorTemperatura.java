public class SensorTemperatura extends Sensor {
    public SensorTemperatura(String id) {
        super(id);
    }

    @Override
    public double lerDados() {
        return valorAtual; // Retorna a temperatura real em graus Celsius (°C)
    }
}