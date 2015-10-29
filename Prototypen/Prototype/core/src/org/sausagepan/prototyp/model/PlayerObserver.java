package org.sausagepan.prototyp.model;

import org.sausagepan.prototyp.enums.PlayerAction;

@Deprecated
/**
 * Created by georg on 18.10.15.
 *
 * Objects that observe players actions for transmitting events over network and so on
 */
public interface PlayerObserver {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */

    /* ............................................................................... METHODS .. */
    public void update(Player observedPlayer, PlayerAction action);

    /* ..................................................................... GETTERS & SETTERS .. */
}
