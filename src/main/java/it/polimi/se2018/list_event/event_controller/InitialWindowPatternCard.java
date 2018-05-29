package it.polimi.se2018.list_event.event_controller;

import it.polimi.se2018.model.card.window_pattern_card.WindowPatternCard;

/**
 * Extends EventView, describe the event "end of the turn" produced by the view
 *
 * @author Luca Genoni
 */
public class InitialWindowPatternCard extends EventController {
    //from EventView private String nicknamPlayer;
    //from EventView private Model model;
    WindowPatternCard[] initialWindowPatternCard;

    public WindowPatternCard[] getInitialWindowPatternCard() {
        return initialWindowPatternCard;
    }

    public void setInitialWindowPatternCard(WindowPatternCard[] initialWindowPatternCard) {
        this.initialWindowPatternCard = initialWindowPatternCard;
    }

    @Override
    public void accept(VisitorEventFromController visitor) {
        visitor.visit(this);
    }
}
