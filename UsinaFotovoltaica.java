import java.util.ArrayList;

public class UsinaFotovoltaica {
    private String nome;
    private String localidade;
    private double irradiacaoReferencia; // Agora começa vazia
    
    private ArrayList<Equipamento> equipamentos;
    private ArrayList<Evento> logEventos;

    // CONSTRUTOR ATUALIZADO (Apenas Nome e Local)
    public UsinaFotovoltaica(String nome, String localidade) {
        this.nome = nome;
        this.localidade = localidade;
        this.irradiacaoReferencia = 0.0;
        this.equipamentos = new ArrayList<>();
        this.logEventos = new ArrayList<>();
    }

    // NOVO MÉTODO: Permite definir a referência só quando for usar
    public void setIrradiacaoReferencia(double ref) {
        this.irradiacaoReferencia = ref;
    }

    // ... (Os métodos adicionarEquipamento, registrarEvento e listarEquipamentos continuam iguais) ...
    public void adicionarEquipamento(Equipamento e) { equipamentos.add(e); }
    public void registrarEvento(Evento e) { logEventos.add(e); e.registrarLog(); }
    
    public void listarEquipamentos() {
        System.out.println("\n--- EQUIPAMENTOS DA USINA " + nome + " ---");
        if (equipamentos.isEmpty()) System.out.println("Nenhum equipamento cadastrado.");
        for (Equipamento e : equipamentos) { e.exibirDetalhes(); }
    }

    // ... (Os métodos analisarClima e analisarTemperatura continuam EXATAMENTE iguais) ...
    public void analisarClima(SensorIrradiacao sensorIrradiacao) {
        double leitura = sensorIrradiacao.lerDados();
        double porcentagemSol = (leitura / irradiacaoReferencia) * 100;

        System.out.println("\n--- ANÁLISE AMBIENTAL: " + localidade + " ---");
        System.out.println("Referência Local: " + irradiacaoReferencia + " W/m²");
        System.out.println("Leitura Atual: " + leitura + " W/m² (" + String.format("%.1f", porcentagemSol) + "%)");

        if (porcentagemSol < 40.0) {
            System.out.println("⚠️ ALERTA: Irradiação muito baixa para a região. Geração de energia será severamente impactada hoje.");
        } else if (porcentagemSol >= 90.0) {
            System.out.println("✅ Clima ideal. Usina operando em capacidade máxima de captação.");
        }
    }

    public void analisarTemperatura(SensorTemperatura sensorTemp) {
        double leitura = sensorTemp.lerDados();
        double tempIdeal = 25.0; 

        System.out.println("\n--- ANÁLISE TÉRMICA: " + nome + " ---");
        System.out.println("Temperatura Atual: " + leitura + " °C");

        if (leitura <= tempIdeal) {
            System.out.println("✅ Temperatura excelente. Painéis operando com eficiência térmica máxima.");
        } else if (leitura > 25.0 && leitura <= 40.0) {
            System.out.println("⚠️ ATENÇÃO: Temperatura elevada. Ocorrerá uma leve perda de eficiência na conversão de energia.");
        } else {
            System.out.println("🚨 ALERTA CRÍTICO: Superaquecimento (" + leitura + "°C). Risco de perda severa de eficiência e desgaste dos inversores!");
        }
    }
}