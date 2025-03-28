package fr.exercice.treasuremap.service;

import fr.exercice.treasuremap.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static fr.exercice.treasuremap.model.Orientation.getOrientationWithAngle;

/**
 * Service gérant les mouvements de l'aventurier : s'il peut avancer ou non, s'il change d'orientation ou s'il ramasse un trésor
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MoveAdventurerService {

    private static final String NEW_POS_ADVENTURER = "Nouvelle position de {}: {}, {} et orientation : {}";

    /**
     * Vérifie si l'aventurier {@link Adventurer} a terminé ses mouvements et change son état isDoneMoving le cas échéant.
     * On considère qu'il a fini de bouger lorsque la valeur du tour dépasse le nombre de mouvements de l'aventurier.
     *
     * @param adventurer L'aventurier qui s'apprête à jouer son tour
     * @param turn       Le tour en train d'être joué
     */
    public void checkIfAdventurerIsDoneMoving(Adventurer adventurer, int turn) {
        if (!adventurer.isDoneMoving() && turn >= adventurer.getMoves().length()) {
            log.info("L'aventurier {} a terminé tous ses mouvements et possède {} trésor(s)", adventurer.getName(), adventurer.getNbTreasure());
            adventurer.setDoneMoving(true);
        }
    }

    /**
     * Gestionnaire des mouvements de l'aventurier en train de jouer son tour.
     * Il va gérer les interactions suivant s'il avance, rencontre une montagne ou un autre aventurier, ramasse un trésor ou change d'orientation.
     *
     * @param game       Le jeu en cours avec tous ses assets (montagnes, trésors, aventuriers)
     * @param adventurer L'aventurier en train de jouer son mouvement
     * @param turn       Le tour en cours
     */
    public void playMove(Game game, Adventurer adventurer, int turn) {
        String move = String.valueOf(adventurer.getMoves().charAt(turn));
        if (StringUtils.equalsIgnoreCase(move, "A")) {
            int newPosX = adventurer.getPosX() + adventurer.getOrientation().getCoordinates()[0];
            int newPosY = adventurer.getPosY() + adventurer.getOrientation().getCoordinates()[1];
            if (isMovePossible(game, newPosX, newPosY, adventurer.getName())) {
                moveForward(game, adventurer, newPosX, newPosY);
            }
        } else {
            setOrientation(move, adventurer);
        }
    }

    private void setOrientation(String move, Adventurer adventurer) {
        if (StringUtils.equalsIgnoreCase(move, "D")) {
            adventurer.setOrientation(getOrientationWithAngle(adventurer.getOrientation().getAngle() + 90));
        } else adventurer.setOrientation(getOrientationWithAngle(adventurer.getOrientation().getAngle() - 90));
        log.info(NEW_POS_ADVENTURER, adventurer.getName(), adventurer.getPosX(), adventurer.getPosY(), adventurer.getOrientation());
    }

    /**
     * Pour que l'aventurier puisse avancer, trois vérifications doivent être faites :
     * <li>La nouvelle position ne doit pas mener hors des dimensions de la carte {@link GameMap}</li>
     * <li>La nouvelle position ne doit pas mener sur une case {@link Cell} occupée par une montagne de {@link CellType} MOUTAIN</li>
     * <li>La nouvelle position ne doit pas mener sur une case occupée par un aventurier</li>
     *
     * @param game    Le jeu en cours avec tous ses assets (montagnes, trésors, aventuriers)
     * @param newPosX L'abscisse X visée par le prochain mouvement de l'aventurier
     * @param newPosY L'ordonnée Y visée par le prochain mouvement de l'aventurier
     * @param name    Le nom de l'aventurier utilisé dans le logging
     * @return Si l'aventurier peut avancer
     */
    private boolean isMovePossible(Game game, int newPosX, int newPosY, String name) {
        return (isWithinMap(game, newPosX, newPosY, name)
                && !isMountainCell(game, newPosX, newPosY, name)
                && !isAdventurerPresent(game, newPosX, newPosY, name));
    }

    private void moveForward(Game game, Adventurer adventurer, int newPosX, int newPosY) {
        adventurer.setPosX(newPosX).setPosY(newPosY);
        if (isTreasureCell(game, newPosX, newPosY, adventurer)) {
            log.info("Nouveau trésor pour {} ! L'aventurier possède désormais {} trésor(s)", adventurer.getName(), adventurer.getNbTreasure());
        }
        log.info(NEW_POS_ADVENTURER, adventurer.getName(), adventurer.getPosX(), adventurer.getPosY(), adventurer.getOrientation());
    }

    private boolean isWithinMap(Game game, int newPosX, int newPosY, String name) {
        boolean isOutOfMap = newPosX > game.getGameMap().getWidth() || newPosY > game.getGameMap().getHeight();
        boolean areNegativeCoordinates = newPosX < 0 || newPosY < 0;
        if (isOutOfMap || areNegativeCoordinates) {
            log.info("Le mouvement de l'aventurier {} sort de la carte, il ne bouge pas sur ce tour", name);
            return false;
        }
        return true;
    }

    private boolean isTreasureCell(Game game, int newPosX, int newPosY, Adventurer adventurer) {
        for (Cell treasure : game.getTreasureCells()) {
            if (treasure.getNbTreasure() == 0) return false;
            if (treasure.getPosX() == newPosX && treasure.getPosY() == newPosY) {
                adventurer.setNbTreasure(adventurer.getNbTreasure() + 1);
                treasure.setNbTreasure(treasure.getNbTreasure() - 1);
                return true;
            }
        }
        return false;
    }

    private boolean isMountainCell(Game game, int newPosX, int newPosY, String name) {
        for (Cell mountain : game.getMountainCells()) {
            if (mountain.getPosX() == newPosX && mountain.getPosY() == newPosY) {
                log.info("Il y a une montagne sur cette case, l'aventurier {} ne bouge pas sur ce tour", name);
                return true;
            }
        }
        return false;
    }

    private boolean isAdventurerPresent(Game game, int newPosX, int newPosY, String name) {
        for (Adventurer adventurer : game.getAdventurers()) {
            if (adventurer.getPosX() == newPosX && adventurer.getPosY() == newPosY) {
                log.info("Il y a déjà un aventurier sur cette case, l'aventurier {} ne bouge pas sur ce tour", name);
                return true;
            }
        }
        return false;
    }

}
