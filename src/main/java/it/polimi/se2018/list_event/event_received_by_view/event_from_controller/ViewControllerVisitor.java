package it.polimi.se2018.list_event.event_received_by_view.event_from_controller;

public interface ViewControllerVisitor {
    //**********************************************from Controller**************************************
    //for the setup
    void visit(StartGame event);
    void visit(JoinGame event);

    //for the initial prt of the game
    void visit(ShowAllCards event);
    void visit(SelectInitialWindowPatternCard event);
    void visit(InitialEnded event);

    //respond messages
    void visit(MessageError event);
    void visit(MessageOk event);

    //for the turn
    void visit(StartPlayerTurn event);
    void visit(SelectCellOfWindow event);
    void visit(SelectDiceFromRoundTrack event);
    void visit(SelectDiceFromDraftPool event);
    void visit(SelectToolCard event);
    void visit(MoveTimeoutExpired event);
    void visit(WaitYourTurn event);

    //for the end game
    void visit(EndGame event);
}