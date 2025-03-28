package fr.exercice.treasuremap.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Ce service a pour rôle de vérifier toutes les valeurs du fichier d'entrée. Si l'une d'entre elles ne respecte pas les normes du jeu, le jeu s'arrête.
 */
@Service
@RequiredArgsConstructor
public class InputValidationService {

    public static final int MAPSIZE = 85182;
    public static final int MAP_MAX_NB_CHARACTERS = 5;
    public static final int MAP_MAX_SPLIT_LENGTH = 3;
    public static final int MOUNTAIN_MAX_NB_CHARACTERS = 5;
    public static final int MOUNTAIN_MAX_SPLIT_LENGTH = 3;
    public static final int TREASURE_MAX_NB_CHARACTERS = 7;
    public static final int TREASURE_MAX_SPLIT_LENGTH = 4;
    public static final int ADVENTURER_MAX_NB_CHARACTERS = 11;
    public static final int ADVENTURER_MAX_SPLIT_LENGTH = 6;
    private int mapWidth = 0;
    private int mapHeight = 0;

    /**
     * On part du principe que pour être valide, le fichier doit comporter au moins une ligne "carte" commençant par C, et une ligne "aventurier" commençant par A.
     * Sans trésor le jeu n'a que peu d'intérêt, mais il peut fonctionner tout de même.
     *
     * @param inputLines l'ensemble des lignes du fichier d'entrée qui doit avoir au minimum une taille de 2 pour être valide.
     */
    public void doFirstChecks(List<String> inputLines) {
        if (inputLines.size() < 2) {
            throw new IllegalStateException("Le fichier est incomplet, impossible de créer le jeu. Il doit contenir au moins une ligne pour la carte et une pour un aventurier");
        }
        if (!StringUtils.startsWith(StringUtils.strip(StringUtils.upperCase(inputLines.getFirst())), "C")
                || !StringUtils.startsWith(StringUtils.strip(StringUtils.upperCase(inputLines.getLast())), "A")) {
            throw new IllegalStateException("Le fichier est mal formaté, impossible de créer le jeu. Il doit au moins commencer par C et sa dernière ligne par A.");
        }
    }

    /**
     * Vérifications des valeurs sur la ligne du fichier d'entrée correspondant à la carte aux trésors.
     * Elle doit avoir :
     * <li>Le bon nombre minimum de caractères et de champs split</li>
     * <li>Une hauteur et une largeur sous forme d'entiers supérieurs à zéro</li>
     * <li>Une surface finale plus petite que la {@code MAPSIZE} : taille du département de la Madre de Dios</li>
     *
     * @param mapLine  La ligne du fichier à traiter correspondant à la carte qui est loggée en cas d'erreur
     * @param mapSplit La ligne splittée et nettoyée
     */
    public void checkMapLine(String mapLine, String[] mapSplit) {
        checkNumberOfCharacters(mapLine, mapSplit, MAP_MAX_NB_CHARACTERS, MAP_MAX_SPLIT_LENGTH, "La ligne de la carte ne contient pas le bon nombre de caractères");
        checkIfCoordinatesAreNumbers(mapSplit, 1, 2, "La hauteur ou la largeur de la carte n'est pas un nombre entier");
        mapWidth = Integer.parseInt(StringUtils.strip(mapSplit[1]));
        mapHeight = Integer.parseInt(StringUtils.strip(mapSplit[2]));
        if (mapWidth < 1 || mapHeight < 1) {
            throw new IllegalArgumentException("La carte a une hauteur ou une largeur inférieure à 1");
        }
        if (mapWidth * mapHeight > MAPSIZE) {
            throw new IllegalArgumentException("La carte est plus grande que le département de la Madre de Dios ! Veuillez revoir la hauteur ou la largeur de la carte");
        }
    }

    /**
     * Vérifications des valeurs sur la ligne du fichier d'entrée correspondant à une montagne. Elle doit avoir :
     * <li>Le bon nombre minimum de caractères et de champs split</li>
     * <li>Une abscisse X et une ordonnée Y sous forme d'entiers supérieurs à zéro</li>
     * <li>Un X et un Y dans les limites de la surface de la carte</li>
     *
     * @param mountainLine  La ligne du fichier à traiter correspondant à la montagne qui est loggée en cas d'erreur
     * @param mountainSplit La ligne splittée et nettoyée
     */
    public void checkMountainLine(String mountainLine, String[] mountainSplit) {
        checkNumberOfCharacters(mountainLine, mountainSplit, MOUNTAIN_MAX_NB_CHARACTERS, MOUNTAIN_MAX_SPLIT_LENGTH, "La ligne d'une des montagnes ne contient pas le bon nombre de caractères");
        checkIfCoordinatesAreNumbers(mountainSplit, 1, 2, "La position d'une des montagnes n'est pas un nombre entier");
        checkIfIsOutOfMap(mountainSplit, 1, 2, "La position d'une des montagnes sort de la taille de la carte");
    }

    /**
     * Vérifications des valeurs sur la ligne du fichier d'entrée correspondant à un trésor. Elle doit avoir :
     * <li>Le bon nombre minimum de caractères et de champs split</li>
     * <li>Une abscisse X et une ordonnée Y sous forme d'entiers supérieurs à zéro</li>
     * <li>Un nombre de trésors sous forme d'entier supérieur à zéro</li>
     * <li>Un X et un Y dans les limites de la surface de la carte</li>
     *
     * @param treasureLine  La ligne du fichier à traiter correspondant à la montagne qui est loggée en cas d'erreur
     * @param treasureSplit La ligne splittée et nettoyée
     */
    public void checkTreasureLine(String treasureLine, String[] treasureSplit) {
        checkNumberOfCharacters(treasureLine, treasureSplit, TREASURE_MAX_NB_CHARACTERS, TREASURE_MAX_SPLIT_LENGTH, "La ligne d'un des trésors ne contient pas le bon nombre de caractères");
        checkIfCoordinatesAreNumbers(treasureSplit, 1, 2, "La position d'un des trésors n'est pas un nombre entier");
        String nbTreasure = treasureSplit[3];
        if (!StringUtils.isNumeric(nbTreasure)) {
            throw new IllegalArgumentException("La quantité d'un des trésors n'est pas un nombre entier");
        }
        if (Integer.parseInt(nbTreasure) < 1) {
            throw new IllegalArgumentException("La quantité d'un des trésors est invalide. Elle doit être de 1 ou plus");
        }
        checkIfIsOutOfMap(treasureSplit, 1, 2, "La position d'un des trésors sort de la taille de la carte");
    }

    /**
     * Vérifications des valeurs sur la ligne du fichier d'entrée correspondant à un aventurier. Elle doit avoir :
     * <li>Le bon nombre minimum de caractères et de champs split</li>
     * <li>Une abscisse X et une ordonnée Y sous forme d'entiers supérieurs à zéro</li>
     * <li>Un X et un Y dans les limites de la surface de la carte</li>
     * <li>Un nom d'au moins un caractère</li>
     * <li>Une orientation correspondant à l'une des lettres : N, S, E, O (majuscule ou minuscule)</li>
     * <li>Une suite d'au moins un mouvement correspondant à l'une des lettres : A, D, G (majuscule ou minuscule)</li>
     *
     * @param adventurerLine  La ligne du fichier à traiter correspondant au trésor qui est loggée en cas d'erreur
     * @param adventurerSplit La ligne splittée et nettoyée
     */
    public void checkAdventurerLine(String adventurerLine, String[] adventurerSplit) {
        checkNumberOfCharacters(adventurerLine, adventurerSplit, ADVENTURER_MAX_NB_CHARACTERS, ADVENTURER_MAX_SPLIT_LENGTH, "La ligne d'un des aventuriers ne contient pas le bon nombre de caractères");
        checkIfCoordinatesAreNumbers(adventurerSplit, 2, 3, "La position d'un des aventuriers n'est pas un nombre entier");
        checkIfIsOutOfMap(adventurerSplit, 2, 3, "La position d'un des aventuriers sort de la taille de la carte");

        String name = StringUtils.strip(adventurerSplit[1]);
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Le nom d'un des aventuriers est vide");
        }

        String orientation = StringUtils.strip(adventurerSplit[4]);
        if (!StringUtils.equalsAnyIgnoreCase(orientation, "N", "S", "E", "O")) {
            throw new IllegalArgumentException("L'orientation d'un des aventuriers ne correspond pas à un point cardinal");
        }

        String moves = StringUtils.strip(adventurerSplit[5]);
        if (!StringUtils.containsOnly(moves, "A, D, G, a, d, g") || StringUtils.isBlank(moves)) {
            throw new IllegalArgumentException("L'un des mouvements d'un des aventuriers n'est pas valide");
        }
    }

    private void checkIfCoordinatesAreNumbers(String[] split, int indexOfX, int indexOfY, String errorMessage) {
        if (!StringUtils.isNumeric(split[indexOfX]) || !StringUtils.isNumeric(split[indexOfY])) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void checkNumberOfCharacters(String line, String[] split, int maxNumberOfCharacters, int maxLengthOfSplit, String errorMessage) {
        if (StringUtils.length(line) < maxNumberOfCharacters || split.length != maxLengthOfSplit) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void checkIfIsOutOfMap(String[] split, int indexOfX, int indexOfY, String errorMessage) {
        if (Integer.parseInt(StringUtils.strip(split[indexOfX])) > mapWidth || Integer.parseInt(StringUtils.strip(split[indexOfY])) > mapHeight) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
