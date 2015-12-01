package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.model.components.LightComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import box2dLight.RayHandler;

/**
 * Created by georg on 30.10.15.
 */
public class LightSystem extends ObservingEntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private ComponentMapper<LightComponent> lights = ComponentMapper.getFor(LightComponent.class);

    private RayHandler rayHandler;

    /* ........................................................................... CONSTRUCTOR .. */
    public LightSystem(RayHandler rayHandler) {
        this.rayHandler = rayHandler;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(LightComponent.class).get());
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
