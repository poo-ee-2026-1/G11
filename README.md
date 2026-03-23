# G11

Participantes: André Tavares, Juliane Gregório, Pedro Lourenço.

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

Inicialmente o sistema funcionará no terminal (menu interativo).

# 🖥️ Exemplo de funcionamento esperado

=== SISTEMA DE MONITORAMENTO FOTOVOLTAICO ===

1 - Cadastrar inversor

2 - Cadastrar string

3 - Registrar geração

4 - Exibir geração total

5 - Verificar falhas

6 - Sair


# Cronograma semanal - Sistema de Monitoramento de Usina Fotovoltaica
Resumo:
Semana 1: planejamento e modelagem
Semana 2: criação das classes
Semana 3: estrutura da usina
Semana 4: cadastro de equipamentos
Semana 5: menu interativo
Semana 6: registro de geração
Semana 7: cálculo da geração
Semana 8: detecção de falhas
Semana 9: testes e ajustes
Semana 10: finalização e apresentação

# Semana 1 (23/03 a 29/03) - Levantamento e modelagem do projeto
Objetivo: entender o problema e desenhar a estrutura orientada a objetos.
Atividades:
- definir o escopo final do sistema
- listar atributos e métodos de cada classe
- modelar as classes:
Equipamento
Inversor
StringSolar
UsinaFotovoltaica
- definir como será o relacionamento entre elas
- planejar o fluxo do menu interativo
Entregas:
- diagrama simples das classes
- lista de funcionalidades
- estrutura inicial dos arquivos do projeto

# Semana 2 (30/03 a 05/04) - Implementação das classes base
Objetivo: criar a base do sistema.
Atividades:
- implementar a classe Equipamento
- implementar a classe Inversor
- implementar a classe StringSolar
- definir construtores, atributos e métodos básicos
- criar métodos para exibir informações dos equipamentos
Entregas:
- classes criadas e funcionando
- testes simples de instanciação no terminal

# Semana 3 (06/04 a 12/04) - Estrutura da usina
Objetivo: permitir que a usina gerencie os componentes cadastrados.
Atividades:
- implementar a classe UsinaFotovoltaica
- adicionar listas para armazenar inversores e strings
- estruturar o relacionamento entre os objetos
Entregas:
- classe usina funcional
- estrutura de armazenamento funcionando

# Semana 4 (13/04 a 19/04) - Cadastro de equipamentos
Objetivo: permitir cadastro via sistema.
Atividades:
- criar função de cadastro de inversores
- criar função de cadastro de strings solares
- validar entradas básicas do usuário
Entregas:
- sistema já cadastra equipamentos pelo menu
- associação de equipamentos dentro da usina funcionando

# Semana 5 (20/04 a 26/04) - Menu interativo
Objetivo: implementar a interface do sistema.
Atividades:
- desenvolver menu no terminal
- implementar navegação entre opções
- organizar fluxo do sistema
Entregas:
- menu funcional
- interação básica com o usuário

# Semana 6 (27/04 a 03/05) - Registro de geração
Objetivo: registrar dados de produção.
Atividades:
- criar funcionalidade para registrar geração de energia
- armazenar geração por inversor ou string
- validar entradas de dados
Entregas:
- registro de geração funcionando

# Semana 7 (04/05 a 10/05) - Cálculo de geração total
Objetivo: consolidar dados de produção.
Atividades:
- implementar cálculo da geração total da usina
- exibir a geração total no terminal
- organizar melhor a saída das informações
Entregas:
- cálculo da geração total funcionando corretamente

# Semana 8 (11/05 a 17/05) - Verificação de falhas
Objetivo: adicionar inteligência ao sistema.
Atividades:
- definir critérios de falha, por exemplo:
- equipamento sem geração
- geração abaixo de um valor mínimo
- equipamento inativo
- criar método de verificação de falhas
- exibir mensagens de alerta no menu
Entregas:
- sistema detecta falhas básicas
- relatório simples de falhas no terminal

# Semana 9 (18/05 a 24/05) - Testes e ajustes
Objetivo: garantir estabilidade do sistema.
Atividades:
- testar cenários normais e com falhas
- corrigir erros encontrados
- melhorar organização do código
Entregas:
sistema estável e funcional

# Semana 10 (25/05 a 01/06) - Integração final, testes e apresentação
Objetivo: finalizar o projeto e preparar entrega/apresentação.
Atividades:
- revisar código
- melhorar organização e legibilidade
- testar todas as opções do menu:
- cadastrar inversor
- cadastrar string
- registrar geração
- exibir geração total
- verificar falhas
- sair
- preparar explicação das classes e funcionamento do sistema
Entregas:
- projeto final funcional
- código revisado
- material pronto para apresentação em 01/06/2026
validação de entrada
exibição detalhada dos equipamentos
