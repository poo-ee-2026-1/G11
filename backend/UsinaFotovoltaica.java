import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UsinaFotovoltaica {
    private final ArrayList<Equipamento> equipamentos;

    public UsinaFotovoltaica() {
        this.equipamentos = new ArrayList<>();
    }

    // --- CADASTRO DE EQUIPAMENTOS ---
    public void cadastrarEquipamento(Equipamento equipamento) {
        equipamentos.add(equipamento);
        System.out.println("Equipamento cadastrado com sucesso!");
    }

    public void registrarGeracao(String idDesejado, double valorGerado) {
        boolean encontrado = false;
        for (Equipamento eq : equipamentos) {
            if (eq.getId().equals(idDesejado)) {
                eq.registrarGeracao(valorGerado);
                System.out.println("Geração registrada com sucesso no equipamento " + idDesejado);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.out.println("Erro: Equipamento não encontrado.");
        }
    }

    public void exibirGeracaoTotal() {
        double total = 0;
        System.out.println("\n--- GERAÇÃO DOS EQUIPAMENTOS ---");
        for (Equipamento eq : equipamentos) {
            eq.exibirDetalhes();
            total += eq.getGeracaoAtual();
        }
        System.out.println("==================================");
        System.out.println("CÁLCULO DA GERAÇÃO TOTAL: " + total + " kWh");
        System.out.println("==================================");
    }

    // --- LÓGICA DE FALHAS MANTIDA ---
    public void verificarFalhas() {
        System.out.println("\n--- VERIFICAÇÃO DE FALHAS ---");
        boolean temFalha = false;
        String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        for (Equipamento eq : equipamentos) {
            if (eq.getGeracaoAtual() == 0.0) {
                FalhaEquipamento falha = new FalhaEquipamento(
                    dataAtual, 
                    "Geração zerada detectada pelo sistema", 
                    eq.getId()
                );
                falha.registrarLog();
                temFalha = true;
            }
        }
        
        if (!temFalha) {
            System.out.println("  [v] STATUS: Todos os equipamentos estão operando normalmente.");
        }
    }

    // --- CÁLCULO DE DESEMPENHO (PERCENTUAL) ---
    public void calcularDesempenho(double irradiacao, double eficiencia, int dias) {
        System.out.println("\n--- RELATÓRIO DE DESEMPENHO DA USINA ---");
        System.out.println("Parâmetros: Irradiação=" + irradiacao + " | Eficiência(PR)=" + (eficiencia * 100) + "% | Tempo=" + dias + " dias\n");

        for (Equipamento eq : equipamentos) {
            double geracaoEsperada = eq.getPotencia() * irradiacao * eficiencia * dias;
            double geracaoReal = eq.getGeracaoAtual(); 
            double desempenhoAlcancado = 0.0;
            
            if (geracaoEsperada > 0) {
                desempenhoAlcancado = (geracaoReal / geracaoEsperada) * 100; 
            }

            System.out.printf("Equipamento: %s (%s)\n", eq.getId(), eq.getClass().getSimpleName());
            System.out.printf("  -> Geração Esperada: %.2f kWh\n", geracaoEsperada);
            System.out.printf("  -> Geração Real: %.2f kWh\n", geracaoReal);
            System.out.printf("  -> Meta Atingida: %.1f%%\n", desempenhoAlcancado);
            
            if (desempenhoAlcancado < 90.0 && geracaoEsperada > 0) {
                System.out.println("  [!] ALERTA: Desempenho abaixo do ideal (Sujeira ou Falha).");
            } else {
                System.out.println("  [v] STATUS: Operação excelente.");
            }
            System.out.println("----------------------------------------");
        }
    }
}