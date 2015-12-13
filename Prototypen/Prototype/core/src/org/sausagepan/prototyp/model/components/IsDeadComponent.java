package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by philipp on 9.11.15.
 * IdComponent used as an identifier for all characters
 */
public class IsDeadComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    // time when the character died
	public final long deathTime;
	// how long the character is marked as dead
    public final long deathLength;
    public boolean deathAcknowledged;

    /* ........................................................................... CONSTRUCTOR .. */
    public IsDeadComponent(long deathTime, long deathLength) {
        this.deathTime = deathTime;
        this.deathLength = deathLength;
        this.deathAcknowledged = false;
    }
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
