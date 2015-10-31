package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by georg on 31.10.15.
 */
public class VisualDebuggingSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private boolean debug = true;

    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public VisualDebuggingSystem(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.one(
                WeaponComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            // TODO
        }
        if(debug) draw();
    }

    public void draw() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Entity entity : entities) {
            if(entity.getComponent(WeaponComponent.class) != null) {
                WeaponComponent weapon = wm.get(entity);
                shapeRenderer.rect(
                        weapon.damageArea.x,
                        weapon.damageArea.y,
                        weapon.damageArea.width,
                        weapon.damageArea.height
                );
            }
        }
        shapeRenderer.end();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
