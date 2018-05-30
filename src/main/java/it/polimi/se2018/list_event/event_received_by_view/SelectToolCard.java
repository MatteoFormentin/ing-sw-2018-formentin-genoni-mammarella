package it.polimi.se2018.list_event.event_received_by_view;

/**
 * Extends EventController, describe the event "end of the turn" produced by the view
 *
 * @author Luca Genoni
 */
public class SelectToolCard extends EventView {
    //from EventController private String nicknamPlayer;
    //from EventController private Model model;

    public void accept(ViewVisitor visitor) {
        visitor.visit(this);
    }
}