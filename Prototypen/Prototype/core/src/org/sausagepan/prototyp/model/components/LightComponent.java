package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * Created by georg on 28.10.15.
 */
public class LightComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public PointLight spriteLight;
    /* ........................................................................... CONSTRUCTOR .. */
    public LightComponent(RayHandler rayHandler) {
        spriteLight = new PointLight(rayHandler, 256, new Color(1,1,1,1), 8, 0, 0);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
