import java.util.ArrayList;
import java.util.Scanner;

public class MenuInterativo {
    public static void main(String[] args) {
        
        // 1. CORREÇÃO DA LÂMPADA AZUL (Try-with-resources): 
        // Agora o Java fecha o Scanner automaticamente no final, sem precisar do leitor.close()
        try (Scanner leitor = new Scanner(System.in)) {
            UsinaFotovoltaica usina = new UsinaFotovoltaica();
            ArrayList<Usuario> bancoDeUsuarios = new ArrayList<>();

            System.out.println("=================================================");
            System.out.println("  BEM-VINDO AO SUPERVISÓRIO DE USINA FOTOVOLTAICA  ");
            System.out.println("=================================================");

            System.out.println("\n--- CADASTRO DE OPERADOR ---");
            System.out.print("Nome completo: ");
            String nomeUser = leitor.nextLine();
            
            System.out.print("Crie um e-mail/login: ");
            String emailUser = leitor.nextLine();

            System.out.print("Crie uma senha: ");
            String senhaUser = leitor.nextLine();
            
            bancoDeUsuarios.add(new Operador(nomeUser, emailUser, senhaUser));
            System.out.println(">>> Operador " + nomeUser + " autenticado com sucesso!\n");

            int opcao = 0;

            while (opcao != 7) { 
                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1 - Cadastrar inversor");
                System.out.println("2 - Cadastrar string solar");
                System.out.println("3 - Registrar geração de energia");
                System.out.println("4 - Exibir geração total");
                System.out.println("5 - Verificar falhas nos equipamentos");
                System.out.println("6 - Calcular porcentagem de desempenho (via Irradiação)"); 
                System.out.println("7 - Sair"); 
                System.out.print("Escolha uma opção: ");
                
                opcao = leitor.nextInt();
                leitor.nextLine(); // Limpa o buffer

                // 2. CORREÇÃO DO TRIÂNGULO AMARELO (Rule Switch):
                // Uso do switch moderno do Java 14+ com setinhas (->), eliminando os "breaks"
                switch (opcao) {
                    case 1 -> {
                        System.out.print("Digite o ID do Inversor: ");
                        String idInv = leitor.nextLine();
                        System.out.print("Digite a Marca: ");
                        String marcaInv = leitor.nextLine();
                        System.out.print("Digite a Potência (kW): ");
                        double potInv = leitor.nextDouble();
                        leitor.nextLine(); 
                        usina.cadastrarEquipamento(new Inversor(idInv, marcaInv, potInv));
                    }
                    case 2 -> {
                        System.out.print("Digite o ID da String: ");
                        String idStr = leitor.nextLine();
                        System.out.print("Digite a Marca das Placas: ");
                        String marcaStr = leitor.nextLine();
                        System.out.print("Digite a Quantidade de Painéis: ");
                        int qtdPaineis = leitor.nextInt();
                        System.out.print("Digite a Potência de CADA painel (em Watts, ex: 550): ");
                        double potPainel = leitor.nextDouble();
                        leitor.nextLine(); 
                        usina.cadastrarEquipamento(new StringSolar(idStr, marcaStr, qtdPaineis, potPainel));
                    }
                    case 3 -> {
                        System.out.print("Digite o ID do equipamento: ");
                        String idBusca = leitor.nextLine();
                        System.out.print("Digite a quantidade de kWh gerada: ");
                        double geracao = leitor.nextDouble();
                        leitor.nextLine(); 
                        usina.registrarGeracao(idBusca, geracao);
                    }
                    case 4 -> usina.exibirGeracaoTotal();
                    case 5 -> usina.verificarFalhas();
                    case 6 -> {
                        System.out.println("\n--- CÁLCULO DE DESEMPENHO ---");
                        System.out.print("Digite a irradiação solar do local (ex: 5,0): ");
                        double irradiacao = leitor.nextDouble();
                        System.out.print("Digite a eficiência do sistema (ex: 0,80 para 80%): ");
                        double eficiencia = leitor.nextDouble();
                        System.out.print("Digite o tempo em dias (ex: 30 para mensal): ");
                        int dias = leitor.nextInt();
                        leitor.nextLine(); 
                        usina.calcularDesempenho(irradiacao, eficiencia, dias);
                    }
                    case 7 -> System.out.println("Sessão do operador " + nomeUser + " encerrada. Desligando...");
                    default -> System.out.println("Opção inválida! Tente novamente.");
                }
            }
        } 
    }
}