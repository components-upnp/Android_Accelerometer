# Android_Accelerometer
Composant UPnP se servant de l'accéléromètre d'un terminal Android

<strong>Description:</strong>

Ce composant envoie la direction du mouvement initié sur le terminal Android, ici GAUCHE, DROITE, BAS et HAUT, direction calculée
grâce à l'accélération linéaire.

On peut utiliser ce composant afin de faire défiler des slides avec des mouvements sur la gauche et la droite, ouvrir ou fermer 
un volet connecté en UPnP.

<strong>Installation:</strong>
Connecter le terminal Android à la machine, accepter le deboggage USB puis lancer l'application depuis l'IDE Adnroid Studio, 
en choisissant le terminal préalablement connecté.

<strong>Spécification UPnP:</strong>

Le composant Android Accelerometer offre le service AccelerometerService dont la dexcription est la suivante:

  1) GetDirection(): retourne la direction courante.
  2) SetDirection(string newDirectionValue): permet de définir la direction.
  
Ce composant envoie un événement de type DirectionEvent lorsque la direction change.

Voici le schéma représentant le composant:

![alt tag](https://github.com/components-upnp/Android_Accelerometer/blob/master/Accelerometer.png)

<strong>Maintenance:</strong>

Ce projet Android est un projet gradle, des dépendances peuvent être ajoutées dans build.gradle.
