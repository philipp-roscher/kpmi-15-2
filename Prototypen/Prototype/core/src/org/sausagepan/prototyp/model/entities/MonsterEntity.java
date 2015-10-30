package org.sausagepan.prototyp.model.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by georg on 30.10.15.
 */
public class MonsterEntity extends Entity {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */

    /**
     * Returns the entity {@link Family} of MonsterEntities, telling you which
     * {@link com.badlogic.ashley.core.Component}s a MonsterEntity should contain.
     * @return
     */
    public static Family getFamiliy() {
        return Family.all(
                DynamicBodyComponent.class,
                SpriteComponent.class,
                HealthComponent.class
        ).get();
    }
}
