package org.sausagepan.prototyp.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.sausagepan.prototyp.managers.MediaManager;

/**
 * Created by georg on 11.11.15.
 */
public class InGameUIRenderer {
    /* ............................................................................ ATTRIBUTES .. */
    private OrthographicCamera camera;
    private MediaManager media;
    private SpriteBatch batch;
    /* ........................................................................... CONSTRUCTOR .. */

    public InGameUIRenderer(MediaManager media, SpriteBatch batch) {
        this.media = media;
        this.batch = batch;
    }
    /* ............................................................................... METHODS .. */
    public void draw() {

    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
