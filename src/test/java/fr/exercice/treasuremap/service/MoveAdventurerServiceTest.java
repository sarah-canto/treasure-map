package fr.exercice.treasuremap.service;

import fr.exercice.treasuremap.model.Adventurer;
import fr.exercice.treasuremap.model.Game;
import fr.exercice.treasuremap.model.Orientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static fr.exercice.treasuremap.utils.TestUtils.generateAdventurer;
import static fr.exercice.treasuremap.utils.TestUtils.generateGame;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MoveAdventurerServiceTest {

    private final MoveAdventurerService moveAdventurerService = new MoveAdventurerService();
    private static Game game;

    @BeforeEach
    void setUp() {
        game = generateGame();
    }

    @ParameterizedTest
    @CsvSource({("Sydney, 8, false"), ("Frodon, 15,true"), ("Nathan, 11,true")})
    void checkIfAdventurerIsDoneMoving_ok(String adventurerName, int turn, boolean expectedDoneMoving) {
        Adventurer adventurer = generateAdventurer(adventurerName);
        moveAdventurerService.checkIfAdventurerIsDoneMoving(adventurer, turn);
        assertThat(adventurer.isDoneMoving())
                .as(String.format("L'aventurier %s doit avoir le champ isDoneMoving à %s", adventurerName, expectedDoneMoving))
                .isEqualTo(expectedDoneMoving);
    }

    @Test
    void playMove_ok() {
        Adventurer adventurer = game.getAdventurers().stream().findFirst().get();
        assertDoesNotThrow(() ->
        {
            for (int i = 0; i < adventurer.getMoves().length(); i++) {
                moveAdventurerService.playMove(game, adventurer, i);
            }
        });
    }

    @Test
    void playMove_whenMoveIsOutOfMap_shouldNotChangeAdventurerPositionAndOrientationAndNbTreasure() {
        Adventurer adventurer = game.getAdventurers().stream().findFirst().get();
        adventurer.setPosX(0);
        adventurer.setOrientation(Orientation.O);
        int posX = adventurer.getPosX();
        int posY = adventurer.getPosY();
        Orientation orientation = adventurer.getOrientation();
        int nbTreasure = adventurer.getNbTreasure();

        moveAdventurerService.playMove(game, adventurer, 0);

        assertEquals(posX, adventurer.getPosX(), "La position X de l'aventurier ne doit pas avoir changé");
        assertEquals(posY, adventurer.getPosY(), "La position Y de l'aventurier ne doit pas avoir changé");
        assertEquals(orientation, adventurer.getOrientation(), "L'orientation de l'aventurier ne doit pas avoir changé");
        assertEquals(nbTreasure, adventurer.getNbTreasure(), "Le nombre de trésors de l'aventurier ne doit pas avoir changé");
    }

    @Test
    void playMove_whenMoveIsOnMountainCell_shouldNotChangeAdventurerPositionAndOrientationAndNbTreasure() {
        Adventurer adventurer = game.getAdventurers().stream().findFirst().get();
        adventurer.setOrientation(Orientation.N);
        int posX = adventurer.getPosX();
        int posY = adventurer.getPosY();
        Orientation orientation = adventurer.getOrientation();
        int nbTreasure = adventurer.getNbTreasure();

        moveAdventurerService.playMove(game, adventurer, 0);

        assertEquals(posX, adventurer.getPosX(), "La position X de l'aventurier ne doit pas avoir changé");
        assertEquals(posY, adventurer.getPosY(), "La position Y de l'aventurier ne doit pas avoir changé");
        assertEquals(orientation, adventurer.getOrientation(), "L'orientation de l'aventurier ne doit pas avoir changé");
        assertEquals(nbTreasure, adventurer.getNbTreasure(), "Le nombre de trésors de l'aventurier ne doit pas avoir changé");
    }

    @Test
    void playMove_whenMoveIsOnAdventurerCell_shouldNotChangeAdventurerPositionAndOrientationAndNbTreasure() {
        Adventurer adventurer = game.getAdventurers().stream().findFirst().get();

        Adventurer blockingAdventurer = generateAdventurer("Block");
        blockingAdventurer.setPosY(2);
        game.getAdventurers().add(blockingAdventurer);

        int posX = adventurer.getPosX();
        int posY = adventurer.getPosY();
        Orientation orientation = adventurer.getOrientation();
        int nbTreasure = adventurer.getNbTreasure();

        moveAdventurerService.playMove(game, adventurer, 0);

        assertEquals(posX, adventurer.getPosX(), "La position X de l'aventurier ne doit pas avoir changé");
        assertEquals(posY, adventurer.getPosY(), "La position Y de l'aventurier ne doit pas avoir changé");
        assertEquals(orientation, adventurer.getOrientation(), "L'orientation de l'aventurier ne doit pas avoir changé");
        assertEquals(nbTreasure, adventurer.getNbTreasure(), "Le nombre de trésors de l'aventurier ne doit pas avoir changé");
    }

    @Test
    void playMove_whenMoveIsOnTreasure_shouldChangeAdventurerPositionAndNbTreasure() {
        Adventurer adventurer = game.getAdventurers().stream().findFirst().get();
        int posX = adventurer.getPosX();
        int posY = adventurer.getPosY();
        Orientation orientation = adventurer.getOrientation();
        int nbTreasure = adventurer.getNbTreasure();

        moveAdventurerService.playMove(game, adventurer, 0);
        moveAdventurerService.playMove(game, adventurer, 1);

        assertEquals(posX, adventurer.getPosX(), "La position X de l'aventurier ne doit pas avoir changé");
        assertNotEquals(posY, adventurer.getPosY(), "La position Y de l'aventurier doit avoir changé");
        assertEquals(posY + 2, adventurer.getPosY(), "La position Y de l'aventurier est incorrecte");
        assertEquals(orientation, adventurer.getOrientation(), "L'orientation de l'aventurier ne doit pas avoir changé");
        assertNotEquals(nbTreasure, adventurer.getNbTreasure(), "Le nombre de trésors de l'aventurier doit avoir changé");
        assertEquals(nbTreasure + 1, adventurer.getNbTreasure(), "Le nombre de trésors est incorrect");
    }

}
