package fr.exercice.treasuremap.service;

import fr.exercice.treasuremap.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Ce service permet de créer tous les objets nécessaires au jeu, en s'assurant que ladite création est possible dans le contexte de l'objet {@link Game}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameAssetsCreatorService {
    /**
     * Méthode de création de la carte aux trésors {@link GameMap}
     *
     * @param mapSplitLine l'array de string correspondant à la ligne de la carte du fichier d'entrée.
     *                     Il doit être de la forme {"C","{@code int}","{@code int}"}"
     *                     Le premier {@code int} correspond à la hauteur de la carte
     *                     Le deuxième {@code int} correspond à la largeur de la carte
     * @return {@link GameMap}
     */
    public GameMap createMap(String[] mapSplitLine) {
        GameMap gameMap = new GameMap();
        return gameMap
                .setWidth(Integer.parseInt(mapSplitLine[1]))
                .setHeight(Integer.parseInt(mapSplitLine[2]));
    }

    /**
     * Méthode de création d'une {@link Cell} de type montagne sur la carte
     *
     * @param mountainSplitLine l'array de string correspondant à une ligne de montagne du fichier d'entrée.
     *                          Il doit être de la forme {"M","{@code int}","{@code int}"}"
     *                          Le premier {@code int} correspond à la position X (abscisse) de la montagne
     *                          Le deuxième {@code int} correspond à la position Y (ordonnée) de la montagne
     * @return {@link Cell} de type {@link CellType} {@code MOUNTAIN}
     */
    public Cell createMountain(String[] mountainSplitLine, Game game, String mountainLine) {
        int posX = Integer.parseInt(mountainSplitLine[1]);
        int posY = Integer.parseInt(mountainSplitLine[2]);
        if (isEmptyCell(posX, posY, game)) {
            return new Cell()
                    .setType(CellType.MOUTAIN)
                    .setPosX(posX)
                    .setPosY(posY);
        } else throw new IllegalArgumentException("Impossible de créer la montagne de la ligne " + mountainLine);
    }

    /**
     * Méthode de création d'une {@link Cell} de type trésor sur la carte
     *
     * @param treasureSplitLine l'array de string correspondant à une ligne de trésor du fichier d'entrée.
     *                          Il doit être de la forme {"T","{@code int}","{@code int},"{@code int}"}".
     *                          Le premier {@code int} correspond à la position X (abscisse) du trésor.
     *                          Le deuxième {@code int} correspond à la position Y (ordonnée) du trésor.
     *                          Le troisième {@code int} correspond au nombre de trésors sur la case.
     * @return {@link Cell} de type {@link CellType} {@code TREASURE}
     */
    public Cell createTreasure(String[] treasureSplitLine, Game game, String treasureLine) {
        int posX = Integer.parseInt(treasureSplitLine[1]);
        int posY = Integer.parseInt(treasureSplitLine[2]);
        if (isEmptyCell(posX, posY, game)) {
            Cell treasureCell = new Cell();
            return treasureCell
                    .setType(CellType.TREASURE)
                    .setPosX(Integer.parseInt(treasureSplitLine[1]))
                    .setPosY(Integer.parseInt(treasureSplitLine[2]))
                    .setNbTreasure(Integer.parseInt(treasureSplitLine[3]));
        } else throw new IllegalArgumentException("Impossible de créer le trésor de la ligne " + treasureLine);
    }

    /**
     * Méthode de création d'un {@link Adventurer} sur la carte
     *
     * @param adventurerLine l'array de string correspondant à une ligne d'aventurier du fichier d'entrée.
     *                       Il doit être de la forme {"A","{@code int}","{@code int},"{@code char}, "{@code String}"}".
     *                       Le premier {@code int} correspond à la position X (abscisse) de l'aventurier.
     *                       Le deuxième {@code int} correspond à la position Y (ordonnée) de l'aventurier.
     *                       Le {@code char} correspond à l'orientation de l'aventurier.
     *                       La {@code String} correspond aux mouvements de l'aventurier.
     * @return {@link Adventurer}
     */
    public Adventurer createAdventurer(String[] adventurerSplitLine, Game game, String adventurerLine) {
        int posX = Integer.parseInt(adventurerSplitLine[2]);
        int posY = Integer.parseInt(adventurerSplitLine[3]);
        if (isEmptyCell(posX, posY, game) && nameDoesNotAlreadyExist(adventurerSplitLine[1], game.getAdventurers())) {
            Adventurer adventurer = new Adventurer();
            return adventurer
                    .setName(StringUtils.capitalize(adventurerSplitLine[1]))
                    .setPosX(Integer.parseInt(adventurerSplitLine[2]))
                    .setPosY(Integer.parseInt(adventurerSplitLine[3]))
                    .setOrientation(Orientation.valueOf(StringUtils.upperCase(adventurerSplitLine[4])))
                    .setMoves(adventurerSplitLine[5]);
        } else throw new IllegalArgumentException("Impossible de créer l'aventurier de la ligne " + adventurerLine);
    }

    /**
     * Cette méthode permet de vérifier qu'il n'existe pas déjà un asset du jeu sur la case de l'objet en cours de création.
     *
     * @param posX L'abscisse de la position de l'objet en cours de création
     * @param posY L'ordonnée de la position de l'objet en cours de création
     * @param game Le jeu contenant tous les assets déjà créés
     * @return Si la case est déjà occupée par un asset
     */
    private boolean isEmptyCell(int posX, int posY, Game game) {
        for (Cell mountainCell : game.getMountainCells()) {
            if (mountainCell.getPosX() == posX && mountainCell.getPosY() == posY) {
                log.error("Il existe déjà une montagne à la position {} - {}", mountainCell.getPosX(), mountainCell.getPosY());
                return false;
            }
        }
        for (Cell treasureCell : game.getTreasureCells()) {
            if (treasureCell.getPosX() == posX && treasureCell.getPosY() == posY) {
                log.error("Il existe déjà un trésor à la position {} - {}", treasureCell.getPosX(), treasureCell.getPosY());
                return false;
            }
        }
        for (Adventurer adventurer : game.getAdventurers()) {
            if (adventurer.getPosX() == posX && adventurer.getPosY() == posY) {
                log.error("Il existe déjà un aventurier à la position {} - {}", adventurer.getPosX(), adventurer.getPosY());
                return false;
            }
        }
        return true;
    }

    /**
     * Chaque aventurier doit avoir un nom différent pour pouvoir les distinguer
     *
     * @param name        Le nom de l'aventurier en cours de création
     * @param adventurers La liste des aventuriers déjà créés
     * @return Si un aventurier avec le même nom existe déjà
     */
    private boolean nameDoesNotAlreadyExist(String name, List<Adventurer> adventurers) {
        for (Adventurer adventurer : adventurers) {
            if (adventurer.getName().equals(name)) {
                log.error("Il existe déjà un aventurier qui se nomme {}", adventurer.getName());
                return false;
            }
        }
        return true;
    }
}
