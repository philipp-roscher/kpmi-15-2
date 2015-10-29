package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import org.sausagepan.prototyp.graphics.EntitySprite;

/**
 * Created by georg on 22.10.15.
 */
public class WeaponComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public EntitySprite sprite;
    public Rectangle damageArea;

    /* ........................................................................... CONSTRUCTOR .. */
    public WeaponComponent(TextureRegion textureRegion) {
        this.sprite = new EntitySprite(textureRegion);
        this.sprite.setSize(1,1);
        this.sprite.setOriginCenter();
        this.sprite.visible = false;
        this.damageArea = new Rectangle(0, 0, .5f, .5f);
    }

    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
