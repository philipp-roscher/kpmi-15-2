package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.VelocityComponent;
import org.sausagepan.prototyp.model.entities.MonsterEntity;

/**
 * Processes all {@link org.sausagepan.prototyp.model.entities.MonsterEntity}s.
 * Created by georg on 30.10.15.
 */
public class MonsterSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;

    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<SpriteComponent> sm
            = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);

    /* ........................................................................... CONSTRUCTOR .. */
    public MonsterSystem() {}
    /* ............................................................................... METHODS .. */

    /**
     * Fetches all available {@link org.sausagepan.prototyp.model.entities.MonsterEntity}s from the
     * {@link Engine}.
     * @param engine
     */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(MonsterEntity.getFamiliy());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            SpriteComponent sprite = sm.get(entity);
            HealthComponent health = hm.get(entity);
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}