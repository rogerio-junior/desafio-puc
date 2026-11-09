# Desafio PUC Rio (Folha de Pagamento)

Os requisitos do desafio podem ser encontrados [aqui](./.enunciado-desafio/desafio-tecnico-estendido.md).

> 📌 **Todos os comandos** devem ser executados a partir da raiz do repositório

## Índice

* [🚀 Como executar rápido](#-como-executar-rápido)
* [🌱 Populando o BD com dados de teste](#-populando-o-bd-com-dados-de-teste-opcional)
* [🐛 Como compilar e depurar](#-como-compilar-e-depurar)
* [🫀 Anatomia da API](#-anatomia-da-api)
* [⚖️ Decisões](#️-decisões)

## 🚀 Como executar rápido

1. Tenha o Docker instalado.
2. Execute o comando `docker compose up`
3. O site fica disponível em `http://localhost:5173/` e a API em `http://localhost:8080/`
4. Se precisar de dados de teste, veja o tópico seguinte.

## 🌱 Populando o BD com dados de teste (Opcional)

O arquivo [db-seed.sql](/db-seed.sql) popula o banco com dados de teste. Você pode executar direto no banco ou, se preferir, com o comando abaixo:

```bash
docker compose exec -T db psql -U payment -d puc_challenge -v ON_ERROR_STOP=1 < db-seed.sql
```

## 🐛 Como compilar e depurar

Passo a passo para executar, compilar e buildar tudo na própria máquina.

### 1. Pré-requisitos

* Docker - Para subir o banco de dados local
* Java SDK 25 LTS - Para compilar, depurar e executar o backend
* Node.js 24 LTS - Para compilar e depurar o frontend

### 2. Banco de dados

1. Inicie o banco de dados:
    ```bash
    docker compose up db
    ```
2. Informações para se conectar ao banco (eu usei o DBeaver):
    * URL: `jdbc:postgresql://localhost:5432/postgres`
    * Usuário: `payment`
    * Senha: `P@yment2022`
3. Todos os dados serão mapeados na pasta [./data/*](./data) (ignorada pelo Git). Caso queira resetar o banco, basta apagar essa pasta e reiniciar o container.

### 3. Compilar e executar o backend

1. Gere o *.jar* com o comando:
    ```bash
    ./backend/mvnw -f backend/pom.xml clean package
    ```
2. Execute o *.jar* com o comando (o servidor será iniciado em `http://localhost:8080`):
    ```bash
    java -jar backend/target/puc-challenge-v1.jar
    ```
3. A aplicação é quem gerencia a estrutura do banco de dados. Ela executa as migrações e cria todas as tabelas automaticamente com o **Flyway**.

> Se preferir, pode executar ou depurar o backend através da sua IDE favorita, sabendo que é um projeto Maven

### 4. Compilar e executar o frontend

1. Instale as dependências, compile e gere o HTML com o comando:
    ```bash
    npm --prefix frontend ci && npm --prefix frontend run build
    ```
2. Sirva o site através de um servidor com o comando:
    ```bash
    npm --prefix frontend run preview
    ```

> O site precisa ser servido por um servidor. Abrir o [/frontend/dist/index.html](./frontend/dist/index.html) diretamente pelo navegador não funciona, pois o navegador bloqueia scripts do tipo módulo.

## 🫀 Anatomia da API

| Método | Rota | Parâmetros | Descrição |
|---|---|---|---|
| `GET` | `/employees` | Query: `active` (padrão `true`), `cep` (opcional, com máscara `00000-000`), `limit` (padrão 25, máx. 500), `offset` (padrão 0) | Lista paginada dos funcionários ativos (ou inativos, com `active=false`), com filtro opcional por CEP |
| `GET` | `/employees/{employeeId}/payments` | Path: `employeeId` (CPF, com ou sem máscara)<br>Query: `month` (`YYYY-MM`, opcional), `limit` (padrão 25, máx. 500), `offset` (padrão 0) | Histórico de pagamentos do funcionário, com filtro opcional por competência |
| `POST` | `/payments/calculate`<br>(RPC) | Body: `{ "referenceDate": "YYYY-MM-DD" }` (data passada ou presente) | Calcula os pagamentos da competência da data informada. 📌 **Usei RPC propositalmente** para representar o processo em lote, um padrão conhecido na literatura (controller resource). |
| `GET` | `/payments` | Header: `Accept: application/json`<br>Query: `month` (`YYYY-MM`), `limit` (padrão 25, máx. 500), `offset` (padrão 0) | Lista paginada dos pagamentos da competência |
| `GET` | `/payments` | Header: `Accept: text/csv`<br>Query: `month` (`YYYY-MM`) | Exporta em CSV todos os pagamentos da competência |

## ⚖️ Decisões

### Employee → Payee 

O desafio propõe a classe abstrata [Employee.java](./.enunciado-desafio/Employee.java), com uma especialização para cada tipo de funcionário, na qual cada uma faz o próprio cálculo do salário.

Mantive essa herança, mas renomeei a classe abstrata para [Payee.java](./backend/src/main/java/br/puc_rio/tecgraf/challenge/domain/payment/Payee.java), movendo-a para o domínio de pagamentos, onde ficam também as implementações. A classe `Employee` continua existindo no domínio de funcionários, mas apenas com os dados cadastrais (sem método de cálculo).

**Motivo**: `Payee` é a visão de `Employee` usada pela folha de pagamento. Cadastro e cálculo têm necessidades diferentes, e separá-los traz os seguintes benefícios:

* **Cada domínio conhece apenas os dados de que precisa**: `Payee` contém os campos usados no cálculo, inclusive `weekendDaysWorked`, que não faz sentido no cadastro de funcionário. Assim, é possível cadastrar um funcionário sem informar dados de frequência. Da mesma forma, `Payee` não carrega dados irrelevantes para o cálculo, como o endereço.
* **Desempenho**: a query que carrega os `Payee` já traz `weekendDaysWorked` agregado pelo banco, o que evita buscar todos os registros de `attendance` para processá-los no Java. Outros dados necessários ao cálculo podem ser fornecidos da mesma maneira.
* **Evolução independente**: `Employee` pode ganhar novas funcionalidades de cadastro sem afetar o cálculo, o que evita uma *God Class*. O domínio de pagamentos, por sua vez, não se preocupa com o cadastro de funcionários, apenas lê os funcionários existentes e usa o que precisa.
* **Regras reutilizáveis**: cada bônus é uma implementação de [SalaryRule](./backend/src/main/java/br/puc_rio/tecgraf/challenge/domain/payment/rules/SalaryRule.java), e cada tipo de `Payee` compõe as regras que se aplicam a ele. Se o percentual do bônus de idosos mudar, por exemplo, a alteração em um único lugar vale para todos os tipos que o recebem.

Mantive o método `computeSalary` na classe por ser a proposta do desafio. Em geral, eu separaria os dados do comportamento, com uma classe para os dados do funcionário e um *handler* para calcular o salário de cada tipo, deixando a classe extensível e evitando que acumule papeis à medida que novas funcionalidades são adicionadas.

### Negócio

* Assumi que a idade do funcionário é referente ao último dia do mês de competência calculado.
* Nos bônus com percentual, apliquei arredondamento para cima nas dízimas com mais de 2 dígitos.
* O aniversáriante do mês é avaliado pelo mês. Quem nasceu em 29/02 faz aniversário em fevereiro mesmo em ano não bissexto.
* Funcionário que já tem pagamento no mês não é recalculado nem sobrescrito.
* Funcionários não binários não ganham bônus por idade, mesmo com mais de 60 anos.

### Stack

* **Spring Boot**: Escolha natural para backend. Criei o projeto pelo `start.spring.io`.
* **Flyway**: Permite manter o banco de dados atualizado automaticamente, sendo útil também para criar containers de banco de dados efêmeros dentro da suíte de testes do Java.
* **Mapstruct**: Para converter objetos de forma mais fácil, visto que optei por deixar o domínio agnóstico.
* **React**: Foi escolhido pela flexibilidade de criação de componentes e gestão do estado das telas. Usei o **Vite** para suportar o desenvolvimento com Hot Reload e build simplificado para produzir um HTML standalone (SPA) sem precisar de renderização no servidor (SSR), devido à natureza simples do projeto.

### Técnico

* **Testes unitários**: Optei por fazer apenas os testes unitários da funcionalidade principal de calculo.
