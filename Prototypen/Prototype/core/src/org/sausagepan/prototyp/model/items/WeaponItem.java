package org.sausagepan.prototyp.model.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.graphics.EntitySprite;

/**
 * Created by georg on 02.11.15.
 */
public abstract class  WeaponItem {
    /* ............................................................................ ATTRIBUTES .. */
    public EntitySprite sprite;
    public boolean justUsed;
    public int strength;
    public Damagetype damagetype;
    /* ........................................................................... CONSTRUCTOR .. */

    public WeaponItem(TextureRegion region, int strength, Damagetype damagetype) {
        this.sprite = new EntitySprite(region);
        this.sprite.setSize(
                UnitConverter.pixelsToMeters(this.sprite.getRegionWidth()),
                UnitConverter.pixelsToMeters(this.sprite.getRegionHeight())
        );
        this.sprite.setOriginCenter();
        this.sprite.visible = false;
        this.justUsed = false;
        this.damagetype = Damagetype.PHYSICAL;
        this.strength = strength;
        this.damagetype = damagetype;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
