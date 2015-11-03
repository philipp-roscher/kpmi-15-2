package org.sausagepan.prototyp.model.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

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
    public static Family getFamiliy() {
        return Family.all(
                DynamicBodyComponent.class,
                CharacterSpriteComponent.class,
                WeaponComponent.class,
                InputComponent.class,
                HealthComponent.class,
                MagicComponent.class,
                InventoryComponent.class
        ).get();
    }
}
