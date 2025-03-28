package fr.exercice.treasuremap.controller;

import fr.exercice.treasuremap.service.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Ceci est un test d'intégration partiel qui mock seulement le service InputValidationService pour pouvoir lui passer librement des chemins de fichier comme s'ils étaient saisis dans la console.
 */
@ExtendWith(MockitoExtension.class)
class GameControllerTestIT {
    @Mock
    private InputFileService inputFileServiceMock;

    private final InputValidationService inputValidationService = new InputValidationService();
    private final GameAssetsCreatorService gameAssetsCreatorService = new GameAssetsCreatorService();
    private final MoveAdventurerService moveAdventurerService = new MoveAdventurerService();
    private final OutputFileService outputFileService = new OutputFileService();
    private GameController gameController;

    @BeforeEach
    void setUp() {
        gameController = new GameController(inputFileServiceMock, inputValidationService, gameAssetsCreatorService, moveAdventurerService, outputFileService);
    }

    @ParameterizedTest
    @CsvSource({("game1.txt, game1_result.txt, game1_result_compare.txt"),
            ("game2.txt, game2_result.txt, game2_result_compare.txt"),
            ("game2_shuffled.txt, game2_shuffled_result.txt, game2_result_compare.txt"),
            ("game3.txt, game3_result.txt, game3_result_compare.txt"),
            ("game4.txt, game4_result.txt, game4_result_compare.txt")})
    void playGame_shouldResultFileExistAndAlwaysBeTheSame_ok(String testGamefileName, String expectedResultFileName, String compareResultFile) throws IOException {
        String testDir = Paths.get("", "src", "test", "java", "resources", "testFiles").toAbsolutePath().toString();
        String filepath = Paths.get(testDir, testGamefileName).toAbsolutePath().toString();
        Path resultFilePath = Paths.get(testDir, expectedResultFileName).toAbsolutePath();
        Path resultCompareFilePath = Paths.get(testDir, compareResultFile).toAbsolutePath();


        when(inputFileServiceMock.getAndCheckInputFilePath()).thenReturn(filepath);
        when(inputFileServiceMock.readFile(anyString())).thenCallRealMethod();
        when(inputFileServiceMock.getCleanSplitElements(anyString())).thenCallRealMethod();

        gameController.playGame();

        try (Stream<Path> fileList = Files.list(Paths.get(filepath).getParent())) {
            assertTrue(fileList.anyMatch(file -> Files.exists(resultFilePath)));
        }

        assertTrue(IOUtils.contentEquals(new FileInputStream(resultFilePath.toFile()), new FileInputStream(resultCompareFilePath.toFile())));
    }
}

