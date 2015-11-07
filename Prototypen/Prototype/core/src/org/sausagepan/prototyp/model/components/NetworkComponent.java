package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.esotericsoftware.kryonet.Client;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.AttackRequest;

/**
 * Created by philipp on 06.11.15.
 */
public class NetworkComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Client client;
    public PositionUpdate posUpdate;
    public int id;

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public void sendPositionUpdate() {
    	client.sendUDP(posUpdate);
    }
    
    public void attack() {
    	client.sendUDP(new AttackRequest(id, false));
    }

	public void stopAttacking() {
    	client.sendUDP(new AttackRequest(id, true));		
	}
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
