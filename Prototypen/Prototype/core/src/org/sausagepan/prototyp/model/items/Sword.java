package org.sausagepan.prototyp.model.items;

import org.sausagepan.prototyp.enums.Damagetype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by georg on 02.11.15.
 */
public class Sword extends WeaponItem {
    /* ............................................................................ ATTRIBUTES .. */
    public Rectangle damageArea;
    /* ........................................................................... CONSTRUCTOR .. */
    public Sword(TextureRegion region, int strength, Damagetype damagetype, long cooldown, String name) {
        super(region, strength, damagetype, cooldown, name);
        this.damageArea = new Rectangle(0, 0, .5f, .5f);
    }
    
    public Sword(TextureRegion region, int strength, Damagetype damagetype, String name) {
    	// default cooldown: 300 ms
        this(region, strength, damagetype, 300, name);
    }
    /* ............................................................................... METHODS .. */
    public boolean checkHit(Rectangle hittableArea) {
        return this.damageArea.overlaps(hittableArea);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
