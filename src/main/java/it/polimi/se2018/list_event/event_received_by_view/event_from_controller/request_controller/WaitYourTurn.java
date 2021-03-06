package it.polimi.se2018.list_event.event_received_by_view.event_from_controller.request_controller;

import it.polimi.se2018.list_event.event_received_by_view.event_from_controller.EventClientFromController;
import it.polimi.se2018.list_event.event_received_by_view.event_from_controller.ViewControllerVisitor;

/**
 * Extends EventClient, tells the view which player is playing
 *
 * @author Luca Genoni
 * @author Matteo Formentin
 */
public class WaitYourTurn extends EventClientFromController {
    private int indexCurrentPlayer;

    public WaitYourTurn(int indexCurrentPlayer) {
        this.indexCurrentPlayer = indexCurrentPlayer;
    }

    public int getIndexCurrentPlayer() {
        return indexCurrentPlayer;
    }

    @Override
    public void acceptControllerEvent(ViewControllerVisitor visitor) {
        visitor.visit(this);
    }
}
