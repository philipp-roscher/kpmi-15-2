package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.enums.Direction;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;


/**
 * Created by georg on 28.10.15.
 */
public class InputComponent implements Component {

    public Direction direction;
    public boolean moving;
    public Vector3 touchPos;
    public boolean weaponDrawn;

    public InputComponent() {
        direction = Direction.SOUTH;
        moving = false;
        this.touchPos = new Vector3(0,0,0);
        this.weaponDrawn = false;
    }


}
