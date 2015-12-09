package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by philipp on 9.11.15.
 * IdComponent used as an identifier for all characters
 */
public class IsDeadComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public final long deathTime;

    /* ........................................................................... CONSTRUCTOR .. */
    public IsDeadComponent(long deathTime) {
        this.deathTime = deathTime;
    };
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
