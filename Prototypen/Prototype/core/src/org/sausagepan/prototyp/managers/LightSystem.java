package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

import box2dLight.RayHandler;
import javafx.scene.effect.Light;

/**
 * Created by georg on 30.10.15.
 */
public class LightSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private ComponentMapper<LightComponent> lights = ComponentMapper.getFor(LightComponent.class);

    private RayHandler rayHandler;

    /* ........................................................................... CONSTRUCTOR .. */
    public LightSystem(RayHandler rayHandler) {
        this.rayHandler = rayHandler;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(LightComponent.class).get());
        for(Entity e : entities) {
            LightComponent light = e.getComponent(LightComponent.class);
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}