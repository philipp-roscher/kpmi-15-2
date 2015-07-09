package org.sausagepan.prototyp.input;

import com.badlogic.gdx.InputAdapter;

/**
 * Created by Georg on 06.07.2015.
 */
public class PlayerInputAdapter extends InputAdapter{

    /* ................................................................................................ ATTRIBUTES .. */

    /* .............................................................................................. CONSTRUCTORS .. */

    /* ................................................................................................... METHODS .. */

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
/* ......................................................................................... GETTERS & SETTERS .. */

}
