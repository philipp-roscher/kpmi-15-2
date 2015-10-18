package org.sausagepan.prototyp.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import org.sausagepan.prototyp.enums.PlayerAction;
import org.sausagepan.prototyp.model.ContainerMessage;
import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.model.PlayerAttributeContainer;
import org.sausagepan.prototyp.model.components.PlayerComponent;
import org.sausagepan.prototyp.view.InMaze;

/**
 * Created by Georg on 06.07.2015.
 */
public class PlayerInputProcessor implements InputProcessor {

    /* ................................................................................................ ATTRIBUTES .. */

    Player  player;
    Vector3 touchPos;
    Camera  camera;

    /* .............................................................................................. CONSTRUCTORS .. */

    public PlayerInputProcessor(Player player, Camera camera) {
        this.player   = player;
        this.touchPos = new Vector3(0,0,0);
        this.camera   = camera;
    }

    /* ................................................................................................... METHODS .. */

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);
        player.input.move(touchPos);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        player.input.stop();
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touchPos.x = screenX;
        touchPos.y = screenY;
        camera.unproject(touchPos);
        player.input.move(touchPos);
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

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.A: {
                player.input.attack();
//                maze.battleSys.updateAttack(player, maze.playerMan.getPlayers());
                player.notifyPlayerObservers(PlayerAction.ATTACK);
                break;
            }
            case Input.Keys.S: {
                player.input.shoot();
//                maze.battleSys.updateAttack(player, maze.playerMan.getPlayers());
                break;
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode) {
            case Input.Keys.A: {
                player.getBattle().stopAttacking();
                player.notifyPlayerObservers(PlayerAction.ATTACK_STOP);
                break;
            }
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


/* ......................................................................................... GETTERS & SETTERS .. */

}

