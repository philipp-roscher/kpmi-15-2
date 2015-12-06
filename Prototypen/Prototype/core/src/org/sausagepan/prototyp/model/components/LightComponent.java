package org.sausagepan.prototyp.model.components;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

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

    /**
     *
     * @param rayHandler
     * @param x
     * @param y
     * @param color
     * @param rays  number of rays for light calculation, less is faster
     * @param dist
     */
    public LightComponent(
            RayHandler rayHandler, float x, float y, Color color, int rays, float dist) {
        spriteLight = new PointLight(rayHandler, rays, color, dist, x, y);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
