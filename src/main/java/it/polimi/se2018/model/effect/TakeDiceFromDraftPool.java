package it.polimi.se2018.model.effect;

import it.polimi.se2018.exception.GameException;
import it.polimi.se2018.list_event.event_received_by_view.EventView;
import it.polimi.se2018.model.GameBoard;
import it.polimi.se2018.model.dice.Dice;

public class TakeDiceFromDraftPool extends EffectGame {

    private int indexDiceOfDicePool;

    public TakeDiceFromDraftPool(int indexDiceOfDicePool) {
        this.indexDiceOfDicePool = indexDiceOfDicePool;
    }

    @Override
    public void doEffect(GameBoard gameBoard, int idPlayer) throws GameException {
        gameBoard.addNewDiceToHandFromDicePool(idPlayer, indexDiceOfDicePool);
    }

    @Override
    public void undo(GameBoard gameBoard, int idPlayer) throws GameException {
        Dice dice = gameBoard.getPlayer(idPlayer).getHandDice().remove(0);
        gameBoard.getDicePool().add(indexDiceOfDicePool, dice);
    }

    @Override
    public EventView askTheViewTheInfo() {
        return null;
    }
}
