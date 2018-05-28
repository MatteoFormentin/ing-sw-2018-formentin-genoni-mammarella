package it.polimi.se2018.network.server.rmi;

import it.polimi.se2018.event.list_event.EventView;
import it.polimi.se2018.network.client.rmi.IRMIClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Class used to permit remote method invocation.
 * CLIENT -> SERVER
 *
 * The interface that define the class to export must:
 * Extends Remote Interface
 * Be Public
 * Every method must start the RemoteException
 *
 * @author DavideMammarella
 */
public interface IRMIServer extends Remote {
    //------------------------------------------------------------------------------------------------------------------
    // METHOD CALLED FROM CLIENT - REQUEST TO SERVER
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Remote method used to login.
     *
     * @param nickname name of the player.
     * @param iRMIClient client associated to the player.
     * @return username that define the player.
     */
    boolean login(String nickname, IRMIClient iRMIClient) throws RemoteException;

    /**
     * Remote method used to send an object to the Server with request to set off an event.
     *
     * @param nickname name of the player.
     * @param eventView object that will use the server to set off the event associated.
     */
    void sendEvent(String nickname, EventView eventView) throws RemoteException;
}
