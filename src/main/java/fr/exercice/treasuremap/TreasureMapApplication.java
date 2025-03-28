package fr.exercice.treasuremap;

import fr.exercice.treasuremap.controller.GameController;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@CommonsLog
@RequiredArgsConstructor
public class TreasureMapApplication implements CommandLineRunner {

    public final GameController gameController;

    public static void main(String[] args) {
        SpringApplication.run(TreasureMapApplication.class, args);
    }

    @Override
    public void run(String... filepath) {
        gameController.playGame();
    }
}