#!/bin/bash
set -e #Arrête le script si erreur

#Check before
if [ ! -f .env ]; then
  echo "Fichier .env introuvable. Abandon du script."
  exit 1
fi
if [ ! -d back ]; then
  echo "Dossier 'back/' manquant."
  exit 1
fi

if [ ! -f front/Dockerfile ]; then
  echo "Dockerfile de 'front/' introuvable."
  exit 1
fi

#Chargement des variable d'environnement
set -a
source .env
set +a

echo "### -- Pulling project from repo ..."
git pull


echo "### -- Building back project docker image ..."
cd back/
docker build -t emballmois-backend:$EMBALLMOIS_BACK_VERSION .
cd ..

echo "### -- Building front project docker image ..."
docker build -f front/Dockerfile -t emballmois-frontend:$EMBALLMOIS_FRONT_VERSION .

echo "### -- Running Docker containers..."
docker-compose up -d
