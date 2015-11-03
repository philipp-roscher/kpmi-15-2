package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.graphics.EntitySprite;
import org.sausagepan.prototyp.model.items.WeaponItem;

/**
 * Created by georg on 22.10.15.
 */
public class WeaponComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public EntitySprite sprite;
    public Rectangle damageArea;
    public boolean justUsed;
    public int strength;
    public Damagetype damagetype;

    public WeaponItem weapon;   // the weapon itself

    /* ........................................................................... CONSTRUCTOR .. */
    public WeaponComponent(TextureRegion textureRegion, WeaponItem weapon) {
        this.sprite = new EntitySprite(textureRegion);
        this.sprite.setSize(1, 1);
        this.sprite.setOriginCenter();
        this.sprite.visible = false;
        this.damageArea = new Rectangle(0, 0, .5f, .5f);
        this.justUsed = false;
        this.strength = 5;
        this.damagetype = Damagetype.PHYSICAL;
        this.weapon = weapon;
    }

    /* ............................................................................... METHODS .. */

    /* ..................................................................... GETTERS & SETTERS .. */
}
