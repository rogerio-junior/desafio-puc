# Desafio PUC Rio (Folha de Pagamento)

Os requisitos do desafio podem ser encontrados [aqui](./desafio/desafio-tecnico-estendido.md).

> 📌 **Todos os comandos** devem ser executados a partir da raiz do repositório

## Índice

* [🚀 Como executar](#-como-executar)
* [🫀 Anatomia da API](#-anatomia-da-api)
* [⚖️ Decisões](#️-decisões)

## 🚀 Como executar

### 1. Pré-requisitos

* Docker - Para subir o banco de dados local
* Java SDK 25 LTS - Para compilar, depurar e executar o backend
* Node.js 24 LTS - Para compilar e depurar o frontend

### 2. Banco de dados

1. Inicie o banco de dados:
    ```bash
    docker compose up
    ```
2. Informações para se conectar ao banco (eu usei o DBeaver):
    * URL: `jdbc:postgresql://localhost:5432/postgres`
    * Usuário: `payment`
    * Senha: `P@yment2022`
3. Todos os dados serão mapeados na pasta [./data/*](./data) (ignorada pelo Git). Caso queira resetar o banco, basta apagar essa pasta reiniciar o container.

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

### 4. Alimentar banco de dados (Opcional)

O arquivo [db-seed.sql](/db-seed.sql) popula o banco com dados de teste. Você pode executar direto no banco ou, se preferir, com o comando abaixo:

```bash
docker compose exec -T db psql -U payment -d puc_challenge -v ON_ERROR_STOP=1 < db-seed.sql
```

### 5. Compilar e executar o frontend

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
| `GET` | `/employees` | Query: `cep` (opcional, com máscara `00000-000`), `limit` (padrão 25, máx. 500), `offset` (padrão 0) | Lista paginada dos funcionários ativos, com filtro opcional por CEP |
| `GET` | `/employees/{employeeId}/payments` | Path: `employeeId` (CPF, com ou sem máscara)<br>Query: `month` (`YYYY-MM`, opcional), `limit` (padrão 25, máx. 500), `offset` (padrão 0) | Histórico de pagamentos do funcionário, com filtro opcional por competência |
| `POST` | `/payments/calculate`<br>(RPC) | Body: `{ "referenceDate": "YYYY-MM-DD" }` (data passada ou presente) | Calcula os pagamentos da competência da data informada. 📌 **Usei RPC propositalmente** para representar o processo em lote, um padrão conhecido na literatura (controller resource). |
| `GET` | `/payments` | Header: `Accept: application/json`<br>Query: `month` (`YYYY-MM`), `limit` (padrão 25, máx. 500), `offset` (padrão 0) | Lista paginada dos pagamentos da competência |
| `GET` | `/payments` | Header: `Accept: text/csv`<br>Query: `month` (`YYYY-MM`) | Exporta em CSV todos os pagamentos da competência |

## ⚖️ Decisões

### Stack

* **Spring Boot**: Escolha natural para backend. Criei o projeto pelo `start.spring.io`.
* **Flyway**: Permite manter o banco de dados atualizado automaticamente, sendo útil também para criar containers de banco de dados efêmeros dentro da suite de testes do Java.
* **Mapstruct**: Para converter objetos de forma mais fácil, visto que optei por deixar o domínio agnóstico.
* **React**: Foi escolhido pela flexibilidade de criação de componentes e gestão do estado das telas. Usei o **Vite** para suportar o desenvolvimento com Hot Reload e build simplificado para produzir um HTML standalone (SPA) sem precisa de um servidor, devo à natureza simples do projeto.

### Negócio

* Assumi que a idade do funcionário é referente ao último dia da competência
* Bonus com percentual apliquei arredondamento para cima nas dízimas com mais de 2 dígitos
* O aniversário é avaliado pelo mês. Quem nasceu em 29/02 faz aniversário em fevereiro mesmo em ano não bissexto.

* Funcionário que já tem pagamento no mês de competência não é recalculado nem sobrescrito.

### Espec

* Usei BigDecimal ao invés de Double
* Problema do CEP, usei um índice como função