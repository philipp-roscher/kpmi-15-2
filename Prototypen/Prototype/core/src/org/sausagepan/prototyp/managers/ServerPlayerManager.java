package org.sausagepan.prototyp.managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.model.ServerPlayer;
import org.sausagepan.prototyp.network.Position;

/**
 * Created by Georg on 26.06.2015.
 */
public class ServerPlayerManager {

    /* ................................................................................................ ATTRIBUTES .. */

    public HashMap<Integer,ServerPlayer> players;


    /* .............................................................................................. CONSTRUCTORS .. */

    public ServerPlayerManager() {
        this.players = new HashMap<Integer,ServerPlayer>();
    }


    /* ................................................................................................... METHODS .. */




    /**
     * adds a player to the system
     * @param id
     * @param player
     */
    public void addCharacter(int id, ServerPlayer player) {
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
     */

	public void updatePosition(int id, Position position) {
		if(players.get(id) != null) {
			this.players.get(id).updatePosition(position.position, position.direction, position.isMoving);
		}
	}


    /* .......................................................................................... GETTERS & SETTERS . */

    public ArrayList<ServerPlayer> getPlayers() {
        return new ArrayList<ServerPlayer>(players.values());
    }


}
