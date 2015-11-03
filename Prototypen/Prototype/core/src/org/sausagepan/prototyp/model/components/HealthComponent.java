package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by georg on 29.10.15.
 * HP HealthPoints Component, for all Characters and living Entities
 */
public class HealthComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public final int initialHP;
    public int HP;
    /* ........................................................................... CONSTRUCTOR .. */
    public HealthComponent(int HP) {
        this.HP = HP;
        this.initialHP = HP;
    };
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
