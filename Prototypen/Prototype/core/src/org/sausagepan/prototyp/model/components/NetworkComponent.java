package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;

import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.HPUpdateRequest;
import org.sausagepan.prototyp.network.Network.ShootRequest;

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

	public void shoot(Vector2 position, Vector2 direction) {
		client.sendUDP(new ShootRequest(id,position,direction));		
	}
	
	public void sendHPUpdate(HPUpdateRequest hpupdate) {
		client.sendTCP(hpupdate);
	}
    
    /* ..................................................................... GETTERS & SETTERS .. */
}