package it.polimi.se2018;

import it.polimi.se2018.alternative_network.newserver.ServerController2;
import it.polimi.se2018.exception.GameException;
import it.polimi.se2018.exception.gameboard_exception.CurrentPlayerException;
import it.polimi.se2018.exception.gameboard_exception.GameIsBlockedException;
import it.polimi.se2018.exception.gameboard_exception.GameIsOverException;
import it.polimi.se2018.list_event.event_received_by_controller.EventController;
import it.polimi.se2018.list_event.event_received_by_view.EventView;
import it.polimi.se2018.model.GameBoard;
import it.polimi.se2018.model.UpdaterView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestGameBoard {
    private static final GameException valueR = new GameException(null);
    private static final CurrentPlayerException noRightPlayer = new CurrentPlayerException();
    private static final GameIsOverException gameOver = new GameIsOverException();
    private static final GameIsBlockedException gameBlock = new GameIsBlockedException();

    private ServerController2 gameRoom;
    private int j;

    @Before
    public void initGameBoard() {
        gameRoom = new ServerController2() {
            @Override
            public void sendEventToGameRoom(EventController eventController){}
            @Override
            public void sendEventToView(EventView eventView){}
        };
    }

    @Test
    public void testNextPlayer() throws GameException {
        GameBoard gameBoard = new GameBoard(4);
        UpdaterView updaterView = new UpdaterView(gameBoard, null, gameRoom);
        gameBoard.startGame(updaterView);
        //testa fino al 9° round
        int numberPlayer= 4;
        int maxRound=10;
        for (int round = 0; round < numberPlayer; round++) {
            //senso orario
            j = round % numberPlayer;
            for (int i = 0; i < numberPlayer; i++) {
                if (j < 0) j = j + numberPlayer;
                else j = j % numberPlayer;
                assertThrows(noRightPlayer.getClass(), () -> gameBoard.nextPlayer(j-1));
                assertThrows(noRightPlayer.getClass(), () -> gameBoard.nextPlayer(j+1));
                gameBoard.nextPlayer(j++);
            }
            //senso antiorario
            j = ((numberPlayer-1) +round) %numberPlayer;
            for (int i = 0; i < numberPlayer; i++) {
                if (j < 0) j = numberPlayer-1;
                else j = j % numberPlayer;
                assertThrows(noRightPlayer.getClass(), () -> gameBoard.nextPlayer(j-1));
                assertThrows(noRightPlayer.getClass(), () -> gameBoard.nextPlayer(j+1));
                if(round==maxRound-1 && i==numberPlayer-1) {
                    assertThrows(gameOver.getClass(), () -> gameBoard.nextPlayer(j));
                    assertThrows(gameBlock.getClass(), () -> gameBoard.nextPlayer(j+1));
                }
                else gameBoard.nextPlayer(j--);
            }
        }
        gameBoard.calculateAllPoint();
    }
    @Test
    public void tryWalkGameBoard2() throws GameException {
        GameBoard gameBoard = new GameBoard(4);
        UpdaterView updaterView = new UpdaterView(gameBoard, null, gameRoom);
        assertThrows(NullPointerException.class,()->gameBoard.calculateAllPoint());
    }
    @After
    public void cleanUp(){
        gameRoom =null;
    }
}
