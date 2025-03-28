package fr.exercice.treasuremap.service;

import fr.exercice.treasuremap.model.Adventurer;
import fr.exercice.treasuremap.model.Cell;
import fr.exercice.treasuremap.model.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Ce service sert à générer le fichier de sortie contenant le résultat final du jeu.
 * Il va traiter les objets contenus dans le {@link Game} à la fin du jeu,
 * puis créer un fichier texte qui sera stocké dans le même répertoire que le fichier d'entrée
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OutputFileService {

    // Chaque élément du jeu (carte, montagne, trésor, aventurier) doit être défini sur une ligne et séparé dans le fichier texte par des tirets.
    public static final String SEPARATOR = " - ";
    // Le fichier de sortie sera nommé comme le fichier d'entrée suivi du suffixe
    public static final String RESULT_NAME_SUFFIX = "_result.txt";

    /**
     * Méthode qui crée le fichier de sortie. S'il en existe déjà un du même nom, il va le supprimer et en créer un nouveau.
     *
     * @param game          Le jeu dans son état final
     * @param inputFilePath Le chemin absolu du fichier d'entrée, qui va servir à créer celui de sortie.
     * @throws IOException Si une erreur I/O se produit lors de la suppression ou création d'un des fichiers
     */
    public void writeOutputFile(Game game, Path inputFilePath) throws IOException {
        List<String> txtOutput = generateOutput(game);
        Path outputFilePath = Paths.get(String.valueOf(inputFilePath.getParent()),
                FilenameUtils.getBaseName(String.valueOf(inputFilePath.getFileName())) + RESULT_NAME_SUFFIX);
        Files.deleteIfExists(outputFilePath);
        Files.createFile(outputFilePath);
        for (String str : txtOutput) {
            Files.writeString(outputFilePath, str + System.lineSeparator(),
                    StandardOpenOption.APPEND);
        }
        log.info("Le fichier de sortie a été créé à l'emplacement {}", outputFilePath);
    }

    /**
     * On attend un format final (où posX est une abscisse et posY une ordonnée sur la carte) :
     * <li>Entièrement en majuscules, sauf le nom de l'aventurier</li>
     * <li>Tous les éléments séparés par le séparateur {@code SEPARATOR}</li>
     * <li>La ligne de la carte en premier, de la forme "C, hauteur, largeur"</li>
     * <li>La ou les lignes de montagne, de la forme "M, posX, posY"</li>
     * <li>La ou les lignes de trésor qui ont un nombre de trésors restants > 0, de la forme "T, posX, posY, nombre de trésors restants"</li>
     * <li>La ou les lignes des aventuriers, de la forme "A, posX, posY, orientation, nombre de trésors ramassés"</li>
     *
     * @param game Le jeu à traiter dans son état final
     * @return La liste de toutes les lignes à insérer dans le fichier dans l'ordre
     */
    private List<String> generateOutput(Game game) {
        List<String> outputLines = new ArrayList<>();
        outputLines.add(StringUtils.join(List.of("C", game.getGameMap().getWidth(), game.getGameMap().getHeight()), SEPARATOR));
        for (Cell mountain : game.getMountainCells()) {
            outputLines.add(StringUtils.join(List.of("M", mountain.getPosX(), mountain.getPosY()), SEPARATOR));
        }
        for (Cell treasure : game.getTreasureCells()) {
            if (treasure.getNbTreasure() > 0) {
                outputLines.add(StringUtils.join(List.of("T", treasure.getPosX(), treasure.getPosY(), treasure.getNbTreasure()), SEPARATOR));
            }
        }
        for (Adventurer adventurer : game.getAdventurers()) {
            outputLines.add(StringUtils.join(List.of("A", adventurer.getName(), adventurer.getPosX(), adventurer.getPosY(), adventurer.getOrientation().name(), adventurer.getNbTreasure()), SEPARATOR));
        }
        return outputLines;
    }
}