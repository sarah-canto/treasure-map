package fr.exercice.treasuremap.controller;

import fr.exercice.treasuremap.model.Adventurer;
import fr.exercice.treasuremap.model.Game;
import fr.exercice.treasuremap.service.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Ce controller va porter toute l'exécution de la simulation de jeu :
 * <li>Récupération du chemin du fichier d'entrée</li>
 * <li>Validation de la syntaxe du fichier</li>
 * <li>Création des objets du jeu</li>
 * <li>Exécution du jeu</li>
 * <li>Génération résultat du jeu</li>
 * <li>Création du fichier de sortie</li>
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class GameController {
    @NonNull
    private final InputFileService inputFileService;
    @NonNull
    private final InputValidationService inputValidationService;
    @NonNull
    private final GameAssetsCreatorService gameAssetsCreatorService;
    @NonNull
    private final MoveAdventurerService moveAdventurerService;
    @NonNull
    private final OutputFileService outputFileService;
    @NonNull
    private final Game game = new Game();

    public void playGame() {
        try {
            Path inputFilePath = generateGame();
            int turn = 0;
            while (game.getAdventurers().stream().anyMatch(adventurer -> !adventurer.isDoneMoving())) {
                turn = playTurn(turn);
            }
            log.info("Fin du jeu, création du fichier de sortie en cours");
            outputFileService.writeOutputFile(game, inputFilePath);
        } catch (NoSuchFileException e) {
            log.error("Nom du fichier en erreur : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("{} Veuillez corriger le fichier", e.getMessage());
        } catch (IllegalStateException e) {
            log.error("{} Veuillez revoir le fichier d'entrée", e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private int playTurn(int turn) {
        log.info("Tour n°" + turn);
        for (Adventurer adventurer : game.getAdventurers()) {
            moveAdventurerService.checkIfAdventurerIsDoneMoving(adventurer, turn);
            if (!adventurer.isDoneMoving()) {
                moveAdventurerService.playMove(game, adventurer, turn);
            }
        }
        turn++;
        return turn;
    }

    /**
     * Cette méthode récupère le chemin du fichier d'entrée, le valide et crée tous les objets nécessaires au jeu.
     *
     * @return inputFilePath le chemin du fichier d'entrée qui va servir pour créer le fichier de sortie à la fin du jeu
     * @throws IOException Si une erreur I/O se produit lors de la lecture du fichier
     */
    private Path generateGame() throws IOException {
        String inputFilePath = inputFileService.getAndCheckInputFilePath();
        List<String> inputLines = inputFileService.readFile(inputFilePath);
        inputValidationService.doFirstChecks(inputLines);
        for (String line : inputLines) {
            String[] splitLine = inputFileService.getCleanSplitElements(line);
            switch (line) {
                case String l when StringUtils.startsWithIgnoreCase(l, "C") -> {
                    inputValidationService.checkMapLine(l, splitLine);
                    game.setGameMap(gameAssetsCreatorService.createMap(splitLine));
                }
                case String l when StringUtils.startsWithIgnoreCase(l, "M") -> {
                    inputValidationService.checkMountainLine(l, splitLine);
                    game.getMountainCells().add(gameAssetsCreatorService.createMountain(splitLine, game, l));
                }
                case String l when StringUtils.startsWithIgnoreCase(l, "T") -> {
                    inputValidationService.checkTreasureLine(l, splitLine);
                    game.getTreasureCells().add(gameAssetsCreatorService.createTreasure(splitLine, game, l));
                }
                case String l when StringUtils.startsWithIgnoreCase(l, "A") -> {
                    inputValidationService.checkAdventurerLine(l, splitLine);
                    game.getAdventurers().add(gameAssetsCreatorService.createAdventurer(splitLine, game, l));
                }
                default ->
                        throw new IllegalStateException("Problème lors de la génération du jeu à la ligne : " + line);
            }
        }
        return Paths.get(inputFilePath);
    }
}
