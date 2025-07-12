# Étape 1 : build de l'app Vue
FROM node:20-alpine AS build
WORKDIR /app

# copier les fichiers de dépendances
COPY ./front/package*.json ./
RUN npm install

# copier le reste (src, public, config, etc.)
COPY ./front/ .
RUN npm run build

# Étape 2 : servir les fichiers avec Nginx
FROM nginx:alpine

# Copier les fichiers statiques générés par Vite
COPY --from=build /app/dist /usr/share/nginx/html

# Ajouter config de Nginx
COPY ./front/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
