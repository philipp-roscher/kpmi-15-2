package org.sausagepan.prototyp.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.view.InMaze;

/**
 * Created by Georg on 06.07.2015.
 */
public class PlayerInputAdapter extends InputAdapter{

    /* ................................................................................................ ATTRIBUTES .. */

    Player  player;
    InMaze  maze;
    Vector3 touchPos;

    /* .............................................................................................. CONSTRUCTORS .. */

    public PlayerInputAdapter(Player player, InMaze maze) {
        this.player   = player;
        this.touchPos = new Vector3(0,0,0);
        this.maze     = maze;
    }

    /* ................................................................................................... METHODS .. */

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPos.set(screenX, screenY, 0);
        maze.camera.unproject(touchPos);
        player.move(touchPos);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        player.stop();
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touchPos.x = screenX;
        touchPos.y = screenY;
        maze.camera.unproject(touchPos);
        player.move(touchPos);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.A: {
                player.attack();
//                maze.battleSys.updateAttack(player, maze.playerMan.getPlayers());
                break;
            }
            case Input.Keys.S: {
                player.shoot();
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
                player.stopAttacking();
                break;
            }
        }

        return true;
    }

/* ......................................................................................... GETTERS & SETTERS .. */

}

