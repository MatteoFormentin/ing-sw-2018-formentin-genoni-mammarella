package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Controller;
import it.polimi.se2018.list_event.event_received_by_server.EventServer;
import it.polimi.se2018.list_event.event_received_by_server.ServerVisitor;
import it.polimi.se2018.list_event.event_received_by_server.event_for_game.ControllerVisitor;
import it.polimi.se2018.list_event.event_received_by_server.event_for_game.event_controller.ControllerEndTurn;
import it.polimi.se2018.list_event.event_received_by_server.event_for_game.EventController;
import it.polimi.se2018.list_event.event_received_by_server.event_for_server.EventPreGame;
import it.polimi.se2018.list_event.event_received_by_server.event_for_server.EventPreGameVisitor;
import it.polimi.se2018.list_event.event_received_by_server.event_for_server.event_pre_game.LoginRequest;
import it.polimi.se2018.list_event.event_received_by_view.EventClient;
import it.polimi.se2018.list_event.event_received_by_view.event_from_controller.request_controller.MessageError;
import it.polimi.se2018.list_event.event_received_by_view.event_from_controller.request_controller.MessageOk;
import it.polimi.se2018.list_event.event_received_by_view.event_from_controller.request_controller.StartGame;
import it.polimi.se2018.list_event.event_received_by_view.event_from_controller.request_controller.StartPlayerTurn;
import it.polimi.se2018.list_event.event_received_by_view.event_from_model.UpdatePlayerConnection;
import it.polimi.se2018.list_event.event_received_by_view.event_from_model.setup.UpdateNamePlayersDuringSetUp;
import it.polimi.se2018.model.UpdateRequestedByServer;
import it.polimi.se2018.network.RemotePlayer;
import it.polimi.se2018.network.server.rmi.RMIServer;
import it.polimi.se2018.network.server.socket.SocketServer;
import it.polimi.se2018.utils.TimerCallback;
import it.polimi.se2018.utils.TimerThread;
import org.fusesource.jansi.AnsiConsole;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Class based on the Abstract Factory Design Pattern.
 * This class define the server side of the game.
 * This class implements ServerController to have basic methods for RMI and Socket Server.
 *
 * @author DavideMammarella
 */
public class Server implements ServerController, TimerCallback, ServerVisitor, EventPreGameVisitor {

    //Porta su cui si appoggierà la comunicazione Socket
    public static int SOCKET_PORT;
    //Porta su cui si appoggierà la comunicazione RMI
    public static int RMI_PORT;

    public static String SERVER_ADDRESS;
    // NUM MINIMO DI GIOCATORI PER PARTITA
    public static final int MIN_PLAYERS = 2;
    // NUM MASSIMO DI GIOCATORI PER PARTITA
    public static final int MAX_PLAYERS = 4;
    //MUTEX usato per gestire un login alla volta (senza questo potrebbe crearsi congestione durante il login)
    private static final Object PLAYERS_MUTEX = new Object();
    //GIOCATORI NELLA STANZA
    private final ArrayList<RemotePlayer> players;
    ServerController serverController;
    boolean flag = true;
    // Socket Server
    private static SocketServer socketServer;
    // RMI Server
    private static RMIServer rmiServer;
    // GAME DELLA ROOM
    private Controller game;

    // Timeout uploaded from properties file
    private long timeout;
    // Thread for the timeout in order to fix a time for the user login
    private TimerThread timerThread;

    //STATO STANZA
    private boolean roomJoinable;
    private int playerCounter = 0;


    private boolean[] playerConnected = {false, false, false, false};
    private String[] playerNickname;


    //------------------------------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Server Constructor.
     */
    // ORA SOLO RMI, MANCA EXCEPTION
    public Server() {
        rmiServer = new RMIServer(this);
        socketServer = new SocketServer(this);

        roomJoinable = true;
        players = new ArrayList<>();

        playerNickname = new String[4];
        AnsiConsole.out.println(ansi().fg(YELLOW).a("Time setting of the room:").reset());
        try {
            // LOAD FROM PROPERTIES
            Properties configProperties = new Properties();

            String timeConfig = "src/resources/configurations/gameroom_configuration.properties";
            FileInputStream inputTime = new FileInputStream(timeConfig);

            configProperties.load(inputTime);

            // TIMEOUT LOAD
            timeout = Long.parseLong(configProperties.getProperty("roomTimeout")) * 1000; //*1000 per convertire in millisecondi
            AnsiConsole.out.println(ansi().fg(YELLOW).a("TIMEOUT : " + configProperties.getProperty("roomTimeout") + " ms").reset());
            AnsiConsole.out.println(ansi().fg(DEFAULT).a("-----------------------------------------\n").reset());
            AnsiConsole.out.println(ansi().fg(DEFAULT).a("<<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>>").reset());
        } catch (IOException e) {
            // LOAD FAILED
            System.err.println("Sorry, the configuration can't be setted! The default one will be used...");
            // Default timeout in case of exception.
            timeout = 120 * 1000;
        }

        timerThread = new TimerThread(this, this.timeout);

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

        AnsiConsole.out.println(ansi().fg(DEFAULT).a("\n-----------------------------------------").reset());
        AnsiConsole.out.println(ansi().fg(BLUE).a("Network setting of the room:"));

        try {
            // LOAD FROM PROPERTIES
            Properties configProperties = new Properties();

            String connectionConfig = "src/resources/configurations/connection_configuration.properties";
            FileInputStream inputConnection = new FileInputStream(connectionConfig);

            configProperties.load(inputConnection);

            // SERVER IP LOAD
            SERVER_ADDRESS = configProperties.getProperty("SERVER_ADDRESS");
            AnsiConsole.out.println(ansi().fg(BLUE).a("SERVER IP : " + configProperties.getProperty("SERVER_ADDRESS")).reset());

            // RMI PORT LOAD
            RMI_PORT = Integer.parseInt(configProperties.getProperty("RMI_PORT"));
            AnsiConsole.out.println(ansi().fg(BLUE).a("RMI PORT : " + configProperties.getProperty("RMI_PORT")).reset());

            // SOCKET PORT LOAD
            SOCKET_PORT = Integer.parseInt(configProperties.getProperty("SOCKET_PORT"));
            AnsiConsole.out.println(ansi().fg(BLUE).a("SOCKET PORT : " + configProperties.getProperty("SOCKET_PORT")).reset());

            AnsiConsole.out.println(ansi().fg(DEFAULT).a("-----------------------------------------").reset());

        } catch (IOException e) {
            // LOAD FAILED
            System.err.println("Sorry, the configuration can't be setted! The default one will be used...");
            // Default RMI PORT in case of exception.
            RMI_PORT = 31415;
            // Default Socket PORT in case of exception.
            SOCKET_PORT = 16180;
        }

        int rmiPort = RMI_PORT;
        int socketPort = SOCKET_PORT;

        // TODO
        try {
            Server server = new Server();
            server.startServer(rmiPort, socketPort);
        } catch (Exception e) {
            System.err.println("Server già in esecuzione!");
        }
    }

    /**
     * Put the server on listen.
     * The server will connect only with the technology selected from client (RMI or Socket).
     *
     * @param rmiPort port used on RMI connection.
     */
    public void startServer(int rmiPort, int socketPort) {
        AnsiConsole.out.println(ansi().fg(DEFAULT).a("Creating network connection:\n").reset());
        try {
            rmiServer.startServer(rmiPort);
            socketServer.startServer(socketPort);
        } catch (Exception e) {
            System.err.println("Server can't be started!\n");
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    // GAME STARTER (ONLY ONE GAME WITH MAXIMUM 4 PLAYERS)
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Starter for the game.
     */
    public void startGame() {
        AnsiConsole.out.println(ansi().fg(GREEN).a("GAME STARTED!").reset());
        AnsiConsole.out.println(ansi().fg(DEFAULT).a("From now the room will not be joinable, except from a RElogin").reset());
        roomJoinable = false;


        String[] playersName = new String[players.size()];
        int i = 0;
        for (RemotePlayer player : players) {
            playersName[i] = player.getNickname();
            i++;
        }

        game = new Controller(this, playersName, null);

        for (RemotePlayer player : players) {
            try {
                StartGame packet = new StartGame(playersName);
                packet.setPlayerId(player.getPlayerId());
                player.sendEventToView(packet);
            } catch (RemoteException ex) {
                // DISCONNESSIONE


                this.playerCounter--;
            }
        }

        //TODO se vuoi ho aggiunto questa interfaccia se chiami un metodo ritorna a te però almeno non devi aver lo sbatti di inviare pacchetti
        UpdateRequestedByServer updater = game.getUpdater();
        game.startController();
        //TODO updatePlayerConnected(int index,String name,boolean duringGame)
        // per comunicare che è stato effettuato il reLogin del giocatore
        // TODO   public void updateDisconnected(int index,String name,boolean duringGame);
        // per comunicare che la disconnessione di un giocatore

    }

    @Override
    public void timerCallback() {
        startGame();
    }

    @Override
    public void timerCallbackWithIndex(int infoToReturn) {
        throw new UnsupportedOperationException();
    }

    /**
     * Starter for the timeout, based on a single thread.
     */
    public void startTimerThread() {
        AnsiConsole.out.println(ansi().fg(GREEN).a("TIMEOUT started!").reset());
        // FACCIO PARTIRE IL THREAD
        timerThread.startThread();
    }

    //------------------------------------------------------------------------------------------------------------------
    // METHOD CALLED FROM CLIENT - REQUEST TO THE SERVER
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Remote method used to log the user to the server with his nickname.
     *
     * @param remotePlayer reference to RMI or Socket Player.     *
     */
    public void login(RemotePlayer remotePlayer) {
        synchronized (PLAYERS_MUTEX) {

            // SE LA STANZA è ACCESSIBILE (PRE-GAME)
            if (roomJoinable) {

                if (players.size() == 1 && timerThread.isAlive()) {
                    timerThread.shutdown();
                    timerThread.startThread();
                    AnsiConsole.out.println(ansi().fg(GREEN).a("TIMER STOPPED CAUSE THERE IS ONLY 1 PLAYER IN THE ROOM").reset());
                }

                AnsiConsole.out.println(ansi().fg(DEFAULT).a("Trying to log the player in the waiting room...").reset());

                // NON ESISTE PLAYER CON QUEL NICKNAME E NON è CONNESSO
                if (!checkPlayerNicknameExists(remotePlayer.getNickname())) {

                    // IMPOSTO L'ID
                    remotePlayer.setPlayerId(playerCounter);

                    // IMPOSTO LA CONNESSIONE
                    connectPlayer(remotePlayer);
                    players.add(remotePlayer);
                    playerNickname[playerCounter] = remotePlayer.getNickname();
                    playerConnected[playerCounter] = true;
                    playerCounter++;

                    // APPENA RAGGIUNGO IL NUMERO MINIMO PLAYER FACCIO PARTIRE TIMER
                    if (this.players.size() == MIN_PLAYERS) {
                        // FAI PARTIRE IL TEMPO DI ATTESA
                        startTimerThread();
                    }

                    // APPENA RAGGIUNGO IL NUMERO MASSIMO DI PLAYER FACCIO PARTIRE IL GIOCO
                    else if (this.players.size() == MAX_PLAYERS) {
                        // TERMINO THREAD SICCOME LA ROOM è PIENA
                        this.timerThread.shutdown();
                        // FACCIO PARTIRE IL PREGAME
                        //startPreGameThread(remotePlayer);
                        startGame();
                    }
                }

                // ESISTE PLAYER CON QUEL NICKNAME ED è CONNESSO
                else if (checkPlayerNicknameExists(remotePlayer.getNickname()) && checkPlayerRunning(remotePlayer.getNickname())) {
                    System.err.println("Player: " + remotePlayer.getNickname() + " already logged, use another nickname...");
                    //  return false;TODO old
                    MessageError packet = new MessageError("Player: \" + remotePlayer.getNickname() + \" already logged, use another nickname...", false, true);
                    //TODO controllare se va bene così
                    Runnable exec;
                    exec = () -> {
                        Thread.currentThread().setName("Thread Login made");
                        try {//TODO check with testing
                            Thread.sleep(20);
                            remotePlayer.sendEventToView(packet);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        } catch (RemoteException ex) {
                            System.out.println("Il giocatore " + remotePlayer.getNickname() + "Si è scollegato dal gioco mentre chiedeva la connessione al gioco");
                            ex.printStackTrace();
                        }
                        ;
                    };
                    (new Thread(exec)).start();
                }

                // ESISTE PLAYER CON QUEL NICKNAME MA NON è CONNESSO (NEL PRE-GAME)
                else if (checkPlayerNicknameExists(remotePlayer.getNickname()) && !checkPlayerRunning(remotePlayer.getNickname())) {
                    // GESTIONE EVENTO DI DISCONNESSIONE PLAYER NEL PRE-GAME

                    // PRENDO IL VECCHIO ID (SICCOME RIMANE SALVATO NELL'ARRAY)
                    int id = remotePlayer.getPlayerId();
                    String nickname = remotePlayer.getNickname();
                    AnsiConsole.out.println(ansi().fg(DEFAULT).a("Welcome, " + nickname + " I noticed your disconnection.\nI'm trying to relog you in the game...").reset());

                    // ASSEGNO UN NUOVO REMOTEPLAYER AL NICKNAME
                    //replacePlayer(id, remotePlayer);

                    // IMPOSTO LA CONNESSIONE
                    connectPlayer(remotePlayer);
                    AnsiConsole.out.println(ansi().fg(GREEN).a("Relogin made!").reset());
                    //DOPO AVER IMPOSTATO LA CONNESSIONE GLI MANDO L'OK E AGGIORNO LA LISTA DEI GIOCATORI NELLA WAIT ROOM

                    MessageOk packet = new MessageOk("Login made!", false, false);
                    UpdateNamePlayersDuringSetUp names = new UpdateNamePlayersDuringSetUp((String[]) players.toArray());
                    RemotePlayer[] tocheck = (RemotePlayer[]) players.toArray();
                    //TODO controllare se va bene così
                    Runnable exec;
                    exec = () -> {
                        Thread.currentThread().setName("Login PreGame");
                        int i = 0;
                        boolean someOnDown = true;
                        do {
                            try {//TODO check with testing
                                Thread.sleep(20);
                                // è fondamentale prima mandargli gli update e poi digli ok, inquesto modo prima si aggiorna
                                // e poi mostra il messaggio
                                tocheck[i].sendEventToView(packet);
                                for (; i < players.size(); i++) tocheck[i].sendEventToView(names);
                                tocheck[i].sendEventToView(names);
                                someOnDown = false;
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            } catch (RemoteException ex) {
                                System.out.println("Il giocatore " + players.get(i).getNickname() + "Si è scollegato dalla wait room");
                                ex.printStackTrace();
                            }
                            ;//TODO per non mettere questo try & catch progettare meglio rmi
                        } while (someOnDown);
                    };
                    (new Thread(exec)).start();
                }
           /*     //TODO controllare sulla gui/cli che vada bene così
                MessageOk packet = new MessageOk("Relogin made!", false, false);
                //TODO controllare se va bene così
                Runnable exec;
                exec = () -> {
                    Thread.currentThread().setName("Relogin Updater");
                    try {//TODO check with testing
                        Thread.sleep(30);
                        // è fondamentale prima mandargli gli update e poi digli ok, inquesto modo prima si aggiorna
                        // e poi mostra il messaggio
                        game.playerUp(remotePlayer.getPlayerId());
                        remotePlayer.sendEventToView(packet);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (RemoteException ex) {
                        System.out.println("Il giocatore " + remotePlayer.getNickname() + "Si è scollegato dal gioco");
                        ex.printStackTrace();
                    }
                    ;//TODO per non mettere questo try & catch progettare meglio rmi
                };
                (new Thread(exec)).start();*/
            }

            // SE LA STANZA NON è ACCESSIBILE (IN-GAME)
            else {
                // ESISTE IL PLAYER CON QUEL NICKNAME ED è CONNESSO
                if (nicknameExist(remotePlayer.getNickname()) && isPlayerConnected(remotePlayer.getNickname())) {
                    MessageError packet = new MessageError("", false, false);
                    //TODO controllare se va bene così
                    Runnable exec;
                    exec = () -> {
                        Thread.currentThread().setName("Sorry \" + remotePlayer.getNickname() + \" but the room is closed and your nickname is already a player in the game!");
                        try {//TODO check with testing
                            Thread.sleep(30);
                            // è fondamentale prima mandargli gli update e poi digli ok, inquesto modo prima si aggiorna
                            // e poi mostra il messaggio
                            remotePlayer.sendEventToView(packet);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        ;//TODO per non mettere questo try & catch progettare meglio rmi
                    };
                    (new Thread(exec)).start();
                    System.err.println("Sorry " + remotePlayer.getNickname() + " but the room is closed and your nickname is already a player in the game!");
                }

                // ESISTE IL PLAYER CON QUEL NICKNAME E NON è CONNESSO
                // RELOGIN
                else if (nicknameExist(remotePlayer.getNickname()) && !isPlayerConnected(remotePlayer.getNickname())) {


                    // L'ID DEL PLAYER E IL NICKNAME DEL PLAYER DEVONO RIMANERE UGUALI, DEVI SOLO CAMBIARE IL REMOTE PLAYER CON UNO NUOVO
                    String nickname = remotePlayer.getNickname();
                    int id = getIdByNickname(nickname);

                    remotePlayer.setPlayerId(id);
                    AnsiConsole.out.println(ansi().fg(DEFAULT).a("Welcome, " + nickname + " I noticed your disconnection.\nI'm trying to relog you in the game...").reset());

                    // SOSTITUZIONE IN BASE ALL'ID

                    // RE IMPOSTAZIONE DELLA CONNESSIONE (RUNNING)
                    connectPlayer(remotePlayer);

                    // RE INTEGRAZIONE NEL GIOCO
                    RemotePlayer[] toCheck = (RemotePlayer[]) players.toArray();
                    players.add(remotePlayer);
                    playerConnected[id] = true;
                    String[] names = (String[]) players.toArray();

                    //INVIO DELLA RICONNESSIONE A TUTTI I GIOCATORI GIA COLLEGATI E IL MESSAGE OK A CHI SI é RICOLLEGATO
                    UpdatePlayerConnection packet = new UpdatePlayerConnection(remotePlayer.getPlayerId(), remotePlayer.getNickname());
                    MessageOk okmessage = new MessageOk("Effettuata riconnessione", false, false);
                    Runnable exec = () -> {
                        Thread.currentThread().setName("");
                        int i = 0;
                        boolean someOnDown = true;
                        reLogPlayer(id);
                        do {
                            try {//TODO check with testing
                                Thread.sleep(20);
                                // è fondamentale prima mandargli gli update e poi digli ok, inquesto modo prima si aggiorna
                                // e poi mostra il messaggio
                                toCheck[i].sendEventToView(okmessage);
                                for (; i < players.size(); i++) toCheck[i].sendEventToView(packet);
                                someOnDown = false;
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            } catch (RemoteException ex) {
                                System.out.println("Il giocatore " + players.get(i).getNickname() + "Si è scollegato dalla wait room");
                                ex.printStackTrace();
                            }
                            ;//TODO per non mettere questo try & catch progettare meglio rmi
                        } while (someOnDown);

                    };
                    (new Thread(exec)).start();

                    //this.game.joinGame(id);
                } else {
                    System.err.println("Room is closed, " + remotePlayer.getNickname() + " can't access!");
                    MessageError error = new MessageError("Room is closed", false, true);
                    Runnable exec = () -> {
                        Thread.currentThread().setName("Thread Room si closed");
                        try {//TODO check with testing
                            Thread.sleep(20);
                            // è fondamentale prima mandargli gli update e poi digli ok, inquesto modo prima si aggiorna
                            // e poi mostra il messaggio
                            remotePlayer.sendEventToView(error);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        } catch (RemoteException ex) {
                            System.out.println("Il giocatore " + remotePlayer.getNickname() + "Si è scollegato dalla wait room");
                            ex.printStackTrace();
                        }

                    };
                    (new Thread(exec)).start();
                }
            }
        }
    }




    int getIdByNickname(String nickname) {
        for (int i = 0; i < 4; i++) {
            if (playerNickname[i].equals(nickname)) return i;
        }
        return -1;
    }

    boolean nicknameExist(String nickname) {
        for (int i = 0; i < 4; i++) {
            if (playerNickname[i].equals(nickname)) return true;
        }
        return false;
    }

    boolean isPlayerConnected(String nickname) throws IndexOutOfBoundsException {
        for (int i = 0; i < 4; i++) {
            if (playerNickname[i].equals(nickname)) return playerConnected[i];
        }
        throw new IndexOutOfBoundsException();
    }

    int getLastRunningPlayer() {
        for (int i = 0; i < 4; i++) {
            if (playerConnected[i]) return i;
        }
        return 0;
    }

    /**
     * Joiner for the game.
     *
     * @param index reference to RMI or Socket Player.
     */

    public void reLogPlayer(int index) {
        game.playerUp(index);
    }

    public void sendPlayerNickname() {
        for (RemotePlayer p : players) {
            UpdateNamePlayersDuringSetUp packet = new UpdateNamePlayersDuringSetUp(playerNickname);
            packet.setPlayerId(p.getPlayerId());
            sendEventToView(packet);
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    // METHOD CALLED FROM SERVER - REQUEST TO THE CLIENT
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Remote method used to send to the client an update of the game.
     *
     * @param eventClient object that will use the client to unleash the update associated.
     */
    @Override
    public void sendEventToView(EventClient eventClient) {
        int id = eventClient.getPlayerId();
        if (!playerConnected[id]) {
            System.err.println("PLAYER DISCONNESSO! L'EVENTO NON SARA' INVIATO");

            if (eventClient instanceof StartPlayerTurn) {
                ControllerEndTurn packet = new ControllerEndTurn();
                packet.setPlayerId(id);
                game.visit(packet);
            }
        } else {

            try {
                searchPlayerById(id).sendEventToView(eventClient);
            } catch (RemoteException ex) {
                RemotePlayer remotePlayer = searchPlayerById(eventClient.getPlayerId());
                playerDisconnect(remotePlayer);
            }
        }
    }

    /**
     * Remote method used to ping the client.
     */
    @Override
    public void ping() {
    }

    //------------------------------------------------------------------------------------------------------------------
    // SUPPORTER METHODS
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Searcher for player id in the game.
     *
     * @param id ID of the player associated to the client.
     * @return player associated to the ID.
     */
    // RITORNA IL GIOCATORE REMOTO (FAI CON QUESTO IL CHECK PER VEDERE SE IL CLIENT C'è O MENO)
    @Override
    public RemotePlayer searchPlayerById(int id) throws IndexOutOfBoundsException {
        for (RemotePlayer p : players) {
            if (p.getPlayerId() == id) return p;
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Checker for player nickname in the server.
     *
     * @param nickname name of the player associated to the client.
     * @return true if the nickname exists, false otherwise.
     */
    private boolean checkPlayerNicknameExists(String nickname) {
        String[] playersName = new String[players.size()];
        int i = 0;

        for (RemotePlayer player : players) {
            playersName[i] = player.getNickname();
            i++;
            if (player.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checker of the player associated to the client, with nickname.
     *
     * @param nickname name of the player associated to the client.
     * @return true if the nickname is associated to a client, false otherwise.
     */
    private boolean checkPlayerRunning(String nickname) {
        for (RemotePlayer player : players) {
            if (player.getPlayerConnection() == "rmi") {
                try {
                    player.ping();
                } catch (RemoteException e) {
                    player.setPlayerRunning(false);
                    player.disconnect();
                    game.playerDown(player.getPlayerId());
                    this.playerCounter--;
                }
                if (player.getNickname().equals(nickname)) {
                    return player.getPlayerRunning();
                }
            }
            if (player.getPlayerConnection() == "socket") {
                try {
                    player.sendAck();
                } catch (Exception e) {
                    playerDisconnect(player);
                }
                if (player.getNickname().equals(nickname)) {
                    return player.getPlayerRunning();
                }
            }
        }
        return false;
    }

    public void playerDisconnect(RemotePlayer player) {
        System.err.println("Player: " + player.getNickname() + " has made a disconnection!");
        player.setPlayerRunning(false);
        playerConnected[player.getPlayerId()] = false;
        player.disconnect();
        players.remove(player);
        this.playerCounter--;
        if (playerCounter > 1) game.playerDown(player.getPlayerId());
        else game.winBecauseOfDisconnection(getLastRunningPlayer());
    }


    private void replacePlayerInGame(int id, RemotePlayer newRemotePlayer) {
        if (newRemotePlayer.getPlayerConnection() == "rmi") {
            // IMPOSTO LA CONNESSIONE
            connectPlayer(newRemotePlayer);
            //players.add(id, newRemotePlayer);
            //playerCounter++;
            // RIMPIAZZO IL GIOCATORE NEL SERVER
            players.set(id, newRemotePlayer);
        }
        if (newRemotePlayer.getPlayerConnection() == "socket") {
            // RIMPIAZZO IL GIOCATORE NEL SERVER
            players.set(id, newRemotePlayer);
            // RIAPRO LA SUA CONNESSIONE SOCKET
            //newRemotePlayer.
        }

        String nickname = newRemotePlayer.getNickname();
        AnsiConsole.out.println(ansi().fg(GREEN).a("Disconnected player " + nickname + " has been replaced from a new client!").reset());
        AnsiConsole.out.println(ansi().fg(DEFAULT).a("-----------------------------------------").reset());
    }

    /**
     * Connecter for player.
     * The connecter work on player connection state flag, putting it true determining a "connection established".
     * EventPreGame supporter method.
     *
     * @param remotePlayer reference to RMI or Socket Player.
     */
    private void connectPlayer(RemotePlayer remotePlayer) {
        remotePlayer.setPlayerRunning(true);
        String nickname = remotePlayer.getNickname();
        AnsiConsole.out.println(ansi().fg(GREEN).a("Player " + nickname + " has been connected!").reset());
        AnsiConsole.out.println(ansi().fg(DEFAULT).a("-----------------------------------------\n").reset());
    }


    //******************************************** Visitor pattern ***********************************************

    @Override
    public void sendEventToController(EventServer eventController) {
        eventController.acceptGeneric(this);
    }


    @Override
    public void visit(EventController event) {
        game.sendEventToController(event);
    }

    @Override
    public void visit(EventPreGame event) {
        event.acceptGeneric(this);
    }

    @Override
    public void visit(LoginRequest event) {
        login(event.getRemotePleyer());
    }
}
