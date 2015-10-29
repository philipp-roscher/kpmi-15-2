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
public class InputComponent implements Component, InputProcessor {

    public Direction direction;
    public boolean moving;
    public Vector3 touchPos;
    private Viewport viewport;
    public boolean attacking;

    public InputComponent(Viewport viewport) {
        direction = Direction.SOUTH;
        moving = false;
        Gdx.input.setInputProcessor(this);
        this.touchPos = new Vector3(0,0,0);
        this.viewport = viewport;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.UP: direction = Direction.NORTH;break;
            case Input.Keys.LEFT: direction = Direction.WEST;break;
            case Input.Keys.RIGHT: direction = Direction.EAST;break;
            case Input.Keys.DOWN: direction = Direction.SOUTH;break;
            case Input.Keys.A: attacking = true;break;
            default:break;
        }
        if(keycode != Input.Keys.A) moving = true;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.A) attacking = false;

        /* Keyboard Input */
//        if(Gdx.input.isKeyPressed(Input.Keys.UP) ||
//                Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
//                Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
//                Gdx.input.isKeyPressed(Input.Keys.RIGHT))
//            return false;
//        else moving = false;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.touchPos.x = screenX;
        this.touchPos.y = screenY;
        this.viewport.unproject(touchPos);
        this.moving = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.moving = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        this.touchPos.x = screenX;
        this.touchPos.y = screenY;
        this.viewport.unproject(touchPos);
        this.moving = true;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
