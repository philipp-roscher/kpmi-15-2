package org.sausagepan.prototyp.model.items;

import org.sausagepan.prototyp.enums.ItemType;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * A bag can contain several other items, characters who die lose all their items at once in a bag
 * Created by georg on 12.11.15.
 */
public class Bag extends Item {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<Item> items;
    /* ........................................................................... CONSTRUCTOR .. */
    public Bag(TextureRegion textureRegion, Array<Item> items) {
        super(textureRegion, ItemType.BAG);
        this.items = items;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
