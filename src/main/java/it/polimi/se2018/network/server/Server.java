package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Controller;
import it.polimi.se2018.event.list_event.EventView;
import it.polimi.se2018.network.RemotePlayer;
import it.polimi.se2018.network.server.rmi.RMIServer;
import it.polimi.se2018.network.server.socket.SocketServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Class based on the Abstract Factory Design Pattern.
 * This class define the server side of the game.
 * This class implements ServerController to have basic methods for RMI and Socket Server.
 * This class is used like a Room on the server, it include one Game (Controller) and multiple players.
 *
 * @author DavideMammarella
 */
public class Server implements ServerController {

    //Porta su cui si appoggierà la comunicazione Socket
    public static final int SOCKET_PORT = 16180;
    //Porta su cui si appoggierà la comunicazione RMI
    public static final int RMI_PORT = 31415;

    // Socket Server
    private SocketServer socketServer;
    // RMI Server
    private RMIServer rmiServer;

    //MUTEX usato per gestire un login alla volta (senza questo potrebbe crearsi congestione durante il login)
    private static final Object PLAYERS_MUTEX = new Object();
    // NUM MINIMO DI GIOCATORI PER PARTITA
    public static final int minPlayers = 2;
    // NUM MASSIMO DI GIOCATORI PER PARTITA
    public static final int maxPlayers = 4;
    // CONTATORE STANZA
    private static int roomCounter = 0;
    //GIOCATORI NELLA STANZA
    private final ArrayList<RemotePlayer> players;
    ServerController serverController;
    boolean flag = true;
    // GAME DELLA ROOM
    private Controller game;
    // TIME
    private long timeout;

    //STATO STANZA
    private boolean roomJoinable;


    //------------------------------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Server Constructor.
     */
    // ORA SOLO RMI, MANCA EXCEPTION
    public Server() {
        rmiServer = new RMIServer(this);
        //socketServer = new SocketServer(this);
        roomJoinable = true;
        roomCounter++;
        players = new ArrayList<RemotePlayer>();
        //roomStartTimeout upload
        try {
            // LOAD FROM PROPERTIES
            Properties configProperties = new Properties();

            String config = "src/main/java/it/polimi/se2018/resources/configurations/gameroom_configuration.properties";
            FileInputStream input = new FileInputStream(config);

            configProperties.load(input);
            //*1000 per convertire in millisecondi
            timeout = Long.parseLong(configProperties.getProperty("roomStartTimeout")) * 1000;
            System.out.println(configProperties.getProperty("roomStartTimeout"));
        } catch (IOException e) {
            System.out.println("Sorry, file can't be found... Using default");
            timeout = 120 * 1000;
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // SERVER STARTER
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Starter for the server.
     * This method start the server and put it listen on the RMI port.
     *
     * @param args parameters used for the connection.
     */
    // ORA SOLO RMI, MANCA EXCEPTION
    public static void main(String[] args) {
        int rmiPort = RMI_PORT;
        //int socketPort = SOCKET_PORT;

        try {
            Server server = new Server();
            server.startServer(rmiPort);
            //server.startServer(socketPort, rmiPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Put the server on listen.
     * The server will connect only with the technology selected from client.
     *
     * @param rmiPort port used on RMI connection.
     */
    // int socketPort
    // socketServer.StartServer (socketPort)
    public void startServer(int rmiPort) throws Exception {
        System.out.println("RMI Server started...");
        rmiServer.startServer(rmiPort);
    }

    //------------------------------------------------------------------------------------------------------------------
    // GAME STARTER
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Start the thread timer.
     * Used as maximum time for each player to join the room.
     */
    public void startTimer() {
        System.out.println("Timeout started...");
        new Thread(new Timer(timeout, this)).start();

    }

    /**
     * Starter for the game.
     * This method start the game and close the room.
     */
    public void startGame() {
        System.out.println("Starting game...");
        game = new Controller(this);
        System.out.println("Closing Room...");
        roomJoinable = false;
    }

    //------------------------------------------------------------------------------------------------------------------
    // METHOD CALLED FROM CLIENT - REQUEST TO THE SERVER
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Log the user to the Server with the username.
     *
     * @param remotePlayer reference to RMI or Socket Player
     * @return true if the user is logged, false otherwise (logged only if nickname doesn't exists)
     */
    //CONSIDERA IL CASO DEL RI LOGIN ELSE IF
    @Override
    public boolean login(RemotePlayer remotePlayer) {
        synchronized (PLAYERS_MUTEX) {
            if (roomJoinable && !checkPlayerNicknameExists(remotePlayer.getNickname())) {
                players.add(remotePlayer);
                System.out.println("Player added...");
                if (players.size() == minPlayers) {
                    // FAI PARTIRE IL TEMPO DI ATTESA
                    startTimer();
                } else if (players.size() == maxPlayers) {
                    startGame();
                }
                return true;
            } else {
                System.out.println("player esiste");
                return false; //game is complete or nickname already exists
            }

        }
    }

    /**
     * Send to the server the request to unleash an event.
     *
     * @param eventView object that will use the server to unleash the event associated.
     */
    @Override
    public void sendEventToController(EventView eventView) {

    }

    //------------------------------------------------------------------------------------------------------------------
    // METHOD FOR SUPPORT (GET, SET, CHECK)
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Checker for player.
     * This method check if exist a player with the submitted nickname.
     *
     * @param nickname name used for the player.
     * @return true if the nickname already exists, false otherwise.
     */
    private boolean checkPlayerNicknameExists(String nickname) {
        for (RemotePlayer player : players) {
            if (player.getNickname().equals(nickname)) {
                return true;
            }

        }
        return false;

    }
}
