# desafio-puc
Desafio de entrevista PUC

* SDK Man para gerenciar os pacotes Java
* Java 25 LTS
* Spring Boot 4.1.2
* Criei o projeto pelo start.spring.io
* Desenvolvi tudo pelo VS Code, por ser um projeto simples, usando a extesão https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack
* Usar docker para subir o banco de dados `docker compose up`
    - Os dados serão montados na pasta .data (gitignorada)
    - Para conectar ao banco por um SGBD:
        - url: `jdbc:postgresql://localhost:5432/puc_challenge`
        - usuário: `payment`
        - senha: `P@yment2022`