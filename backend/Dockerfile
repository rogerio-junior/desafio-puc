# Etapa 1: compila a aplicação com o Maven Wrapper (só precisa do JDK)
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copia primeiro só o necessário para baixar as dependências, aproveitando o cache de camadas
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q dependency:go-offline

COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q package -DskipTests \
    && cp target/*.jar app.jar

# Etapa 2: executa apenas o .jar, com uma imagem menor (só o JRE)
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
