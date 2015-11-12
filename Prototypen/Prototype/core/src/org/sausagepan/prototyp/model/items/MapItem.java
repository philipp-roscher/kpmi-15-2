package org.sausagepan.prototyp.model.items;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import org.sausagepan.prototyp.enums.ItemType;

/**
 * Created by georg on 13.11.15.
 */
public class MapItem {
    /* ............................................................................ ATTRIBUTES .. */
    public Vector2 position;
    public ItemType type;
    public int value;
    /* ........................................................................... CONSTRUCTOR .. */

    public MapItem(float x, float y, String type, float value) {
        this.position = new Vector2(x,y);
        this.type = ItemType.POTION_HP;
        if(type.equals("key")) this.type = ItemType.KEY;
        if(type.equals("potion_hp")) this.type = ItemType.POTION_HP;
        if(type.equals("potion_mp")) this.type = ItemType.POTION_MP;
        this.value = MathUtils.round(value);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
