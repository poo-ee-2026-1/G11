# G11

Participantes: André Tavares, Juliane Gregório.

# ☀️ Sistema de Monitoramento de Usina Fotovoltaica

Sistema de monitoramento que consiga representar equipamentos como inversores, strings e módulos, registrar dados de operação e calcular indicadores como geração total, eficiência de produção e detecção de falhas

O sistema simula um ambiente de supervisão onde é possível acompanhar a operação de componentes como:
- inversores
- strings
- módulos fotovoltaicos

A partir dessas informações, o sistema poderá calcular indicadores básicos de desempenho e identificar possíveis falhas.

# ⚡ Objetivo do Projeto

Por meio da POO, criar um sistema que permita:
- cadastrar equipamentos da usina
- registrar geração de energia
- calcular a geração total da usina
- identificar possíveis falhas nos equipamentos
- apresentar informações básicas de operação

# 🧱 Estrutura inicial do sistema

O sistema será organizado em classes que representam os elementos da usina:
- Equipamento
- Inversor
- StringSolar
- UsinaFotovoltaica

Cada classe representará um componente do sistema e suas respectivas características.

# ⚙️ Funcionalidades previstas

O sistema deverá permitir:

- cadastro de inversores
- cadastro de strings solares
- registro de geração de energia
- cálculo da geração total da usina
- verificação de possíveis falhas
- passo a passo para solução das falhas
- gráfico de geração de cada usina
- geração anual e comparação de desempenho ao longo dos meses
- consumo instantâneo

# Cronograma semanal

- Semana 1: planejamento e modelagem

- Semana 2: criação das classes

- Semana 3: estrutura da usina

- Semana 4: cadastro de equipamentos

- Semana 5: menu interativo

- Semana 6: registro de geração

- Semana 7: cálculo da geração

- Semana 8: detecção de falhas

- Semana 9: testes e ajustes

- Semana 10: finalização e apresentação

# Resumo dos conceitos aplicados até o momento:

- Classes Principais: UsinaFotovoltaica, Equipamento, Inversor, StringSolar, ModuloFotovoltaico, MedidorBidirecional, Usuario, Administrador, Cliente, Operador, Evento, FalhaEquipamento, Sensor, SensorIrradiacao, SensorTemperatura e MenuInterativo.

- Objetos: new UsinaFotovoltaica(nomeUsina, local), new Administrador(nomeUser, loginUser, senhaUser), new Cliente(nomeUser, loginUser, senhaUser), new Operador(nome, login, senha), new Inversor(id, "Fronius", 50.0), new StringSolar(id, "Canadian", 15.0, 20), new ModuloFotovoltaico(id, marca, potencia), new MedidorBidirecional(id, marca), new SensorIrradiacao("S-IRR-01"), new SensorTemperatura("S-TEMP-01") e new FalhaEquipamento("Hoje", "Teste de Sistema", "N/A").

- Encapsulamento e Modificadores de Acesso
private: idEquipamentoFalho em FalhaEquipamento; quantidadeModulos em StringSolar; nome, localidade, irradiacaoReferencia, equipamentos, logEventos em UsinaFotovoltaica; dataHora e descricao em Evento.
protected: valorAtual na classe Sensor.
public: getId(), getMarca(), getPotencia(), isAtivo(), setStatus(), exibirDetalhes() em Equipamento (e derivadas); getDescricao(), registrarLog() em Evento (e derivadas); getNome(), getNivelAcesso(), autenticar(), exibirMenu() em Usuario (e derivadas); simularAmbiente(), lerDados() em Sensor (e derivadas); setIrradiacaoReferencia(), adicionarEquipamento(), registrarEvento(), listarEquipamentos(), analisarClima(), analisarTemperatura() em UsinaFotovoltaica; e o método main() em MenuInterativo.
final: id, marca e potencia em Equipamento; id em Sensor; nome, login, senha e nivelAcesso em Usuario.

- Abstração
Classes Abstratas: Equipamento, Evento, Sensor e Usuario.
Métodos Abstratos: exibirDetalhes() em Equipamento, registrarLog() em Evento, lerDados() em Sensor e exibirMenu() em Usuario.

- Herança
Administrador, Cliente e Operador herdam de Usuario. Inversor, StringSolar, ModuloFotovoltaico e MedidorBidirecional herdam de Equipamento. FalhaEquipamento herda de Evento. SensorIrradiacao e SensorTemperatura herdam de Sensor.

- Polimorfismo
O método exibirDetalhes() da classe abstrata Equipamento é reescrito (@Override) e age de forma diferente para Inversor, StringSolar, ModuloFotovoltaico e MedidorBidirecional. O método exibirMenu() da classe abstrata Usuario é reescrito para responder diferente dependendo se é Administrador, Cliente ou Operador. O método registrarLog() da classe abstrata Evento tem um comportamento específico dentro de FalhaEquipamento. O método lerDados() da classe abstrata Sensor é reescrito dentro de SensorIrradiacao e SensorTemperatura.

- Composição e Agregação
Composição: A classe UsinaFotovoltaica possui coleções (ArrayList<Equipamento> e ArrayList<Evento>) que gerenciam o ciclo de vida e armazenam os componentes do sistema. MenuInterativo possui internamente a ArrayList<Usuario> bancoDeUsuarios.
Agregação: A classe StringSolar agrega uma quantidade de módulos (quantidadeModulos).

- Associação e Atribuições
Associação: A UsinaFotovoltaica usa objetos do tipo SensorIrradiacao e SensorTemperatura de forma transitória nos métodos analisarClima(SensorIrradiacao sensorIrradiacao) e analisarTemperatura(SensorTemperatura sensorTemp), recebendo-os como parâmetros para realizar cálculos de eficiência e acionar alertas.
Atribuição: A lógica de estado (statusAtivo) modificada via setters; a definição do valor na simulação de sensores (this.valorAtual = valor); a atribuição dos dados passados no construtor de classes com variáveis protegidas (this.id = id, this.nome = nome); e as atribuições para controle de simulação (ex: irr.simularAmbiente(leitor.nextDouble()) e temp.simularAmbiente(leitor.nextDouble()) no MenuInterativo).
