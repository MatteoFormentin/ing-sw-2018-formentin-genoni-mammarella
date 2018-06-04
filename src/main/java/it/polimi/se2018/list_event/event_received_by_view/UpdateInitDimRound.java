package it.polimi.se2018.list_event.event_received_by_view;

import it.polimi.se2018.model.dice.DiceStack;

public class UpdateInitDimRound extends EventView  {
    private DiceStack[] roundTrack;

    public UpdateInitDimRound(DiceStack[] roundTrack) {
        this.roundTrack = roundTrack;
    }

    public DiceStack[] getRoundTrack() {
        return roundTrack;
    }

    public void setRoundTrack(DiceStack[] roundTrack) {
        this.roundTrack = roundTrack;
    }

    public void accept(ViewVisitor visitor) {
        visitor.visit(this);
    }

}