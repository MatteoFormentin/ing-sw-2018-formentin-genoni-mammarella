package it.polimi.se2018.network.client.rmi;

import it.polimi.se2018.event.list_event.EventView;
import it.polimi.se2018.network.client.AbstractClient;
import it.polimi.se2018.network.client.ClientController;
import it.polimi.se2018.network.server.rmi.IRMIServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class based on the Abstract Factory Design Pattern.
 * The class define the RMI client for every single game.
 * Extends AbstractClient class in order to implement and facilitate Client connection.
 *
 * @author DavideMammarella
 */
public class RMIClient extends AbstractClient implements IRMIClient {

    //Interfaccia client dell'RMI, viene utilizzata per permettere l'invocazione remota di metodi tramite server
    private IRMIServer iRMIServer;

    // Username del giocatore
    private String nickname;

    //------------------------------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Instantiate one RMIClient.
     *
     * @param clientController client interface, used as
     *                         controller to communicate with the client.
     * @param serverIpAddress server address.
     * @param serverPort port used from server to communicate.
     */
    public RMIClient(ClientController clientController, String serverIpAddress, int serverPort){
        super(clientController, serverIpAddress, serverPort);
    }

    //------------------------------------------------------------------------------------------------------------------
    // CONNECTION
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Estabilish a connection with the RMI Registry (on the server).
     */
    // SISTEMA EXCEPTION
    public void connectToServer(){
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(getServerIpAddress(), getServerPort());
            iRMIServer = (IRMIServer) registry.lookup("IRMIServer");
            UnicastRemoteObject.exportObject(this, 0);
            System.out.println("RMI Client Connected!");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // METHOD CALLED FROM CLIENT - REQUEST TO THE SERVER
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Log the user to the RMI Server with the nickname.
     * The Username will be added to the map.
     *
     * @param nickname name used for the player.
     */
    @Override
    public void login(String nickname){
        try {
            iRMIServer.login(nickname, this);
        } catch (RemoteException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send to the Server the request to unleash an event.
     *
     * @param eventView object that will use the server to unleash the event associated.
     */
    public void sendEvent(EventView eventView){
        try{
            iRMIServer.sendEvent(this.nickname,eventView);
        } catch (RemoteException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // METHOD CALLED FROM SERVER - REQUEST TO THE CLIENT
    // NOTIFY
    //------------------------------------------------------------------------------------------------------------------

    /*
    @Override
    public void notify(EventUpdate eventUpdate){
        getClientController().update(eventUpdate);
    }
    */

}
