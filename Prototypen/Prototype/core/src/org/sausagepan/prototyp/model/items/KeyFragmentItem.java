package org.sausagepan.prototyp.model.items;

import org.sausagepan.prototyp.enums.ItemType;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by georg on 12.11.15.
 */
public class KeyFragmentItem extends Item {
    /* ............................................................................ ATTRIBUTES .. */
    public int keyFragmentNr;
    /* ........................................................................... CONSTRUCTOR .. */
    public KeyFragmentItem(TextureRegion keyTexture, int nr) {
        super(keyTexture, ItemType.KEY);
        this.keyFragmentNr = nr;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
