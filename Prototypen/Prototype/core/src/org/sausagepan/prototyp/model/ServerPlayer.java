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
import org.sausagepan.prototyp.network.NetworkPosition;

public class ServerPlayer {

	/* ............................................................................ ATTRIBUTES .. */

	// Character Properties
	private String name;
	private int id;

    // Components
    private Status status_;
    private Weapon weapon;

	// Geometry
	private Direction dir;                  // looking direction of character (N,S,W,E)
	private Vector2   direction;            // vector from character to touch position
    private Array<Bullet> activeBullets;    // bullets flying through the air right now

    public NetworkPosition position;              // position container for network transmission

	// Physics
	private boolean moving;         // whether character is moving or not


	/* .......................................................................... CONSTRUCTORS .. */

    /**
     * Standard Constructor
     * @param name          characters name
     * @param sex           characters sex
     * @param spriteSheet   sprite sheet to use for drawing character
     * @param status_        characters status_
     * @param weapon        characters initial weapon
     */
	public ServerPlayer(String name, String sex, int id, String spriteSheet, Status status_, Weapon weapon, boolean self) {

		this.name = name;

		if(!sex.equals("m") && !sex.equals("f")) throw new IllegalArgumentException();
		this.id = id;

		// CHARACTERS PROPERTIES
		this.status_ = status_;
		this.weapon = weapon;

		// Geometry
		this.dir       = Direction.SOUTH;
		this.direction = new Vector2(0,0);

		this.activeBullets = new Array<Bullet>();
        this.position  = new NetworkPosition(new Vector3(0,0,0), this.dir, this.moving);

		// Physics
		this.moving = false;
	}
	
	
	/* ............................................................................... METHODS .. */
	public String getName() {
		return name;
	}

    public int getId() {
    	return id;
    }
	
	public Vector2 getDirection() {
		return direction;
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
