package it.polimi.se2018.list_event.event_received_by_view.event_from_model;

import it.polimi.se2018.model.dice.DiceStack;

/**
 * Extends EventView, updates a single dice Stack of the RoundTrack
 *
 * @author Luca Genoni
 * @author Matteo Formentin
 */
public class UpdateSingleTurnRoundTrack extends EventViewFromModel {
    private int indexRound;
    private DiceStack roundDice;

    public UpdateSingleTurnRoundTrack(int indexRound, DiceStack roundDice) {
        this.indexRound = indexRound;
        this.roundDice = roundDice;
    }

    public int getIndexRound() {
        return indexRound;
    }

    public DiceStack getRoundDice() {
        return roundDice;
    }

    public void acceptModelEvent(ViewModelVisitor visitor) {
        visitor.visit(this);
    }

}