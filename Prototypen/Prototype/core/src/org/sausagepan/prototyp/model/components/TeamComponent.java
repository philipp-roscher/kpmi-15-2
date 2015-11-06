package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by sara on 4.11.15.
 * TeamComponent, for all Characters and living Entities
 */
public class TeamComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public final int TeamId;

    /* ........................................................................... CONSTRUCTOR .. */
    public TeamComponent(int Id) {
        this.TeamId = Id;
    };
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
