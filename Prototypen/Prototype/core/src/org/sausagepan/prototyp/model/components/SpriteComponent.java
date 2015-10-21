package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by georg on 21.10.15.
 */
public class SpriteComponent implements Component{
    /* ............................................................................ ATTRIBUTES .. */
    public Sprite sprite;

    /* ........................................................................... CONSTRUCTOR .. */
    public SpriteComponent () {
        this.sprite = new Sprite();
        this.sprite.setSize(1, 1);
    }

    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
