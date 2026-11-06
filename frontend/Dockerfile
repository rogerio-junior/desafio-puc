# Etapa 1: gera os arquivos estáticos (dist/)
FROM node:24-alpine AS build
WORKDIR /app

# Copia primeiro só os manifestos para aproveitar o cache de camadas no npm ci
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# Etapa 2: nginx serve o dist/ e faz o proxy de /api para a API (mesmo papel do proxy do Vite).
# O endereço da API vem da variável API_URL (ver docker-compose.yaml)
FROM nginx:1.29-alpine
COPY nginx.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
