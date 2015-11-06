package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.esotericsoftware.kryonet.Client;
import org.sausagepan.prototyp.network.Network.PositionUpdate;

/**
 * Created by philipp on 06.11.15.
 */
public class NetworkComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Client client;
    public PositionUpdate posUpdate;

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public void sendPositionUpdate() {
    	client.sendUDP(posUpdate);
    }
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
