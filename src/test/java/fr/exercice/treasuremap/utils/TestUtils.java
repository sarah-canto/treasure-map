package fr.exercice.treasuremap.utils;

import fr.exercice.treasuremap.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
public class TestUtils {

    /**
     * Permet de simuler l'insertion d'un input utilisateur dans la ligne de commande
     *
     * @param userInput ce que rédige l'utilisateur dans la ligne de commande
     */
    public static void writeInCommandLineRunner(String userInput) {
        InputStream input = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(input);
    }

    /**
     * Permet de générer une game standard que l'on peut adapter suivant les besoins du test
     *
     * @return Game pouvant être représentée sous la forme :
     * .           M           .
     * .           A(Lara, S)  .
     * .           .           .
     * .           T(2)        .
     */
    public static Game generateGame() {
        Game game = new Game();
        game.setGameMap(generateGameMap());
        game.getAdventurers().add(generateAdventurer("Lara"));
        game.getMountainCells().add(generateMountain());
        game.getTreasureCells().add(generateTreasure());
        return game;
    }

    public static GameMap generateGameMap() {
        return new GameMap()
                .setHeight(3)
                .setWidth(4);
    }

    public static Cell generateMountain() {
        return new Cell()
                .setPosX(1)
                .setPosY(0)
                .setType(CellType.MOUTAIN);
    }

    public static Cell generateTreasure() {
        return new Cell()
                .setPosX(1)
                .setPosY(3)
                .setNbTreasure(2)
                .setType(CellType.TREASURE);
    }

    public static Adventurer generateAdventurer(String name) {
        return new Adventurer()
                .setName(name)
                .setPosX(1)
                .setPosY(1)
                .setOrientation(Orientation.S)
                .setMoves("AADADAGGA");
    }
}
