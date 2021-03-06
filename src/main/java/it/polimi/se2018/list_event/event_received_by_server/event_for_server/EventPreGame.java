package it.polimi.se2018.list_event.event_received_by_server.event_for_server;

import it.polimi.se2018.list_event.event_received_by_server.EventServer;
import it.polimi.se2018.list_event.event_received_by_server.ServerVisitor;

public class EventPreGame extends EventServer {

    @Override
    public void acceptGeneric(ServerVisitor viewVisitor) {
        viewVisitor.visit(this);
    }

    //ha bisogno dell'overriding
    public void acceptPreGame(EventPreGameVisitor visitor) {
    }

}
