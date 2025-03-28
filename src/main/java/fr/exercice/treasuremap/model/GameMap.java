package fr.exercice.treasuremap.model;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GameMap {
    @PositiveOrZero
    private int width;
    @PositiveOrZero
    private int height;
}
