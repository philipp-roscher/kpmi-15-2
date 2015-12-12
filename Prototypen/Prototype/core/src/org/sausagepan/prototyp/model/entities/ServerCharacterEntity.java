package org.sausagepan.prototyp.model.entities;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.components.CharacterClassComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by philipp on 30.11.15.
 */
public class ServerCharacterEntity extends Entity {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
	public MapCharacterObject createClientInformation() {
		Vector2 position = this.getComponent(DynamicBodyComponent.class).dynamicBody.getPosition();
		CharacterClass characterClass = this.getComponent(CharacterClassComponent.class).characterClass;
		int teamId = this.getComponent(TeamComponent.class).TeamId;
		int health = this.getComponent(HealthComponent.class).HP;
		boolean[] ownKeys = this.getComponent(InventoryComponent.class).ownKeys;
		return new MapCharacterObject(position, characterClass, teamId, health, ownKeys);
	}
	
    /* ..................................................................... GETTERS & SETTERS .. */
    /**
     * Returns the entity {@link Family} of ServerCharacterEntities, telling you which
     * {@link com.badlogic.ashley.core.Component}s a ServerCharacterEntity should contain.
     * @return
     */
    public static Family getFamily() {
        return Family.all(
                DynamicBodyComponent.class,
                WeaponComponent.class,
                InputComponent.class,
                HealthComponent.class,
                MagicComponent.class,
                InventoryComponent.class,
                TeamComponent.class
        ).get();
    }

}
