package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by georg on 22.10.15.
 */
public class WeaponComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Sprite sprite;

    /* ........................................................................... CONSTRUCTOR .. */
    public WeaponComponent(TextureRegion textureRegion) {
        this.sprite = new Sprite(textureRegion);
        this.sprite.setSize(1,1);
    }

    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
