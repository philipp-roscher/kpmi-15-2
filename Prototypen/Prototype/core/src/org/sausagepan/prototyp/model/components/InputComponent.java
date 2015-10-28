package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import org.sausagepan.prototyp.enums.Direction;

import javafx.scene.input.KeyCode;

/**
 * Created by georg on 28.10.15.
 */
public class InputComponent implements Component, InputProcessor {

    public Direction direction;
    public boolean moving;

    public InputComponent() {
        direction = Direction.SOUTH;
        moving = false;
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.UP: direction = Direction.NORTH;break;
            case Input.Keys.LEFT: direction = Direction.WEST;break;
            case Input.Keys.RIGHT: direction = Direction.EAST;break;
            default: direction = Direction.SOUTH;break;
        }
        moving = true;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        moving = false;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
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
