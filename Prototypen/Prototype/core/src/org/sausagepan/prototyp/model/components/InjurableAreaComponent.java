package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Defines the area in which a character or monster can be hit, must get synchronized with dynamic
 * body
 * Created by georg on 01.11.15.
 */
public class InjurableAreaComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Rectangle area;
    /* ........................................................................... CONSTRUCTOR .. */
    public InjurableAreaComponent(float x, float y, float w, float h) {
        area = new Rectangle(x,y,w,h);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
