package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by georg on 21.10.15.
 */
public class MovementSystem extends EntitySystem implements EntityListener {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;
    private World world;
    /* ........................................................................... CONSTRUCTOR .. */


    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);

    public MovementSystem(World world) {this.world = world;}

    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family
                .all(DynamicBodyComponent.class)
                .exclude(InventoryComponent.class).get());
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            body.dynamicBody.setLinearVelocity(
                    MathUtils.sin(elapsedTime),
                    MathUtils.cos(elapsedTime));
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        addedToEngine(this.getEngine());
    }

    @Override
    public void entityRemoved(Entity entity) {
        world.destroyBody(pm.get(entity).dynamicBody);
        addedToEngine(this.getEngine());
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
