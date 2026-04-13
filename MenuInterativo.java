import java.util.ArrayList;
import java.util.Scanner;

public class MenuInterativo {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        ArrayList<Usuario> bancoDeUsuarios = new ArrayList<>();

        System.out.println("=====================================");
        System.out.println("  BEM-VINDO AO SUPERVISÓRIO DE USINA FOTOVOLTAICA  ");
        System.out.println("=====================================");

        // 1. CADASTRO DE USUÁRIO VEM PRIMEIRO
        System.out.println("\n--- 1. CADASTRO DE USUÁRIO ---");
        System.out.print("Nome completo: ");
        String nomeUser = leitor.nextLine();

        System.out.print("Crie um Login: ");
        String loginUser = leitor.nextLine();

        System.out.print("Crie uma Senha: ");
        String senhaUser = leitor.nextLine();

        int tipoUser = 0;
        while (tipoUser != 1 && tipoUser != 2) {
            System.out.print("Qual o seu nível de acesso? (1 - Administrador / 2 - Visualizador): ");
            try {
                tipoUser = leitor.nextInt();
            } catch (Exception e) {
                System.out.println("❌ Entrada inválida. Digite 1 ou 2.");
            }
            leitor.nextLine(); // Limpa o buffer do teclado
        }

        // Adiciona ao banco dependendo da escolha
        if (tipoUser == 1) {
            bancoDeUsuarios.add(new Administrador(nomeUser, loginUser, senhaUser));
        } else {
            bancoDeUsuarios.add(new Cliente(nomeUser, loginUser, senhaUser)); // Cliente = Visualizador
        }

        // 2. CONFIGURAÇÃO DA USINA VEM DEPOIS
        System.out.println("\n--- 2. CONFIGURAÇÃO DA USINA ---");
        System.out.print("Nome da Usina Fotovoltaica: ");
        String nomeUsina = leitor.nextLine();
        System.out.print("Localidade (Cidade/Estado): ");
        String local = leitor.nextLine();

        // Cria a usina sem precisar da irradiação agora
        UsinaFotovoltaica usina = new UsinaFotovoltaica(nomeUsina, local);

        System.out.println("\n✅ Sistema configurado! Pressione ENTER para ir para a Tela de Login.");
        leitor.nextLine();

        // 3. TELA DE LOGIN
        System.out.println("\n=====================================");
        System.out.println("         TELA DE LOGIN - " + nomeUsina.toUpperCase());
        System.out.println("=====================================");

        System.out.print("Login: ");
        String loginTentativa = leitor.nextLine();

        System.out.print("Senha: ");
        String senhaTentativa = leitor.nextLine();

        // Lógica de Autenticação
        Usuario usuarioLogado = null;
        for (Usuario u : bancoDeUsuarios) {
            if (u.autenticar(loginTentativa, senhaTentativa)) {
                usuarioLogado = u;
                break;
            }
        }

        // 4. O MENU PRINCIPAL
        if (usuarioLogado != null) {
            System.out.println("\n🔓 Login efetuado com sucesso!");
            usuarioLogado.exibirMenu(); // Mostra se é Master ou Visualizador

            int opcao = 0;
            while (opcao != 6) {
                System.out.println("\n--- MENU DE OPERAÇÃO ---");
                System.out.println("1 - Instalar Equipamento (Inversor/String)");
                System.out.println("2 - Simular Leituras Ambientais e Eficiência");
                System.out.println("3 - Registrar Geração Diária");
                System.out.println("4 - Ver Relatórios");
                System.out.println("5 - Verificar Alertas de Falha");
                System.out.println("6 - Sair");
                System.out.print("Escolha: ");
                
                try {
                    opcao = leitor.nextInt();
                } catch (Exception e) {
                    opcao = 0; // Força cair no erro padrão
                }
                leitor.nextLine(); // Limpa buffer

                switch (opcao) {
                    case 1:
                        // REGRA DE NEGÓCIO: Visualizador não pode instalar equipamento
                        if(usuarioLogado.getNivelAcesso().equals("VISUALIZADOR")) {
                            System.out.println("❌ Acesso negado. Apenas Administradores podem instalar equipamentos.");
                            break;
                        }
                        
                        System.out.print("Tipo (1-Inversor, 2-String): ");
                        int tipo = leitor.nextInt(); leitor.nextLine();
                        System.out.print("ID do Equipamento: ");
                        String id = leitor.nextLine();
                        if(tipo == 1) usina.adicionarEquipamento(new Inversor(id, "Fronius", 50.0));
                        else usina.adicionarEquipamento(new StringSolar(id, "Canadian", 15.0, 20));
                        System.out.println("✅ Equipamento instalado.");
                        break;

                    case 2:
                        System.out.print("Deseja realizar a comparação de eficiência climática? (S/N): ");
                        String fazerComp = leitor.nextLine().toUpperCase();
                        
                        if (fazerComp.equals("S")) {
                            SensorIrradiacao irr = new SensorIrradiacao("S-IRR-01");
                            SensorTemperatura temp = new SensorTemperatura("S-TEMP-01");

                            System.out.print("Qual a Irradiação de Referência do Local (STC em W/m²)? ");
                            double ref = leitor.nextDouble();
                            usina.setIrradiacaoReferencia(ref);

                            System.out.print("Qual a Irradiação Atual lida pelo sensor (W/m²)? ");
                            irr.simularAmbiente(leitor.nextDouble());
                            
                            System.out.print("Qual a Temperatura Atual dos módulos (°C)? ");
                            temp.simularAmbiente(leitor.nextDouble());
                            leitor.nextLine(); // Limpar buffer numérico

                            usina.analisarClima(irr);
                            usina.analisarTemperatura(temp);
                        } else {
                            System.out.println("Análise de eficiência cancelada pelo usuário.");
                        }
                        break;

                    case 3:
                        System.out.println("Funcionalidade de registro em construção...");
                        break;

                    case 4:
                        usina.listarEquipamentos();
                        break;

                    case 5:
                        usina.registrarEvento(new FalhaEquipamento("Hoje", "Teste de Sistema", "N/A"));
                        break;

                    case 6:
                        System.out.println("Encerrando o SCADA...");
                        break;

                    default:
                        System.out.println("Opção inválida.");
                        break;
                }
            }
        } else {
            System.out.println("❌ Credenciais inválidas. O sistema será encerrado.");
        }
        leitor.close();
    }
}