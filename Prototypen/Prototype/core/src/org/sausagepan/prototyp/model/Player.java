package org.sausagepan.prototyp.model;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.components.PlayerBattleComponent;
import org.sausagepan.prototyp.model.components.PlayerGraphicsComponent;
import org.sausagepan.prototyp.model.components.PlayerPhysicsComponent;
import org.sausagepan.prototyp.network.NetworkPosition;

public class Player {

	/* ............................................................................ ATTRIBUTES .. */

	// Character Properties
	private String  name;
	private int     id;

    // Components
    private PlayerAttributeContainer attributes;
    private PlayerPhysicsComponent   physics;    // collisions, walking, ...
    private PlayerBattleComponent    battle;     // damage, fighting, ...
    private PlayerGraphicsComponent  graphics;


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
            World world,
            RayHandler rayHandler,
            Vector2 spawnPos) {

		this.name       = name;
		this.id         = id;
        this.attributes = new PlayerAttributeContainer(
                mediaManager, spriteSheet, world, rayHandler, status_, weapon, spawnPos);

        // Components
        this.physics  = new PlayerPhysicsComponent(attributes, world, rayHandler);
        this.graphics = new PlayerGraphicsComponent(attributes);
        this.battle   = new PlayerBattleComponent(attributes);

        attributes.subscribe(graphics);
	}
	
	
	/* ............................................................................... METHODS .. */



    /**
     * Updates characters properties once a render loop
     * @param elapsedTime
     */
	public void update(float elapsedTime) {

        physics.update(elapsedTime);
        graphics.update(elapsedTime);
        battle.update(elapsedTime);

	}

    public void update(Vector3 touchPos) {
        physics.update(touchPos);
    }

    public void draw(ShapeRenderer shp) {
        graphics.drawCharacterStatus(shp);
    }

    /**
     * Stop characters movement
     */
    public void stop() {
        attributes.setMoving(false);                        // for sprite
        attributes.getDirection().set(0, 0);                    // set velocities to zero
    }


    public void updatePosition(Vector3 position, Direction dir, boolean moving) {
        physics.getDynBody().setTransform(position.x, position.y, 0.0f);
        attributes.setDir(dir);
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

    public int getId() {
    	return id;
    }

	public Vector2 getPosition() {
		return physics.getDynBody().getPosition();
	}

    public NetworkPosition getPos() {
        return attributes.getNetPos();
    }
	
	public boolean isMoving() {
		return attributes.isMoving();
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

	public Sprite getSprite() {
		return attributes.getSprite();
	}

    public Rectangle getDamageCollider() {
        return battle.getAttackCollider();
    }

    public Direction getDir() {
        return attributes.getDir();
    }

    public PlayerBattleComponent getBattle() {
        return battle;
    }
}
