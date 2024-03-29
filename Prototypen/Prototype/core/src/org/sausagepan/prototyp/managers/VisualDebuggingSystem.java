package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.model.Bullet;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.Sword;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;

import box2dLight.RayHandler;

/**
 * Created by georg on 31.10.15.
 */
public class VisualDebuggingSystem extends EntitySystem implements EntityListener {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private int teamId;
    private boolean debug = true;
    private boolean damageFeedback=false;
    private long damFeedbStartTime=0;
    private RayHandler rayHandler;

    /* ........................................................................... CONSTRUCTOR .. */
    public VisualDebuggingSystem(
            ShapeRenderer shapeRenderer,
            OrthographicCamera camera,
            RayHandler rayHandler) {
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
        this.rayHandler = rayHandler;
        this.teamId = -1;
    }
    /* ............................................................................... METHODS .. */
    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                HealthComponent.class,
                DynamicBodyComponent.class,
                InjurableAreaComponent.class
        ).get());
        
        if(teamId == -1) {
        	teamId = engine.getEntitiesFor(EntityFamilies.ownCharacterFamily).get(0)
    			.getComponent(TeamComponent.class).TeamId;
        }
    }

    public void update(float deltaTime) {
        if(debug) draw();
    }

    public void draw() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        if(GlobalSettings.DEBUGGING_ACTIVE) drawWeaponDebugger();
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        drawBattleDebugger();
        if(damageFeedback) {
        	this.rayHandler.setAmbientLight(.5f, .1f, .1f, 1);
        }
        shapeRenderer.end();


        if(TimeUtils.timeSinceMillis(damFeedbStartTime) > 100){
        	damageFeedback = false;
            this.rayHandler.setAmbientLight(.3f, .3f, .3f, 1);        	
        }
    }

    public void drawWeaponDebugger() {
        for (Entity entity : entities) {
            if(entity.getComponent(WeaponComponent.class) != null) {
                WeaponComponent weapon = CompMappers.weapon.get(entity);
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
                InjurableAreaComponent area = CompMappers.injurableArea.get(entity);
                shapeRenderer.rect(area.area.x,area.area.y, area.area.width, area.area.height);
            }
        }
    }

    public void drawBattleDebugger() {
        for (Entity entity : entities) {
            if(entity.getComponent(HealthComponent.class) != null && entity.getComponent(TeamComponent.class) != null) {
                HealthComponent health = CompMappers.health.get(entity);
                DynamicBodyComponent body = CompMappers.dynBody.get(entity);
                if (entity.getComponent(TeamComponent.class).TeamId != teamId)
                	shapeRenderer.setColor(Color.RED);
                
                shapeRenderer.rect(
                        body.dynamicBody.getPosition().x - .5f,
                        body.dynamicBody.getPosition().y + .7f,
                        ((float)health.HP)/health.initialHP,
                        .05f
                );
                if (entity.getComponent(TeamComponent.class).TeamId != teamId)
                	shapeRenderer.setColor(Color.GREEN);
                
                if(health.justHurt) {
                    health.justHurt = false;
                    this.damageFeedback = true;
                    this.damFeedbStartTime = TimeUtils.millis();
                }
            }
        }
    }
    
    @Override
    public void entityAdded(Entity entity) {
        addedToEngine(this.getEngine());
    }

    @Override
    public void entityRemoved(Entity entity) {
        addedToEngine(this.getEngine());
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
