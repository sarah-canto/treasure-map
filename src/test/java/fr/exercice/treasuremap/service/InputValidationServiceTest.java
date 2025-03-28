package fr.exercice.treasuremap.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class InputValidationServiceTest {

    private final InputValidationService inputValidationService = new InputValidationService();

    //On utilise la vraie méthode getCleanSplitElements du service pour les lignes vérifiées afin de garantir que les tests fonctionnent toujours si elle venait à changer.
    private final InputFileService inputFileService = new InputFileService();

    @ParameterizedTest
    @MethodSource("inputsOk")
    void doFirstCheck_ok(List<String> inputLines) {
        assertDoesNotThrow(() -> inputValidationService.doFirstChecks(inputLines));
    }

    @ParameterizedTest
    @MethodSource("firstChecks_fail")
    void doFirstCheck_notOk(List<String> inputLines, String errorMessage) {
        Exception exception = assertThrows(IllegalStateException.class, () -> inputValidationService.doFirstChecks(inputLines));

        assertThat(exception.getMessage()).contains(errorMessage);
    }

    private static Stream<Arguments> firstChecks_fail() {
        List<String> inputs1 = List.of("C - 5 - 8");
        List<String> inputs2 = List.of("T - 5 - 8");
        List<String> inputs3 = new ArrayList<>();
        List<String> inputs4 = Arrays.asList("M - 1 - 1", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Indiana - 1 - 1 - S - AADADA");
        List<String> inputs5 = Arrays.asList("C - 3", "M - 1 - 1", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", " - Indiana - 1 - 1 - S - AADADA");
        List<String> inputs6 = Arrays.asList("C - 3 - 4 -", "M - 1 - 1", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1");
        return Stream.of(
                Arguments.of(inputs1, "Le fichier est incomplet, impossible de créer le jeu. Il doit contenir au moins une ligne pour la carte et une pour un aventurier"),
                Arguments.of(inputs2, "Le fichier est incomplet, impossible de créer le jeu. Il doit contenir au moins une ligne pour la carte et une pour un aventurier"),
                Arguments.of(inputs3, "Le fichier est incomplet, impossible de créer le jeu. Il doit contenir au moins une ligne pour la carte et une pour un aventurier"),
                Arguments.of(inputs4, "Le fichier est mal formaté, impossible de créer le jeu. Il doit au moins commencer par C et sa dernière ligne par A."),
                Arguments.of(inputs5, "Le fichier est mal formaté, impossible de créer le jeu. Il doit au moins commencer par C et sa dernière ligne par A."),
                Arguments.of(inputs6, "Le fichier est mal formaté, impossible de créer le jeu. Il doit au moins commencer par C et sa dernière ligne par A.")
        );
    }

    @ParameterizedTest
    @MethodSource("checkInputs_whenProcessingMapLine_NotOk")
    void checkMapLine_whenWrongInputs_throwsIllegalArgumentException(String line, String expectedExceptionMessage) {
        String[] split = inputFileService.getCleanSplitElements(line);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> inputValidationService.checkMapLine(line, split));

        assertThat(exception.getMessage()).contains(expectedExceptionMessage);
    }

    @ParameterizedTest
    @MethodSource("checkInputs_whenProcessingMountainLine_NotOk")
    void checkMountainLine_whenWrongInputs_throwsIllegalArgumentException(String line, String expectedExceptionMessage) {
        setMapDimensions(15, 25);
        String[] split = inputFileService.getCleanSplitElements(line);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> inputValidationService.checkMountainLine(line, split));

        assertThat(exception.getMessage()).contains(expectedExceptionMessage);
    }

    @ParameterizedTest
    @MethodSource("checkInputs_whenProcessingTreasureLine_NotOk")
    void checkTreasureLine_whenWrongInputs_throwsIllegalArgumentException(String line, String expectedExceptionMessage) {
        setMapDimensions(18, 31);
        String[] split = inputFileService.getCleanSplitElements(line);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> inputValidationService.checkTreasureLine(line, split));

        assertThat(exception.getMessage()).contains(expectedExceptionMessage);
    }

    @ParameterizedTest
    @MethodSource("checkInputs_whenProcessingAdventurerLine_NotOk")
    void checkAdventurerLine_whenWrongInputs_throwsIllegalArgumentException(String line, String expectedExceptionMessage) {
        setMapDimensions(7, 8);
        String[] split = inputFileService.getCleanSplitElements(line);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> inputValidationService.checkAdventurerLine(line, split));

        assertThat(exception.getMessage()).contains(expectedExceptionMessage);
    }

    private static Stream<Arguments> inputsOk() {
        List<String> inputs1 = Arrays.asList("C - 3 - 4", "M - 1 - 1", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Indiana - 1 - 1 - S - AADADA");
        List<String> inputs2 = Arrays.asList(" C - 5 - 8", "M - 4 - 7", "M - 1 - 3", "M - 4 - 2", "T - 0 - 3 -  7   ", "A - Dakota - 4 - 1 - O - AAGADADGA", "A - Wisconsin - 1 - 1 - S - GADADGAAA");
        List<String> inputs3 = Arrays.asList("C - 9 - 4", "T - 0 - 3 - 2", "T -   6 - 1 - 1", "t - 8 - 2 - 3", "A - Mississippi - 1 - 1 - S - AAADAAAAADAGGGGDAG", "A - Ohio - 7 - 3 - E - AADADA", "A - Illinois - 3 - 3 - N - AADADAG");
        List<String> inputs4 = Arrays.asList("C - 21 - 54", "   M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "a - Iowa - 1 - 1 - S - GGADADDAADADA", "A - Minnesota - 1 - 1 - E - GGADADDAAA");
        List<String> inputs5 = Arrays.asList("    C - 21 - 54", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Maine - 1 - 1 - S - GGADADDAADADA", "A - Texas - 1 - 1 - E - GGADADDAAA");
        List<String> inputs6 = Arrays.asList("c - 61 - 84   ", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Utah - 1 - 1 - S - GGADADDAADADA", "A - Minnesota - 1 - 1 - E - GGADADDAAA");
        List<String> inputs7 = Arrays.asList("C - 95 - 432   ", "M - 2 - 2   ", "  T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Alabama - 1 - 1 - N - GGADADDAADADA", "A - Washington - 1 - 1 - E - ADADDAAA");
        //Tests sur les cas limites :
        List<String> littlestMap = Arrays.asList("C - 1 - 1", "M - 1 - 1", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Indiana - 1 - 1 - S - AADADA");
        List<String> biggestMap1 = Arrays.asList("C - 291 - 290", "M - 1 - 1", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Indiana - 1 - 1 - S - AADADA");
        List<String> biggestMap2 = Arrays.asList("C - 290 - 291", "M - 1 - 1", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Indiana - 1 - 1 - S - AADADA");
        List<String> adventurerOnlyGoesForward = Arrays.asList("C - 290 - 291", "M - 1 - 1", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Indiana - 1 - 1 - S - AAAAAAAAA");
        List<String> adventurerOnlyTurns = Arrays.asList("C - 290 - 291", "M - 1 - 1", "M - 2 - 2", "T - 0 - 3 - 2", "T - 1 - 3 - 1", "A - Indiana - 1 - 1 - S - GDGGDDDGGDGDGGG");

        return Stream.of(
                Arguments.of(inputs1),
                Arguments.of(inputs2),
                Arguments.of(inputs3),
                Arguments.of(inputs4),
                Arguments.of(inputs5),
                Arguments.of(inputs6),
                Arguments.of(inputs7),
                Arguments.of(littlestMap),
                Arguments.of(biggestMap1),
                Arguments.of(biggestMap2),
                Arguments.of(adventurerOnlyGoesForward),
                Arguments.of(adventurerOnlyTurns)
        );
    }

    private static Stream<Arguments> checkInputs_whenProcessingMapLine_NotOk() {
        String wrongNbCharacters1 = "C - 3";
        String wrongNbCharacters2 = "  C - 3 - 4 -";
        String notNumeric1 = "C -   3 -";
        String notNumeric2 = "C - 3 - Z   ";
        String notNumeric3 = "C - 3 - &   ";
        String notNumeric4 = "C - 3 - 8z   ";
        String mapTooBig = "C - 300 - 1200";
        String notNumeric5 = "C - 37.100 - 40";
        String mapTooSmall1 = "C - 0 - 12";
        String mapTooSmall2 = "C - 3 - 0";
        return Stream.of(
                Arguments.of(wrongNbCharacters1, "La ligne de la carte ne contient pas le bon nombre de caractères"),
                Arguments.of(wrongNbCharacters2, "La ligne de la carte ne contient pas le bon nombre de caractères"),
                Arguments.of(notNumeric1, "La hauteur ou la largeur de la carte n'est pas un nombre entier"),
                Arguments.of(notNumeric2, "La hauteur ou la largeur de la carte n'est pas un nombre entier"),
                Arguments.of(notNumeric3, "La hauteur ou la largeur de la carte n'est pas un nombre entier"),
                Arguments.of(notNumeric4, "La hauteur ou la largeur de la carte n'est pas un nombre entier"),
                Arguments.of(notNumeric5, "La hauteur ou la largeur de la carte n'est pas un nombre entier"),
                Arguments.of(mapTooBig, "La carte est plus grande que le département de la Madre de Dios ! Veuillez revoir la hauteur ou la largeur de la carte"),
                Arguments.of(mapTooSmall1, "La carte a une hauteur ou une largeur inférieure à 1"),
                Arguments.of(mapTooSmall2, "La carte a une hauteur ou une largeur inférieure à 1")
        );
    }

    private static Stream<Arguments> checkInputs_whenProcessingMountainLine_NotOk() {
        String wrongNbCharacters1 = "M - 1 ";
        String wrongNbCharacters2 = "M 2 - 2";
        String notNumeric1 = "M -   3 -";
        String notNumeric2 = "M - Y - 7  ";
        String notNumeric3 = "M - 3 - &   ";
        String notNumeric4 = "M - 3 - 2l   ";
        String notNumeric5 = "M 3 - 2 -";
        String notNumeric6 = "M - 20 - 12.4";
        String outOfMap1 = "M - 30 - 120";
        return Stream.of(
                Arguments.of(wrongNbCharacters1, "La ligne d'une des montagnes ne contient pas le bon nombre de caractères"),
                Arguments.of(wrongNbCharacters2, "La ligne d'une des montagnes ne contient pas le bon nombre de caractères"),
                Arguments.of(notNumeric1, "La position d'une des montagnes n'est pas un nombre entier"),
                Arguments.of(notNumeric2, "La position d'une des montagnes n'est pas un nombre entier"),
                Arguments.of(notNumeric3, "La position d'une des montagnes n'est pas un nombre entier"),
                Arguments.of(notNumeric4, "La position d'une des montagnes n'est pas un nombre entier"),
                Arguments.of(notNumeric5, "La position d'une des montagnes n'est pas un nombre entier"),
                Arguments.of(notNumeric6, "La position d'une des montagnes n'est pas un nombre entier"),
                Arguments.of(outOfMap1, "La position d'une des montagnes sort de la taille de la carte")
        );
    }

    private static Stream<Arguments> checkInputs_whenProcessingTreasureLine_NotOk() {
        String wrongNbCharacters1 = "T - 1 -";
        String wrongNbCharacters2 = "T-2-1-";
        String wrongNbCharacters3 = "T--1";
        String notNumeric1 = "T -   3 - -3";
        String notNumeric2 = "T - Y - 7  -6";
        String notNumeric3 = "T - 3 - &   -";
        String notNumeric4 = "T - 3 - 2l   -3";
        String notNumeric5 = "T 3 - 2 --4";
        String nbTreasureNotNumeric1 = "T - 3 - 2 -";
        String nbTreasureNotNumeric2 = "T - 3 - 2 - J";
        String nbTreasureNotNumeric3 = "T - 3 - 2 - 5f";
        String nbTreasureNotNumeric4 = "T - 3 - 2 - &";
        String nbTreasureNotNumeric5 = "T - 3 - 2 - 1.5";
        String nbTreasureNotValid1 = "T - 3 - 2 - 0";
        String outOfMap1 = "T - 30 - 120 - 12";
        return Stream.of(
                Arguments.of(wrongNbCharacters1, "La ligne d'un des trésors ne contient pas le bon nombre de caractères"),
                Arguments.of(wrongNbCharacters2, "La ligne d'un des trésors ne contient pas le bon nombre de caractères"),
                Arguments.of(wrongNbCharacters3, "La ligne d'un des trésors ne contient pas le bon nombre de caractères"),
                Arguments.of(notNumeric1, "La position d'un des trésors n'est pas un nombre entier"),
                Arguments.of(notNumeric2, "La position d'un des trésors n'est pas un nombre entier"),
                Arguments.of(notNumeric3, "La position d'un des trésors n'est pas un nombre entier"),
                Arguments.of(notNumeric4, "La position d'un des trésors n'est pas un nombre entier"),
                Arguments.of(notNumeric5, "La position d'un des trésors n'est pas un nombre entier"),
                Arguments.of(nbTreasureNotNumeric1, "La quantité d'un des trésors n'est pas un nombre entier"),
                Arguments.of(nbTreasureNotNumeric2, "La quantité d'un des trésors n'est pas un nombre entier"),
                Arguments.of(nbTreasureNotNumeric3, "La quantité d'un des trésors n'est pas un nombre entier"),
                Arguments.of(nbTreasureNotNumeric4, "La quantité d'un des trésors n'est pas un nombre entier"),
                Arguments.of(nbTreasureNotNumeric5, "La quantité d'un des trésors n'est pas un nombre entier"),
                Arguments.of(nbTreasureNotValid1, "La quantité d'un des trésors est invalide. Elle doit être de 1 ou plus"),
                Arguments.of(outOfMap1, "La position d'un des trésors sort de la taille de la carte")
        );
    }

    private static Stream<Arguments> checkInputs_whenProcessingAdventurerLine_NotOk() {
        String noFieldForMoves = "A - Lara - 1 - 2 - S";
        String noPosXorY = "A - Lara - 1 - S - AADADA";
        String posXNotNumeric = "A - Lara - X - 2 - S - AADADA";
        String posYNotNumeric = "A - Lara - 1 - X - S - AADADA";
        String noName = "A -- 1 - 2 - S -A";
        String invalidOrientation1 = "A - Lara - 1 - 2 - Z - AADADA";
        String invalidOrientation2 = "A - Lara - 1 - 2 - 8 - AADADA";
        String invalidMoves1 = "A - Lara - 1 - 2 - S - AADAXA";
        String invalidMoves2 = "A - Lara - 1 - 2 - S - ";
        String outOfMap = "A - Lara - 10 - 10 - S - AADADA";

        return Stream.of(
                Arguments.of(noFieldForMoves, "La ligne d'un des aventuriers ne contient pas le bon nombre de caractères"),
                Arguments.of(noPosXorY, "La ligne d'un des aventuriers ne contient pas le bon nombre de caractères"),
                Arguments.of(posYNotNumeric, "La position d'un des aventuriers n'est pas un nombre"),
                Arguments.of(posXNotNumeric, "La position d'un des aventuriers n'est pas un nombre"),
                Arguments.of(noName, "Le nom d'un des aventuriers est vide"),
                Arguments.of(invalidOrientation1, "L'orientation d'un des aventuriers ne correspond pas à un point cardinal"),
                Arguments.of(invalidOrientation2, "L'orientation d'un des aventuriers ne correspond pas à un point cardinal"),
                Arguments.of(invalidMoves1, "L'un des mouvements d'un des aventuriers n'est pas valide"),
                Arguments.of(invalidMoves2, "L'un des mouvements d'un des aventuriers n'est pas valide"),
                Arguments.of(outOfMap, "La position d'un des aventuriers sort de la taille de la carte")
        );
    }

    // Méthode utilitaire pour redéfinir les valeurs de mapWidth et mapHeight dans la classe
    private void setMapDimensions(int width, int height) {
        try {

            Field fieldX = InputValidationService.class.getDeclaredField("mapWidth");
            Field fieldY = InputValidationService.class.getDeclaredField("mapHeight");
            fieldX.setAccessible(true);
            fieldY.setAccessible(true);
            fieldX.set(inputValidationService, width);
            fieldY.set(inputValidationService, height);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
