package org.sausagepan.prototyp.model;

import box2dLight.PointLight;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.components.PlayerPhysicsComponent;
import org.sausagepan.prototyp.network.NetworkPosition;

public class ServerPlayer {

	/* ................................................................................................ ATTRIBUTES .. */

	// Character Properties
	private String name;
	private String sex;
	private int id;

    // Components
    private Status status_;
    private Weapon weapon;
    private PlayerPhysicsComponent physics;

    // Character Status
    private boolean attacking = false;  // whether the character is weaponDrawn at the moment

	// Geometry
	private Direction dir;                  // looking direction of character (N,S,W,E)
	private Vector2   direction;            // vector from character to touch position
	private Vector2   normDir;              // normalized direction vector with length 1
	private float     ax, ay;               // direction vectors components
	private Pool<Bullet>  bulletPool;       // pool of available bullets
    private Array<Bullet> activeBullets;    // bullets flying through the air right now

    public NetworkPosition position;              // position container for network transmission

	// Physics
	private boolean moving;         // whether character is moving or not
	
    private long lastAttack = 0;


	/* ...................................................... CONSTRUCTORS .. */

    /**
     * Standard Constructor
     * @param name          characters name
     * @param sex           characters sex
     * @param spriteSheet   sprite sheet to use for drawing character
     * @param status_        characters status_
     * @param weapon        characters initial weapon
     * @param mediaManager  {@link MediaManager} for obtaining textures
     * @param world         {@link World} for creation of characters {@link Body}
     * @param rayHandler    for creation of characters {@link PointLight}
     */
	public ServerPlayer(String name, String sex, int id, String spriteSheet, Status status_, Weapon weapon, boolean self) {

		this.name = name;

		if(!sex.equals("m") && !sex.equals("f")) throw new IllegalArgumentException();
		this.sex = sex;
		this.id = id;

		// CHARACTERS PROPERTIES
		this.status_ = status_;
		this.weapon = weapon;

		// Geometry
		this.dir       = Direction.SOUTH;
		this.direction = new Vector2(0,0);
		this.normDir   = new Vector2(0,0);

		this.activeBullets = new Array<Bullet>();
        this.position  = new NetworkPosition(new Vector3(0,0,0), this.dir, this.moving);

		// Physics
		this.moving = false;
        
        // Pools
//        this.bulletPool = new Pool<Bullet>() {
//            @Override
//            protected Bullet newObject() {
//                return new Bullet(dynBody.getNetPos().x, dynBody.getNetPos().y, .1f, .1f, normDir);
//            }
//        };
	}
	
	
	/* ........................................................... METHODS .. */

	public void attack() {
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return;
		attacking = true;
        lastAttack = TimeUtils.millis();
	}

    public void stopAttacking() {
        this.attacking = false;
    }

    /**
     * Spawns new bullets
     */
//    public void shoot() {
//        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return; // maximum 10 bullets per second
//        Bullet newBullet = bulletPool.obtain();                 // obtain new bullet from pool
//        newBullet.init(                                         // initialize obtained bullet
//                dynBody.getNetPos().x,
//                dynBody.getNetPos().y,
//                normDir);
//        activeBullets.add(newBullet);                           // add initialized bullet to active bullets
//        lastAttack = TimeUtils.millis();                        // remember spawn time
//    }


    public void updatePosition(Vector3 position, Direction dir, boolean moving) {
    	this.position.position.x = position.x;
    	this.position.position.y = position.y;
        this.dir = dir;
        this.moving = moving;
    }

	public String getName() {
		return name;
	}

	public String getSex() {
		return sex;
	}

    public int getId() {
    	return id;
    }
	
	public Vector2 getDirection() {
		return direction;
	}
	
	public boolean isMoving() {
		return moving;
	}

	public Status getStatus_() {
		return status_;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public Rectangle getWeaponCollider() {
		switch (dir) {
	        case EAST:
	            return new Rectangle(
	                    position.position.x + .5f,
	                    position.position.y,
	                    .5f,
	                    .2f);
	        case WEST:
	        	return new Rectangle(
	                    position.position.x - 1,
	                    position.position.y,
	                    .5f,
	                    .2f);
	        case NORTH:
	        	return new Rectangle(
	                    position.position.x,
	                    position.position.y + .6f,
	                    .2f,
	                    .5f);
	        case SOUTH:
	        	return new Rectangle(
	                    position.position.x,
	                    position.position.y - 1.2f,
	                    .2f,
	                    .5f);
	        default: return null;
		}
	}

    public Array<Bullet> getBullets() {
        return activeBullets;
    }

    public Rectangle getDamageCollider() {
        return new Rectangle(position.position.x-.35f,position.position.y-.5f,.7f,1);
    }




}
