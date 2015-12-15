package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by georg on 15.12.15.
 */
public class ChaseComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Body body;
    /* ........................................................................... CONSTRUCTOR .. */

    public ChaseComponent(Body body) {
        this.body = body;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
