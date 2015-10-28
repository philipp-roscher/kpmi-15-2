package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import org.sausagepan.prototyp.enums.Direction;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkTransmissionComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Vector2 position = new Vector2(0,0);
    public Direction direction = Direction.SOUTH;
    public boolean moving = false;

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
