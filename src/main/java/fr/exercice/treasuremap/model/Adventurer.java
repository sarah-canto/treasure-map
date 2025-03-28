package fr.exercice.treasuremap.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Adventurer {
    @NotBlank
    private String name;
    @PositiveOrZero
    private int posX;
    @PositiveOrZero
    private int posY;
    @NotNull
    private Orientation orientation;
    @NotBlank
    private String moves;
    @PositiveOrZero
    private int nbTreasure = 0;
    private boolean isDoneMoving;
}
