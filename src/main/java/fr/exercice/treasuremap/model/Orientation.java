package fr.exercice.treasuremap.model;

import lombok.Getter;

/**
 * L'orientation de l'aventurier porte plusieurs informations :
 * La lettre correspondant au point cardinal
 * Un array de deux entiers permettant de calculer les changements de position des aventuriers
 * Une valeur d'angle permettant de calculer les changements d'orientation des aventuriers
 */
@Getter
public enum Orientation {
    N(new int[]{0, -1}, 0),
    S(new int[]{0, 1}, 180),
    E(new int[]{1, 0}, 90),
    O(new int[]{-1, 0}, 270);

    private final int[] coordinates;
    private final int angle;

    Orientation(int[] coordinates, int angle) {
        this.coordinates = coordinates;
        this.angle = angle;
    }

    public static Orientation getOrientationWithAngle(int angle) {
        return switch (angle) {
            case 0, 360 -> N;
            case 180 -> S;
            case 90 -> E;
            case 270, -90 -> O;
            default -> null;
        };
    }
}
