# Treasure Map

Bienvenue à la Madre de Dios ! Le gouvernement péruvien a autorisé les aventuriers en quête de trésors à explorer le département.
Qui trouvera le plus de richesses dans ces plaines montagneuses ?

## Installation

### Prérequis

- Java 21 ou version supérieure
- Maven
- Un fichier .txt avec lequel lancer la simulation

### Étapes d'installation

1. Clonez le dépôt :

   ```bash
   git clone https://github.com/sarah-canto/treasure-map.git
   cd treasure-map
   Exécutez le projet dans votre IDE ou lancez la commande ./mvnw spring-boot:run

## À propos du projet

Le principe est simple : on cherche à exécuter un programme permettant à des aventuriers de chercher des trésors sur une carte prédéfinie.
Sur cette carte peuvent se trouver des plaines, des montagnes, des trésors et plusieurs aventuriers.

Le programme permet de suivre ces aventuriers dans leurs déplacements et leur collecte de trésors.

En entrée de la simulation, vous devez fournir un fichier .txt. Celui-ci va être consommé par le programme, puis rendre le résultat dans un fichier .txt de sortie.

### Règles de base :
* La surface totale (hauteur*largeur) de la carte ne doit pas excéder les 85182km² du département de la Madre de Dios.
* Un aventurier avance d'une seule case par tour à la fois, en hauteur ou en largeur, pas en diagonale
* Les mouvements des aventuriers à chaque tour se déroulent dans le même ordre que leur apparition dans le fichier d'entrée. 
* Si un mouvement est impossible, l'aventurier reste sur sa case et garde son orientation.
* Une montagne empêche un aventurier d'avancer et lui fait passer son tour.
* Plusieurs trésors peuvent se situer sur la même case.
  * Lorsqu'un aventurier arrive sur une case trésor, il en ramasse un. 
  * La case trésor a donc un trésor de moins.
  * Pour ramasser un autre trésor sur la même case, l'aventurier devra la quitter puis revenir dessus.
* Si un aventurier se déplace sur une case déjà occupée par un autre aventurier, il passe son tour.

## Fichier d'entrée :

Le fichier doit répondre à un certain nombre de règles pour pouvoir être traité correctement.
* Il est constitué de plusieurs "assets" : 
  * Une carte avec une **hauteur** et une **largeur** définies par des entiers.
  La carte est donc définie ainsi : `C - {hauteur} - {largeur}`
  * Une ou plusieurs montagnes, avec leur position en **abscisse** et **ordonnée** de la carte.
  Une montagne est donc définie ainsi : `M - {abscisse} - {ordonnée}`
  * Un ou plusieurs trésors, avec leur position en **abscisse** et **ordonnée** de la carte et la **quantité** de trésors à cette position.
    Un trésor est donc défini ainsi : `T - {abscisse} - {ordonnée} - {nombre de trésors}`
  * Un ou plusieurs aventuriers, avec leur **nom** unique, leur position en **abscisse** et **ordonnée** de la carte, leur **orientation** et tous les **mouvements** qu'ils vont faire au cours du jeu. 
  Un aventurier est donc défini ainsi : `A - {abscisse} - {ordonnée} - {orientation} - {mouvements}`. L'orientation doit être de forme `N`, `S`, `E` ou `O`. Un mouvement est défini soit par la lettre `A` pour avancer, `G` pour se tourner vers la gauche ou `D` pour se tourner vers la droite. 
  * Un asset ne peut être créé que si sa position n'est pas déjà occupée par un autre.

### Contraintes :
* Le fichier doit être un fichier texte d'extension `.txt`. 
* Chaque asset doit être décrit sur une ligne à la fois et chaque élément doit être séparé par un tiret.
* Les caractères peuvent être en majuscules ou minuscules, avec des espaces ou non entre chaque tiret.
* Une partie de jeu doit contenir au moins une carte et un aventurier. La carte doit être le premier asset défini dans le fichier.
* Vous pouvez mettre tous les assets dans l'ordre que vous souhaitez, mais il doit obligatoirement y avoir la carte en première ligne et un aventurier en dernière ligne.

## Fichier de sortie

Le fichier de sortie va contenir le résultat de la simulation du jeu.
* Il est constitué de :
  * La carte du jeu qui a été définie à l'entrée : `C - {hauteur} - {largeur}`
  * La ou les montagnes définies à l'entrée : `M - {abscisse} - {ordonnée}`
  * Le ou les trésors qui restent (et donc pas ceux dont la quantité est tombée à zéro) : `T - {abscisse} - {ordonnée} - {nombre de trésors restants}`
  * Le ou les aventuriers à leur position finale et avec les trésors ramassés : `A - {abscisse finale} - {ordonnée finale} - {orientation finale} - {nombre de trésors ramassés}`

* Détails :
  * Le fichier de sortie va ordonner les lignes dans l'ordre : carte, montagne(s), trésor(s), aventurier(s).
  * Les caractères de sortie seront tous mis en majuscules, sauf le nom de l'aventurier dont la première lettre sera toutefois capitalisée.
  * Chaque asset sera séparé par un tiret encadré d'espaces "` - `"

## Exemples de fichier :
Voici un exemple-type de fichier d'entrée.
```text
C - 3 - 4
M - 1 - 0
T - 0 - 3 - 2
M - 1 - 4
A- Lara - 1 - 2 - O - AGAADADAGGA
T - 1 - 3 - 3
A - Sydney - 2 - 3 - O - AGGADDAAGADADAGGA
```

Et son fichier de sortie correspondant :
```text
C - 3 - 4
M - 1 - 0
M - 1 - 4
T - 1 - 3 - 1
A - Lara - 0 - 4 - S - 2
A - Sydney - 0 - 3 - S - 2
```

Des fichiers d'exemple sont disponibles dans le package resources des tests.