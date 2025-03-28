package fr.exercice.treasuremap.controller;

import fr.exercice.treasuremap.model.Game;
import fr.exercice.treasuremap.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {
    @Mock
    private InputFileService inputFileServiceMock;

    @Mock
    private InputValidationService inputValidationServiceMock;

    @Mock
    private GameAssetsCreatorService gameAssetsCreatorServiceMock;

    @Mock
    private MoveAdventurerService moveAdventurerServiceMock;

    @Mock
    private OutputFileService outputFileServiceMock;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        gameController = new GameController(inputFileServiceMock, inputValidationServiceMock, gameAssetsCreatorServiceMock, moveAdventurerServiceMock, outputFileServiceMock);
    }

    @Test
    void playGame_ok() throws IOException {
        when(inputFileServiceMock.getAndCheckInputFilePath()).thenReturn("testInputFilePath");
        when(inputFileServiceMock.readFile(anyString())).thenReturn(new ArrayList<>());

        gameController.playGame();

        verify(inputFileServiceMock, times(1)).getAndCheckInputFilePath();
        verify(inputFileServiceMock, times(1)).readFile(anyString());
        verify(inputValidationServiceMock, times(1)).doFirstChecks(anyList());
        verify(outputFileServiceMock, times(1)).writeOutputFile(any(Game.class), any());
    }
}
