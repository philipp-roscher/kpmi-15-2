package org.sausagepan.prototyp.model.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by georg on 12.11.15.
 */
public abstract class Item {
    /* ............................................................................ ATTRIBUTES .. */
    private TextureRegion itemImg;
    /* ........................................................................... CONSTRUCTOR .. */
    public Item(TextureRegion itemImg) {
        this.itemImg = itemImg;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
