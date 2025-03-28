package fr.exercice.treasuremap.service;

import fr.exercice.treasuremap.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameAssetsCreatorServiceTest {

    private final GameAssetsCreatorService gameAssetsCreatorService = new GameAssetsCreatorService();
    //On utilise la vraie méthode getCleanSplitElements du service pour les lignes vérifiées afin de garantir que les tests fonctionnent toujours si elle venait à changer.
    private final InputFileService inputFileService = new InputFileService();
    @Mock
    private Game game = Mockito.mock(Game.class);

    @Test
    void createMap_ok() {
        String[] splitLine = new String[]{"C", "8", "12"};
        GameMap gameMap = gameAssetsCreatorService.createMap(splitLine);

        assertEquals(8, gameMap.getWidth(), "La largeur de la carte est incorrecte");
        assertEquals(12, gameMap.getHeight(), "La hauteur de la carte est incorrecte");
    }

    @Test
    void createMountain_ok() {
        String line = "M - 1 - 2";
        String[] splitLine = inputFileService.getCleanSplitElements(line);

        Cell mountain = gameAssetsCreatorService.createMountain(splitLine, game, line);

        assertEquals(CellType.MOUTAIN, mountain.getType());
        assertEquals(1, mountain.getPosX(), "La position X de la montagne est incorrecte");
        assertEquals(2, mountain.getPosY(), "La position X de la montagne est incorrecte");
    }

    @Test
    void createMountain_whenCellIsNotEmpty_shouldThrowIllegalArgumentException() {
        String line = "M - 1 - 2";
        String[] splitLine = inputFileService.getCleanSplitElements(line);
        Cell alreadyExistingCell = new Cell().setPosX(1).setPosY(2).setType(CellType.MOUTAIN);
        Mockito.when(game.getMountainCells()).thenReturn(List.of(alreadyExistingCell));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameAssetsCreatorService.createMountain(splitLine, game, line);
        });

        assertEquals("Impossible de créer la montagne de la ligne " + line, exception.getMessage(), "Le message d'erreur attendu est incorrect");
    }

    @Test
    void createTreasure_ok() {
        String line = "T - 1 - 2 - 3";
        String[] splitLine = inputFileService.getCleanSplitElements(line);

        Cell treasure = gameAssetsCreatorService.createTreasure(splitLine, game, line);

        assertEquals(CellType.TREASURE, treasure.getType(), "Le type de cellule est incorrect");
        assertEquals(1, treasure.getPosX(), "La position X du trésor est incorrecte");
        assertEquals(2, treasure.getPosY(), "La position Y du trésor est incorrecte");
        assertEquals(3, treasure.getNbTreasure(), "Le nombre de trésors est incorrect");
    }

    @Test
    void createTreasure_whenCellIsNotEmpty_shouldThrowIllegalArgumentException() {
        String line = "T - 1 - 2 - 3";
        String[] splitLine = inputFileService.getCleanSplitElements(line);
        Adventurer alreadyOccupiedCell = new Adventurer().setPosX(1).setPosY(2).setOrientation(Orientation.E).setName("Robinson").setMoves("DAAGAAADDDA");
        Mockito.when(game.getAdventurers()).thenReturn(List.of(alreadyOccupiedCell));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameAssetsCreatorService.createTreasure(splitLine, game, line);
        });

        assertEquals("Impossible de créer le trésor de la ligne " + line, exception.getMessage(), "Le message d'erreur attendu est incorrect");
    }

    @Test
    void createAdventurer_ok() {
        String line = "A - Lara - 1 - 2 - S - AADADA";
        String[] splitLine = inputFileService.getCleanSplitElements(line);
        Adventurer alreadyExistingAdventurer = new Adventurer().setPosX(2).setPosY(3).setNbTreasure(2).setName("Laura");
        Mockito.when(game.getAdventurers()).thenReturn(List.of(alreadyExistingAdventurer));

        Adventurer adventurer = gameAssetsCreatorService.createAdventurer(splitLine, game, line);

        assertEquals(1, adventurer.getPosX(), "La position X de l'aventurier est incorrecte");
        assertEquals(2, adventurer.getPosY(), "La position Y de l'aventurier est incorrecte");
        assertEquals(0, adventurer.getNbTreasure(), "Le nombre de trésors de l'aventurier est incorrect");
        assertEquals("Lara", adventurer.getName(), "Le nom de l'aventurier est incorrect");
        assertEquals(Orientation.S, adventurer.getOrientation(), "L'orientation de l'aventurier n'est pas bonne");
        assertEquals("AADADA", adventurer.getMoves(), "Les mouvements de l'aventurier sont incorrects");
    }

    @Test
    void createAdventurer_whenCellIsNotEmpty_shouldThrowIllegalArgumentException() {
        String line = "A - Lara - 1 - 2 - S - AADADA";
        String[] splitLine = inputFileService.getCleanSplitElements(line);
        Cell alreadyExistingCell = new Cell().setPosX(1).setPosY(2).setNbTreasure(2).setType(CellType.TREASURE);
        Mockito.when(game.getTreasureCells()).thenReturn(List.of(alreadyExistingCell));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameAssetsCreatorService.createAdventurer(splitLine, game, line);
        });

        assertEquals("Impossible de créer l'aventurier de la ligne " + line, exception.getMessage(), "Le message d'erreur attendu est incorrect");
    }

    @Test
    void createAdventurer_whenNameAlreadyExists_shouldThrowIllegalArgumentException() {
        String line = "A - Lara - 1 - 2 - S - AADADA";
        String[] splitLine = inputFileService.getCleanSplitElements(line);
        Adventurer alreadyExistingAdventurer = new Adventurer().setPosX(2).setPosY(3).setNbTreasure(2).setName("Lara");
        Mockito.when(game.getAdventurers()).thenReturn(List.of(alreadyExistingAdventurer));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameAssetsCreatorService.createAdventurer(splitLine, game, line);
        });

        assertEquals("Impossible de créer l'aventurier de la ligne " + line, exception.getMessage(), "Le message d'erreur attendu est incorrect");
    }
}