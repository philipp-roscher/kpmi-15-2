package org.sausagepan.prototyp.model.entities;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.components.CharacterClassComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.items.Item;
import org.sausagepan.prototyp.model.items.MapItem;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by georg on 30.10.15.
 */
public class ItemEntity extends Entity {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
	public MapItem createClientInformation() {
		Rectangle area = this.getComponent(InjurableAreaComponent.class).area;
		ItemComponent item = this.getComponent(ItemComponent.class);
		Vector2 position = new Vector2(area.x, area.y);
		ItemType type = item.type;
		int value = item.value;
		return new MapItem(position, type, value);
	}
	
    /* ..................................................................... GETTERS & SETTERS .. */

    /**
     * Returns the entity {@link Family} of MonsterEntities, telling you which
     * {@link com.badlogic.ashley.core.Component}s a MonsterEntity should contain.
     * @return
     */
    public static Family getFamily() {
        return EntityFamilies.itemFamily;
    }
}
