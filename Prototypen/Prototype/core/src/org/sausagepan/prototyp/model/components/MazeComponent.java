package org.sausagepan.prototyp.model.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Georg on 14.07.2015.
 */
public abstract class MazeComponent {

    /* ................................................................................................ ATTRIBUTES .. */

    /* .............................................................................................. CONSTRUCTORS .. */

    /* ................................................................................................... METHODS .. */

    public abstract void update(float elapsedTime);

    public abstract void render(SpriteBatch batch);


    /* ......................................................................................... GETTERS & SETTERS .. */

}
