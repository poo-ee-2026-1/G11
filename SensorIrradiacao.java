public class SensorIrradiacao extends Sensor {
    public SensorIrradiacao(String id) {
        super(id);
    }

    @Override
    public double lerDados() {
        return valorAtual; 
    }
}