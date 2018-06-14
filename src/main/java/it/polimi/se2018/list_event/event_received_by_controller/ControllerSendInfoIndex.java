package it.polimi.se2018.list_event.event_received_by_controller;

/**
 * Extends EventController, the controller receives the selected window's coordinates
 *
 * @author Luca Genoni
 * @author Matteo Formentin
 */
public class ControllerSendInfoIndex extends EventController {
    private int[] arrayIndex;

    public void accept(ControllerVisitor visitor) {
        visitor.visit(this);
    }

}
