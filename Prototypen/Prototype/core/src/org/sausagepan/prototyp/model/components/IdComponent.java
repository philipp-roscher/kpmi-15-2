package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by philipp on 9.11.15.
 * IdComponent used as an identifier for all characters
 */
public class IdComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public final int id;

    /* ........................................................................... CONSTRUCTOR .. */
    public IdComponent(int id) {
        this.id = id;
    }
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
