package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;

import org.sausagepan.prototyp.model.items.Item;

/**
 * Created by georg on 12.11.15.
 */
public class ItemComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Item item;
    /* ........................................................................... CONSTRUCTOR .. */
    public ItemComponent(Item item) {
        this.item = item;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
