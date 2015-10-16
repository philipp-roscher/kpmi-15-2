package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.model.ContainerMessage;
import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.model.PlayerAttributeContainer;

import java.util.Observer;

/**
 * Created by Georg on 14.07.2015.
 */
public abstract class PlayerComponent {

    /* ............................................................................ ATTRIBUTES .. */
    protected final PlayerAttributeContainer attributes;


    /* .......................................................................... CONSTRUCTORS .. */
    public PlayerComponent(PlayerAttributeContainer attributes) {
        this.attributes = attributes;
    }


    /* ............................................................................... METHODS .. */

    public abstract void update(float elapsedTime);

    /**
     * Inform component about changes of a given attribute and react to it
     * @param message
     */
    public abstract void update(ContainerMessage message);

    /* ..................................................................... GETTERS & SETTERS .. */

}
