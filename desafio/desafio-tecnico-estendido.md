## 1. Descrição do problema

Suponha que uma empresa tem na sua folha de pagamentos funcionários de quatro categorias: Contratados por tempo indeterminado, contratados a prazo, estagiários e bolsistas.

A empresa consta com um servidor de banco de dados para registrar as informações relacionadas aos seus funcionários:

 - A tabela "employee" armazena todos os funcionários da empresa e ela apresenta a estrutura (de colunas) abaixo:

```
    id_social_number - Number(11,0) - PK // Número de CPF da pessoa física
    first_name - varchar2(2000)  // Nome
    last_name - varchar2(2000)  // Sobrenome
    birth_date - date //Data de nascimento
    gender - varchar(1) //Gênero com o qual a pessoa se indentifica (M- Masculino, F- Feminino, N- Não binário)
    ative - Number(1,0) // Indica se a pessoa está em atividade ou não na empresa
    base_salary - number(6,2) // Informa o valor do salário base a receber
    address - varchar2(4000)// Registra o endereço completo da pessoa física
    type - varchar2(10)// Determina o tipo atual do empregado: A -> Contratado por tempo indeterminado,
                                                        B -> contratado a prazo, C -> Estagiario, D -> Bolsista
```

 - A tabela "payment" registra o valor pago para o funcionário no respectivo mês. Ela apresenta a estrutura abaixo:

```
    id_employee - Number(11,0) PK - FK to employee(id_social_number)
    month - Number(2,0) PK
    year - Number (4,0) PK
    payment - number(6,2)
```

 - A tabela "attendance" registra o comparecimento (primeira entrada) do funcionário à empresa, mediante a estrutura seguinte:

```
   id_employee - Number(11,0) - PK - FK to employee(id_social_number)
   entrance - timestamp  - PK
```

  As informações referentes à base de dados são:
```
   host: localhost
   username: payment
   password: P@yment2022
```
   Atenção! Você pode usar os valores definidos por padrão para o resto dos parâmetros de conexão necessários, atendendo ao SGBD escolhido.


---

## 2. Implementação das classes Employee e Gender

Usando os arquivos Employee.java e Gender.java, implemente um projeto Java cumprindo os seguintes pontos:

1) Realize as solicitações listadas no To-Do dos arquivos Employee.java e Gender.java. Caso seja preciso, você pode realizar edições nesses arquivos não detalhadas no To-Do.

De modo que:
 Para computar os valores dos pagamentos dos salários para todos os funcionários ativos referente ao mês em curso e armazená-los na tabela payment
   - Mês em curso: Se a data de referência é no mês de abril, então o salário a computar é do mês de março (de 01/03 até 31/03).
   - Se o funcionário é contratado por tempo indeterminado e faz aniversário no mês em curso ele recebe uma bonificação de 200 reais
   - Para o funcionário é contratado por tempo indeterminado ou por tempo determinado e tem mais de 65 anos se é homem ou mais de 60 anos se é mulher, então ele recebe um acréscimo de 3% do salário base.
   - Se o funcionário é bolsista ou estagiário, ele receberá 40 reais a mais por cada sábado ou domingo que comparecer na empresa.
   *Outros tipos de benefícios, como férias e 13o salários, não são considerados neste desafio.

2) Dada a informação de identidade de um funcionário (CPF), implementar a recuperação de todos os pagamentos recebidos por ele:
   mês/ano -> valor recebido

3) Implementar a funcionalidade de exportar para um arquivo csv os pagamentos recebidos de todos os funcionários em determinado mês/ano com a estrutura de cabeçalho seguinte:
    "CPF", "Nome", "Sobrenome", "Mês", "Ano", "Salário a receber"

4) Observe-se que o modelo de dados da base de dados apresenta uma dificuldade para recuperar os CEPs dos funcionários, dada esta situação, implemente a recuperação da lista de CEPs dos funcionários ativos, sabendo que o CEP cumpre com o padrão de formação seguinte: <cinco números>-<três números>.


---

## 3. API REST

Projetar e implementar uma API REST que exponha as funcionalidades da seção anterior e permita consultas flexíveis dos dados de pagamento e de funcionários. A escolha do framework, linguagem e estilo arquitetural fica a critério do candidato.

A API deve:
   - Seguir os princípios REST (recursos nomeados por substantivos, verbos HTTP semânticos, códigos de status adequados).
   - Retornar respostas em JSON, com estrutura consistente entre os endpoints.
   - Tratar erros de forma clara (ex.: funcionário não encontrado, mês/ano inválidos).
   - Ser stateless.

Os endpoints devem cobrir as funcionalidades úteis para o usuário final, sem gerar rotas redundantes ou que exponham informações supérfluas. O candidato deve se colocar no lugar do cliente do sistema e definir a API que ele próprio gostaria de consumir, justificando as escolhas no README.


---

## 4. Interface web

Implementar uma interface web que consuma a API da seção anterior e permita consultar e operar os dados de pagamento. A escolha do framework, biblioteca de componentes e estratégia de estado fica a critério do candidato.

A interface deve:
   - Funcionar em um navegador moderno sem necessidade de instalação adicional pelo usuário.
   - Comunicar-se exclusivamente com a API via HTTP (sem acesso direto ao banco).
   - Apresentar feedback claro para ações em andamento e erros de requisição.

O candidato deve se colocar no lugar do cliente do sistema e projetar a interface que ele próprio gostaria de usar, priorizando clareza e utilidade. As funcionalidades expostas devem ser as que fazem sentido para o usuário final.


---

## 5. Indicações finais

  Para a implementação:
   - Escolha o servidor de banco de dados de sua preferência para a implementação.
   - Para as seções 3 e 4, a escolha de tecnologias fica a critério do candidato, priorizando flexibilidade de design. As decisões tomadas devem ser justificadas no README do projeto.

  Para a entrega:
   - O projeto da seção 2 não será executado, porém não pode conter erros de compilação.
   - Os projetos das seções 3 e 4 devem ser executáveis localmente seguindo as instruções do README.
   - Não enviar as pastas "target" (Java) e "node_modules" (frontend).

Dicas:
   Para realizar testes de seu código, particularmente a parte relacionada a interagir com o banco de dados, você pode usar Docker's containers de SGBDs disponíveis no Docker Hub (docker.io).
   Para as seções 3 e 4, um docker-compose.yml unificado cobrindo banco, API e frontend facilita a execução e é bem visto na avaliação.
