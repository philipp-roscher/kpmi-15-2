package org.sausagepan.prototyp.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import org.sausagepan.prototyp.KPMIPrototype;
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
        player.setMoving(true);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        player.setMoving(false);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        maze.camera.unproject(touchPos);
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.A: {
                player.attack();
                maze.battleSys.updateAttack(player, maze.playerMan.getPlayers());
                break;
            }
            case Input.Keys.S: {
                player.attack();
                maze.battleSys.updateAttack(player, maze.playerMan.getPlayers());
                break;
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

/* ......................................................................................... GETTERS & SETTERS .. */

}
