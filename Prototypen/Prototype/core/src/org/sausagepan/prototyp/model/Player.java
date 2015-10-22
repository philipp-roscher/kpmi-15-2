package org.sausagepan.prototyp.model;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.enums.PlayerAction;
import org.sausagepan.prototyp.input.PlayerInputProcessor;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.components.PlayerBattleComponent;
import org.sausagepan.prototyp.model.components.PlayerGraphicsComponent;
import org.sausagepan.prototyp.model.components.PlayerInputComponent;
import org.sausagepan.prototyp.model.components.PlayerPhysicsComponent;
import org.sausagepan.prototyp.model.components.PositionComponent;
import org.sausagepan.prototyp.model.components.SkyDirectionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.network.NetworkPosition;

public class Player extends Entity {

	/* ............................................................................ ATTRIBUTES .. */

	// Character Properties
	private String  name;
	private int     id;

    // Components
    private PlayerAttributeContainer attributes;
    private PlayerPhysicsComponent   physics;    // collisions, walking, ...
    private PlayerBattleComponent    battle;     // damage, fighting, ...
    public  PlayerGraphicsComponent  graphics;
    public  PlayerInputComponent     input;

    private Array<PlayerObserver> observers;          // observe players actions


	/* .......................................................................... CONSTRUCTORS .. */
    /**
     * Standard Constructor
     * @param name          characters name
     * @param spriteSheet   sprite sheet to use for drawing character
     * @param status_       characters status_
     * @param weapon        characters initial weapon
     * @param mediaManager  {@link MediaManager} for obtaining textures
     * @param world         {@link World} for creation of characters {@link Body}
     * @param rayHandler    for creation of characters {@link PointLight}
     */
	public Player(
            String name,
            String sex,
            int id,
            String spriteSheet,
            Status status_,
            Weapon weapon,
            boolean self,
            MediaManager mediaManager,
            World world,                // world object for physics calculations
            RayHandler rayHandler,      // object for handling light rendering
            Vector2 spawnPos            // players start position in maze
    ) {

		this.name       = name;
		this.id         = id;

        // Container object for players attributes, keeps components untangled
        this.attributes = new PlayerAttributeContainer(
                mediaManager, spriteSheet, world, rayHandler, status_, weapon, spawnPos);

        this.observers = new Array<PlayerObserver>();

        // Components
        this.physics  = new PlayerPhysicsComponent(attributes, world, rayHandler);
        this.graphics = new PlayerGraphicsComponent(attributes);
        this.battle   = new PlayerBattleComponent(attributes);
        this.input    = new PlayerInputComponent(this, attributes, physics, battle);

        // Components observer attributes for changes
        attributes.subscribe(graphics);
        attributes.subscribe(physics);
        attributes.subscribe(battle);

        this.add(new WeaponComponent(mediaManager.getTextureAtlasType("weapons").findRegion("sword")));
        this.add(new SkyDirectionComponent());
        this.add(new PositionComponent());
	}
	
	
	/* ............................................................................... METHODS .. */



    /**
     * Updates characters properties once a render loop
     * @param elapsedTime
     */
	public void update(float elapsedTime) {

        // components get updated independently from each other once per frame
        physics.update(elapsedTime);
        graphics.update(elapsedTime);
        battle.update(elapsedTime);
        this.getComponent(PositionComponent.class).x = physics.getDynBody().getPosition().x-32;
        this.getComponent(PositionComponent.class).y = physics.getDynBody().getPosition().y-32;
        this.getComponent(SkyDirectionComponent.class).skyDirection = attributes.getDir();

	}


    public void draw(ShapeRenderer shp) {
        graphics.drawCharacterStatus(shp);
    }


    public void updatePosition(Vector3 position, Direction dir, boolean moving) {
        physics.getDynBody().setTransform(position.x, position.y, 0.0f);
        attributes.setDir(dir);
    }


    public void notifyPlayerObservers(PlayerAction action) {
        for(PlayerObserver o : observers)
            o.update(this, action);
    }



    /**
     * Draws a red rectangle where the characters collider for attacks is placed
     * @param shp   {@link ShapeRenderer} to draw to
     */
    public void debugRenderer(ShapeRenderer shp) {
        battle.debugRenderer(shp);
    }

    /* ..................................................................... SETTERS & GETTERS .. */
	public String getName() {
		return name;
	}

    public int getPlayerId() {
    	return id;
    }

	public Vector2 getPosition() {
		return physics.getDynBody().getPosition();
	}

    public NetworkPosition getPos() {
        return attributes.getNetPos();
    }

	public Status getStatus_() {
		return attributes.getStatus();
	}

	public Body getBody() {
		return physics.getDynBody();
	}
	
    public Array<Bullet> getBullets() {
        return battle.getActiveBullets();
    }

    public Direction getDir() {
        return attributes.getDir();
    }

    public PlayerBattleComponent getBattle() {
        return battle;
    }
}
