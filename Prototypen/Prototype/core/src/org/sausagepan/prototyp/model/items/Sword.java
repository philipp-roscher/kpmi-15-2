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
    public Sword(TextureRegion region, int strength, Damagetype damagetype) {
        super(region, strength, damagetype);
        this.damageArea = new Rectangle(0, 0, .5f, .5f);
    }
    /* ............................................................................... METHODS .. */
    public boolean checkHit(Rectangle hittableArea) {
        /*System.out.println(this.damageArea.overlaps(hittableArea));
        System.out.println(hittableArea);
        System.out.println(damageArea);*/
        return this.damageArea.overlaps(hittableArea);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
