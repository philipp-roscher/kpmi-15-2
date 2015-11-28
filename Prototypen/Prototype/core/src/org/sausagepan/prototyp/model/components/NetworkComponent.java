package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.HPUpdateRequest;
import org.sausagepan.prototyp.network.Network.LoseKeyRequest;
import org.sausagepan.prototyp.network.Network.ShootRequest;
import org.sausagepan.prototyp.network.Network.TakeKeyRequest;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;

/**
 * Created by philipp on 06.11.15.
 */
public class NetworkComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Client client;
    public int id;

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */    
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

	public void loseKey(int keySection, float x, float y) {
		System.out.println("LOSEKEY");
		client.sendTCP(new LoseKeyRequest(id,keySection,x,y));
	}

	public void takeKey(int keySection) {
		client.sendTCP(new TakeKeyRequest(id,keySection));
	}
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
