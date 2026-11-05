# Desafio PUC Rio (Folha de Pagamento)

Os requisitos do desafio podem ser encontrados [aqui](./desafio/desafio-tecnico-estendido.md).

> 📌 **Todos os comandos** devem ser executados a partir da raiz do repositório

## Índice

* [🚀 Como executar](#-como-executar)
* [⚖️ Decisões](#️-decisões)

## 🚀 Como executar

### 1. Pré-requisitos

* Docker - Para subir o banco de dados local
* Java SDK 25 LTS - Para compilar, depurar e executar o backend
* Node.js 24 LTS - Para compilar e depurar o frontend

### 2. Banco de dados

1. Inicie o banco de dados: `docker compose up`
2. Informações para se conectar ao banco (eu usei o DBeaver):
    * URL: `jdbc:postgresql://localhost:5432/postgres`
    * Usuário: `payment`
    * Senha: `P@yment2022`
3. Todos os dados serão mapeados na pasta [./data/*](./data) (ignorada pelo Git). Caso queira resetar o banco, basta apagar essa pasta reiniciar o container.

### 3. Compilar e executar o backend

1. Gere o *.jar* com o comando: `./backend/mvnw -f backend/pom.xml clean package`
2. Execute o *.jar* com o comando: `java -jar backend/target/puc-challenge-v1.jar`. O servidor é iniciado em `http://localhost:8080`.
3. A aplicação é quem gerencia a estrutura do banco de dados. Ela executa as migrações e cria todas as tabelas automaticamente com o **Flyway**.

> Se preferir, pode executar ou depurar o backend através da sua IDE favorita, sabendo que é um projeto Maven

### 4. Alimentar banco de dados (Opcional)

O arquivo [db-seed.sql](/db-seed.sql) pode ser executado para popular a tabela com dados de teste
* TODO: Melhorar seed, especificar cenários

### 5. Compilar e executar o frontend

1. Instale as dependências, compile e gere o HTML com o comando: `npm --prefix frontend ci && npm --prefix frontend run build`
2. Sirva o site através de um servidor com o comando: `npm --prefix frontend run preview`. Isso é necessário porque se abrir o [/frontend/dist/index.html](./frontend/dist/index.html) direto no navegador, o navegador bloqueia os scripts do tipo módulo.

## ⚖️ Decisões

### Spring Boot

Escolha natural para backend. Criei o projeto pelo `start.spring.io`.

### React

Foi escolhido pela flexibilidade de criação de componentes e gestão do estado das telas. Usei o **Vite** para suportar o desenvolvimento com Hot Reload e build simplificado para produzir um HTML standalone (SPA) sem precisa de um servidor, devo à natureza simples do projeto.

### Flyway

Permite manter o banco de dados atualizado automaticamente, sendo útil também para criar containers de banco de dados efêmeros dentro da suite de testes do Java.
