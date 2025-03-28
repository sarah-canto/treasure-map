package fr.exercice.treasuremap.model;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Cell {
    @PositiveOrZero
    private int posX;
    @PositiveOrZero
    private int posY;
    @PositiveOrZero
    private int nbTreasure = 0;
    private CellType type;
}
