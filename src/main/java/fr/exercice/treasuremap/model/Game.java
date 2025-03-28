package fr.exercice.treasuremap.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Game {
    @NotBlank
    private GameMap gameMap;
    private List<Cell> mountainCells = new ArrayList<>();
    private List<Cell> treasureCells = new ArrayList<>();
    private List<Adventurer> adventurers = new ArrayList<>();
}
