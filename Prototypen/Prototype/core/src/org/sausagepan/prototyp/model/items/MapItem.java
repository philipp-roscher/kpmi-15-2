package org.sausagepan.prototyp.model.items;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.entities.MapFactoryObject;

/**
 * Created by georg on 13.11.15.
 */
public class     MapItem extends MapFactoryObject {
    /* ............................................................................ ATTRIBUTES .. */
    public ItemType type;
    public int value;
    /* ........................................................................... CONSTRUCTOR .. */

    public MapItem(Vector2 position, String type, float value) {
        super(position);
        this.type = ItemType.POTION_HP;
        if(type.equals("key")) this.type = ItemType.KEY;
        if(type.equals("potion_hp")) this.type = ItemType.POTION_HP;
        if(type.equals("potion_mp")) this.type = ItemType.POTION_MP;
        this.value = MathUtils.round(value);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
