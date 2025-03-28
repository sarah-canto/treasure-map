package fr.exercice.treasuremap.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static fr.exercice.treasuremap.utils.TestUtils.writeInCommandLineRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InputFileServiceTest {

    private final InputFileService inputFileService = new InputFileService();

    @ParameterizedTest
    @ValueSource(strings = {
            "src/test/java/resources/inputFiles/game1.txt",
            "src/test/java/resources/inputFiles/game2.txt",
            "src/test/java/resources/inputFiles/game3.txt"})
    void getAndCheckInputFilePath_ok(String filePath) {
        writeInCommandLineRunner(filePath);
        String result = inputFileService.getAndCheckInputFilePath();
        assertEquals(result, filePath);
    }

    @ParameterizedTest
    @MethodSource("wrongInputFilePaths")
    void getAndCheckInputFilePath_whenExtensionIsNotTxtOrFilePathEmpty_throwsIllegalArgumentException(String filePath, Class<Exception> exceptionThrown, String errorMessage) {
        writeInCommandLineRunner(filePath);

        Exception exception = assertThrows(exceptionThrown, inputFileService::getAndCheckInputFilePath);
        assertThat(exception.getMessage()).contains(errorMessage);
    }

    private static Stream<Arguments> wrongInputFilePaths() {
        return Stream.of(
                Arguments.of(Paths.get("src/test/java/resources/inputFiles/game.pdf").toString(), IllegalArgumentException.class, "L'extension du fichier doit être '.txt', veuillez réessayer."),
                Arguments.of(Paths.get("src/test/java/resources/inputFiles/game.pdf").toString(), IllegalArgumentException.class, "L'extension du fichier doit être '.txt', veuillez réessayer."),
                Arguments.of(" ", IllegalStateException.class, "Le chemin de fichier entré est vide, veuillez réessayer.")
        );
    }

    @ParameterizedTest
    @CsvSource({
            ("src/test/java/resources/testFiles/game1.txt, 6"),
            ("src/test/java/resources/testFiles/game2.txt, 8"),
            ("src/test/java/resources/testFiles/game3.txt, 7")})
    void readFile_ok(String filePath, Integer expectedSize) throws IOException {

        assertDoesNotThrow(() -> inputFileService.readFile(filePath));
        List<String> inputLines = inputFileService.readFile(filePath);
        assertThat(inputLines).as("La liste des inputs ne doit pas être vide").isNotEmpty()
                .as("La liste des inputs n'a pas la taille attendue").hasSize(expectedSize);
    }

    @Test
    void readFile_whenFileDoesNotExist_throwsNoSuchFileException() {
        String filePath = "src/test/java/resources/inputFiles/game.txt";
        Exception exception = assertThrowsExactly(NoSuchFileException.class, () -> {
            inputFileService.readFile(filePath);
        });
        assertThat(exception.getMessage()).contains("Le fichier spécifié est introuvable : " + Paths.get(filePath));
    }

    @ParameterizedTest
    @CsvSource({
            ("M-1-3, 3"),
            ("C--2, 3"),
            ("  A-Lara-1-1-S-AADADAGGA, 6"),
            ("AaDdd aAAAgG, 1"),
            ("T-   - 7 - 9,  4")})
    void getCleanSplitElements_ok(String lineToSplit, Integer expectedSize) {
        String[] cleanSplitElements = inputFileService.getCleanSplitElements(lineToSplit);
        assertThat(cleanSplitElements).as("L'array d'éléments splittés ne doit pas être vide").isNotEmpty()
                .as("L'array d'éléments splittés n'a pas la taille attendue").hasSize(expectedSize);
        //Les éléments de l'array ne doivent pas contenir de whitespace
        assertFalse(Arrays.stream(cleanSplitElements).anyMatch(s -> s.contains(" ")));
    }
}
