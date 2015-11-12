package org.sausagepan.prototyp.model.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by georg on 12.11.15.
 */
public class PotionMP extends Item {
    /* ............................................................................ ATTRIBUTES .. */
    public int strength;
    /* ........................................................................... CONSTRUCTOR .. */
    public PotionMP(TextureRegion textureRegion, int strength) {
        super(textureRegion);
        this.strength = strength;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
