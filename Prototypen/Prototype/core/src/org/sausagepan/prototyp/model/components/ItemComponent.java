package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.items.Item;

import com.badlogic.ashley.core.Component;

/**
 * Created by georg on 12.11.15.
 */
public class ItemComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Item item;
    public ItemType type;
    public int value;
    /* ........................................................................... CONSTRUCTOR .. */
    public ItemComponent(Item item, ItemType type, int value) {
        this.item = item;
        this.type = type;
        this.value = value;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
