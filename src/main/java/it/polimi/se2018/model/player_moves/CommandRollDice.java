package it.polimi.se2018.model.player_moves;

import it.polimi.se2018.event.list_event.EventView;
import it.polimi.se2018.model.GameBoard;

public class CommandRollDice implements ICommandPlayerMove {
    public boolean canPerform(GameBoard gameBoard, EventView event) {
        return false;
    }

    public void doMove(GameBoard gameBoard, EventView event) {

    }
}