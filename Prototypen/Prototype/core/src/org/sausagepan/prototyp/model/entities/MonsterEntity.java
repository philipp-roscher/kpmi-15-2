package org.sausagepan.prototyp.model.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

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
    public static Family getFamily() {
        return EntityFamilies.monsterFamily;
    }
}
