package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;

/**
 * MP Magical Points - only for magical folks like wizards and healers
 * Created by georg on 29.10.15.
 */
public class MagicComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public int MP;
    /* ........................................................................... CONSTRUCTOR .. */
    public MagicComponent(int MP) {
        this.MP = MP;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
