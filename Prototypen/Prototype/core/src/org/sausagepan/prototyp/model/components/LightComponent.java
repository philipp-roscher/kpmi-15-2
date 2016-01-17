package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.model.CollisionFilter;

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
        this(rayHandler, 0, 0, new Color(1,1,1,1), 256, 8);
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
        spriteLight.setContactFilter(CollisionFilter.CATEGORY_LIGHT, (short)0, CollisionFilter.MASK_LIGHT);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
