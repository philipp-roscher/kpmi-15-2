package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.sausagepan.prototyp.enums.Direction;


/**
 * Created by georg on 28.10.15.
 */
public class InputComponent implements Component {

    public Direction direction;
    public boolean moving;
    public Vector3 touchPos;
    private Viewport viewport;
    public boolean attacking;

    public InputComponent() {
        direction = Direction.SOUTH;
        moving = false;
        this.touchPos = new Vector3(0,0,0);
    }


}
