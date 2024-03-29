package org.sausagepan.prototyp.model.entities;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

/**
 * Created by georg on 28.10.15.
 */
public class CharacterEntity extends Entity {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    /**
     * Returns the entity {@link Family} of CharacterEntities, telling you which
     * {@link com.badlogic.ashley.core.Component}s a MonsterEntity should contain.
     * @return
     */

    @SuppressWarnings("unchecked")
    public static Family getFamily() {
        return Family.all(
                DynamicBodyComponent.class,
                CharacterSpriteComponent.class,
                WeaponComponent.class,
                InputComponent.class,
                HealthComponent.class,
                MagicComponent.class,
                InventoryComponent.class,
                TeamComponent.class
        ).get();
    }
}
