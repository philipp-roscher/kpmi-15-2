package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
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
    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<InjurableAreaComponent> jm
            = ComponentMapper.getFor(InjurableAreaComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public VisualDebuggingSystem(
            ShapeRenderer shapeRenderer,
            OrthographicCamera camera) {
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                HealthComponent.class,
                DynamicBodyComponent.class,
                InjurableAreaComponent.class
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
        shapeRenderer.setColor(Color.WHITE);
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
            if(entity.getComponent(InjurableAreaComponent.class) != null) {
                InjurableAreaComponent area = jm.get(entity);
                shapeRenderer.rect(area.area.x,area.area.y, area.area.width, area.area.height);
            }
        }
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity entity : entities) {
            if(entity.getComponent(HealthComponent.class) != null) {
                HealthComponent health = hm.get(entity);
                DynamicBodyComponent body = dm.get(entity);
                shapeRenderer.rect(
                        body.dynamicBody.getPosition().x - .5f,
                        body.dynamicBody.getPosition().y + .7f,
                        ((float)health.HP)/health.initialHP,
                        .05f
                );
            }
        }
        shapeRenderer.end();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
