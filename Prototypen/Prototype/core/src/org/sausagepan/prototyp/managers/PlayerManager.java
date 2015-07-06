package org.sausagepan.prototyp.managers;

import com.badlogic.gdx.utils.Array;
import org.sausagepan.prototyp.model.Player;

/**
 * Created by Georg on 26.06.2015.
 */
public class PlayerManager {

    /* ................................................................................................ ATTRIBUTES .. */

    public Array<Player> players;


    /* .............................................................................................. CONSTRUCTORS .. */

    public PlayerManager() {
        this.players = new Array<Player>();
    }


    /* ................................................................................................... METHODS .. */




    /**
     * adds a player to the system
     * @param player
     */
    public void addCharacter(Player player) {
        this.players.add(player);
    }


    /* .......................................................................................... GETTERS & SETTERS . */

    public Array<Player> getPlayers() {
        return players;
    }
}
