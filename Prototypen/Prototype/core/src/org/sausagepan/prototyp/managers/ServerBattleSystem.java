package org.sausagepan.prototyp.managers;

import java.util.ArrayList;

import org.sausagepan.prototyp.model.*;
import org.sausagepan.prototyp.network.GameServer;

/**
 * Created by Georg on 26.06.2015.
 */
public class ServerBattleSystem {
	GameServer gameServer;
	
    /* ................................................................................................ ATTRIBUTES .. */

    /* .............................................................................................. CONSTRUCTORS .. */

    public ServerBattleSystem(GameServer gameServer) {
		this.gameServer = gameServer;
	}

    /* ................................................................................................... METHODS .. */

	public void attack(ServerPlayer attacker, ArrayList<ServerPlayer> characters) {
        for(ServerPlayer c : characters) {
            if(!attacker.equals(c)) {
            	System.out.println(attacker.getWeaponCollider());
            	System.out.println(c.getDamageCollider());
                if (attacker.getWeaponCollider().overlaps(c.getDamageCollider()))
                	break;
                    /*gameServer.inflictDamage(
                    		c.getId(),
                            attacker.getWeapon().getDamage()
                                    + attacker.getStatus_().getAttPhys());*/
            }
        }
    }

    public void updateBullets(ServerPlayer attacker, ArrayList<ServerPlayer> characters) {
        for(ServerPlayer c : characters) {
            if(!attacker.equals(c))
                for(Bullet b : attacker.getBullets())
                    if(b.overlaps(c.getDamageCollider())) c.getStatus_().
                            doPhysicalHarm(attacker.getWeapon().getDamage()
                            + attacker.getStatus_().getAttPhys());
        }
    }

    /* .......................................................................................... GETTERS & SETTERS . */


}
