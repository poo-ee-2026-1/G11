# Classes
As classes serão divididas entre os equipamentos disponíveis em uma usina foto voltaíca. 
Algumas classes iniciais que iremos usar: Inversor (converte corrente contínua em corrente alternada), StringSolar (série de painéis solares conectados), 
Usina (conglomerado de inversores e Strings).

# Objeto
Os objetos neste projeto podem ser as diferentes marcas e especificações de cada Inversor e StringSolar.
Por exemplo: 
Inversor - Tipo (offgrid, ongrid, hibrido), função(conversão, monitoramento, proteção), marca, potêncial nominal.

# Herança
A herança pode ocorrer devido ao tipo do equipamento, por exemplo, todos os Inversores irão herdar os atributos Tipo, Função, Marca e Potência nominal. Aqueles que forem
do da função Proteção irão herdar o atributo Grau de Proteção.

# Abstração
Usaremos de classes abstratas principalmente para calcular a geração total das usinas, usando um método universal de calculo, que dependerá somente dos atributos dos objetos cadastrados
na usina.

# Encapsulamento
O encapsulamento irá restringir o acesso aos dados dos objetos para que os mesmos não sofram modificações durante a operação da Usina.
