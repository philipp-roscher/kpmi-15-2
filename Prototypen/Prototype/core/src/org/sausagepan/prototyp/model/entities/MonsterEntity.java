package org.sausagepan.prototyp.model.entities;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.components.CharacterClassComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by georg on 30.10.15.
 */
public class MonsterEntity extends Entity {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
	public MapMonsterObject createClientInformation() {
		Vector2 position = this.getComponent(DynamicBodyComponent.class).dynamicBody.getPosition();
		CharacterClass characterClass = this.getComponent(CharacterClassComponent.class).characterClass;
		int health = this.getComponent(HealthComponent.class).HP;
		return new MapMonsterObject(position, characterClass, health);
	}
	
    /* ..................................................................... GETTERS & SETTERS .. */

    /**
     * Returns the entity {@link Family} of MonsterEntities, telling you which
     * {@link com.badlogic.ashley.core.Component}s a MonsterEntity should contain.
     * @return
     */
    public static Family getFamily() {
        return EntityFamilies.monsterFamily;
    }
}
