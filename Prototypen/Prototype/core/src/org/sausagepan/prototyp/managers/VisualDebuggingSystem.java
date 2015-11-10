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
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

import org.sausagepan.prototyp.model.Bullet;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.Sword;

/**
 * Created by georg on 31.10.15.
 */
public class VisualDebuggingSystem extends ObservingEntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private boolean debug = true;
    private Maze maze;

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
            OrthographicCamera camera,
            Maze maze) {
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
        this.maze = maze;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) {
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

//        drawMazeColliders();

        for (Entity entity : entities) {
            if(entity.getComponent(WeaponComponent.class) != null) {
                WeaponComponent weapon = wm.get(entity);
                if(weapon.weapon.getClass().equals(Sword.class)) {
                    shapeRenderer.rect(
                            ((Sword)weapon.weapon).damageArea.x,
                            ((Sword)weapon.weapon).damageArea.y,
                            ((Sword)weapon.weapon).damageArea.width,
                            ((Sword)weapon.weapon).damageArea.height
                    );
                }
                if(weapon.weapon.getClass().equals(Bow.class))
                    for(Bullet a : ((Bow)weapon.weapon).activeArrows)
                        shapeRenderer.rect(a.x, a.y, .1f, .1f);
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

    public void drawMazeColliders() {
        // Colliders
        MapObjects mo = maze.getColliders();
        Rectangle r;
        for(MapObject m : mo) {
            r = ((RectangleMapObject) m).getRectangle();
            shapeRenderer.rect(r.x/32f, r.y/32f, r.width/32f, r.height/32f);
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
