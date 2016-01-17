package org.sausagepan.prototyp.model.items;

import org.sausagepan.prototyp.enums.ItemType;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by georg on 12.11.15.
 */
public abstract class Item {
    /* ............................................................................ ATTRIBUTES .. */
    public TextureRegion itemImg;
    public ItemType type;
    /* ........................................................................... CONSTRUCTOR .. */
    public Item(TextureRegion itemImg, ItemType type) {
        this.itemImg = itemImg;
        this.type = type;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
