package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by georg on 01.11.15.
 */
public class StatusSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private boolean debug = true;
    private Batch batch;
    private BitmapFont font;

    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public StatusSystem(Batch batch) {
        this.batch = batch;
        this.font = new BitmapFont();
        font.getData().setScale(0.1f, 0.1f);
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                HealthComponent.class,
                DynamicBodyComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            // TODO
        }
        if(debug) draw();
    }

    public void draw() {
        batch.begin();
        for (Entity entity : entities) {
            if (entity.getComponent(WeaponComponent.class) != null) {
                HealthComponent health = hm.get(entity);
                DynamicBodyComponent body = dm.get(entity);
                font.draw(batch, Integer.toString(health.HP),
                        body.dynamicBody.getPosition().x,
                        body.dynamicBody.getPosition().y);
            }
        }
        batch.end();
    }
}
