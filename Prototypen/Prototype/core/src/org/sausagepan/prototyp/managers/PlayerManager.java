package org.sausagepan.prototyp.managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.network.NetworkPosition;

/**
 * Created by Georg on 26.06.2015.
 */
public class PlayerManager {

    /* ................................................................................................ ATTRIBUTES .. */

    public HashMap<Integer,Player> players;


    /* .............................................................................................. CONSTRUCTORS .. */

    public PlayerManager() {
        this.players = new HashMap<Integer,Player>();
    }


    /* ................................................................................................... METHODS .. */




    /**
     * adds a player to the system
     * @param id
     * @param player
     */
    public void addCharacter(int id, Player player) {
        this.players.put(id, player);
    }

    /**
     * removes a player from the system
     * @param playerId
     */
	public void removeCharacter(int playerId) {
		this.players.remove(playerId);
	}
	
    /**
     * modifies position of currently existing player
     * @param id
     * @param position
     * @param elapsedTime
     */

	public void updatePosition(int id, NetworkPosition position, float elapsedTime) {
		if(players.get(id) != null) {
			this.players.get(id).updatePosition(position.position, position.direction, position.isMoving);
			this.players.get(id).update(elapsedTime);
		}
	}


    /* .......................................................................................... GETTERS & SETTERS . */

    public ArrayList<Player> getPlayers() {
        return new ArrayList<Player>(players.values());
    }


}
