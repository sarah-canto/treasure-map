package fr.exercice.treasuremap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Ce service gère tout ce qui va concerner le fichier d'entrée du jeu. Il récupère son chemin, valide sa valeur, l'extension du fichier, lit et nettoie le contenu.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InputFileService {

    /**
     * Méthode qui permet de recevoir le chemin absolu du fichier d'entrée du jeu. Elle vérifie que la valeur n'est pas vide et l'extension correcte
     *
     * @return le chemin absolu du fichier d'entrée
     */
    public String getAndCheckInputFilePath() {
        log.info("Veuillez entrer le le chemin du fichier d'entrée : ");
        String filepath = "";
        Scanner s = new Scanner(System.in);
        filepath = s.nextLine();
        if (StringUtils.isBlank(filepath)) {
            throw new IllegalStateException("Le chemin de fichier entré est vide, veuillez réessayer.");
        }
        if (!FilenameUtils.getExtension(filepath).equalsIgnoreCase("txt")) {
            throw new IllegalArgumentException("L'extension du fichier doit être '.txt', veuillez réessayer.");
        }
        return filepath;
    }

    /**
     * Lit le contenu du fichier texte d'entrée
     *
     * @param filename le chemin absolu du fichier
     * @return une liste de String contenant chaque ligne du fichier les unes à la suite des autres
     * @throws IOException si une erreur I/O se produit lors de la lecture du fichier
     */
    public List<String> readFile(String filename) throws IOException {
        List<String> inputLines;
        try {
            Path path = Paths.get(filename);
            inputLines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            String message = "Le fichier spécifié est introuvable : ";
            log.error(message);
            throw new NoSuchFileException(message + e.getMessage());
        } catch (IOException e) {
            log.error("Une erreur s'est produite lors de la lecture du fichier : " + e.getMessage());
            throw e;
        }
        return inputLines;
    }

    /**
     * Cette méthode va split chaque élément de la ligne avec le séparateur {@code -} en gardant chaque champ, même vide, et retirer tous les whitespace
     *
     * @param line la ligne à séparer
     * @return un array de String avec tous les éléments de la ligne sans whitespace
     */
    public String[] getCleanSplitElements(String line) {
        String[] splitLine = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "-");
        for (int i = 0; i < splitLine.length; i++) {
            splitLine[i] = StringUtils.deleteWhitespace(splitLine[i]);
        }
        return splitLine;
    }
}
