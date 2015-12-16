package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkTransmissionComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public boolean attack = false;
    public boolean stopAttacking = false;
    public boolean shoot = false;
    public MonsterSpawnComponent monster = null;

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
