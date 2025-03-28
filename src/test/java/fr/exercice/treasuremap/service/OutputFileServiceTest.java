package fr.exercice.treasuremap.service;

import fr.exercice.treasuremap.model.Game;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static fr.exercice.treasuremap.utils.TestUtils.generateGame;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OutputFileServiceTest {

    private final OutputFileService outputFileService = new OutputFileService();

    @Test
    void writeOutputFile_ok() throws IOException {
        Game game = generateGame();
        // En supposant que le chemin du fichier d'entrée initial était src/test/java/resources/testFiles
        Path testBaseDir = Paths.get("", "src", "test", "java", "resources", "testFiles").toAbsolutePath();

        String inputTestFileName = "inputTest";
        String expectedOutputFileName = inputTestFileName + "_result";
        String fileExtension = ".txt";
        Path inputFilePath = Paths.get(testBaseDir.toString(), inputTestFileName + fileExtension).toAbsolutePath();

        outputFileService.writeOutputFile(game, inputFilePath);

        // On s'attend à ce que le fichier de sortie soit composé du nom du fichier d'entrée + "_result" et dans le même répertoire.
        Path expectedOutputPath = Paths.get("", "src", "test", "java", "resources", "testFiles", expectedOutputFileName + fileExtension).toAbsolutePath();
        try (Stream<Path> fileList = Files.list(inputFilePath.getParent())) {
            assertTrue(fileList.anyMatch(file -> Files.exists(expectedOutputPath)));
        }

        List<String> outputLines = Files.readAllLines(expectedOutputPath, StandardCharsets.UTF_8);
        List<String> expectedLines = Arrays.asList("C - 4 - 3", "M - 1 - 0", "T - 1 - 3 - 2", "A - Lara - 1 - 1 - S - 0");
        assertLinesMatch(expectedLines, outputLines, "Les lignes du fichier de sortie doivent correspondre aux objets contenus dans le Game entré en paramètre");
    }
}
