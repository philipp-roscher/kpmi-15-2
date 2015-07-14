package org.sausagepan.prototyp.model;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.network.Position;

import java.util.Iterator;

import static org.sausagepan.prototyp.enums.Direction.*;

public class Player {

	/* ................................................................................................ ATTRIBUTES .. */

	// Character Properties
	private String name;
	private String sex;

    // Components
    private Status status;
    private Weapon weapon;

    // Character Status
    private boolean attacking = false;  // whether the character is attacking at the moment

	// Geometry
	private Direction dir;                  // looking direction of character (N,S,W,E)
	private Vector2   direction;            // vector from character to touch position
	private Vector2   normDir;              // normalized direction vector with length 1
	private float     ax, ay;               // direction vectors components
	private Pool<Bullet>  bulletPool;       // pool of available bullets
    private Array<Bullet> activeBullets;    // bullets flying through the air right now
    private Rectangle     attackCollider;   // represents the characters body in battles

    public  Position position;              // position container for network transmission

	// Media
	private ArrayMap<String,Animation> playerAnims;     // characters animations (N,S,W,E)
	private Animation                  recentAnim;      // alive animation
	private TextureRegion              recentIdleImg;   // alive idle image
	private Sprite                     sprite;          // characters sprite

	// Physics
	private boolean moving;         // whether character is moving or not
	private Body    dynBody;        // dynamic box2d body for physics
	private Fixture fixture;        // characters fixture for shape, collision, material a.s.o., child of body

	// Light
	private PointLight spriteLight; // sprites light source

    private long lastAttack = 0;


	/* ...................................................... CONSTRUCTORS .. */

    /**
     * Standard Constructor
     * @param name          characters name
     * @param sex           characters sex
     * @param spriteSheet   sprite sheet to use for drawing character
     * @param status        characters status
     * @param weapon        characters initial weapon
     * @param mediaManager  {@link MediaManager} for obtaining textures
     * @param world         {@link World} for creation of characters {@link Body}
     * @param rayHandler    for creation of characters {@link PointLight}
     */
	public Player(String name, String sex, String spriteSheet, Status status, Weapon weapon, MediaManager mediaManager,
				  World world, RayHandler rayHandler) {

		this.name = name;

		if(!sex.equals("m") && !sex.equals("f")) throw new IllegalArgumentException();
		this.sex = sex;

		// CHARACTERS PROPERTIES
		this.status = status;
		this.weapon = weapon;

		// Geometry
		this.dir       = Direction.SOUTH;
		this.direction = new Vector2(0,0);
		this.normDir   = new Vector2(0,0);

		this.activeBullets = new Array<Bullet>();
        this.position  = new Position(new Vector3(0,0,0), this.dir, this.moving);

		// Physics
		this.moving = false;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;    // set up body definition for player
		bodyDef.position.set(1.1f, 1.1f);               // set players bodys position
		dynBody = world.createBody(bodyDef);            // add body to the world
		CircleShape circle = new CircleShape();         // give body a shape
		circle.setRadius(.4f);                          // set the shapes radius
		FixtureDef fixDef = new FixtureDef();           // create players fixture
		fixDef.shape       = circle;                    // give shape to fixture
		fixDef.density     = 0.5f;                      // objects density
		fixDef.friction    = 0.4f;                      // objects friction on other objects
		fixDef.restitution = 0.0f;                      // bouncing
		fixture = dynBody.createFixture(fixDef);        // add fixture to body
		circle.dispose();                               // dispose shapes

        this.attackCollider = new Rectangle(dynBody.getPosition().x-.35f,dynBody.getPosition().y-.5f,.7f,1);

		// Light
		spriteLight = new PointLight(rayHandler, 256, new Color(1,1,1,1), 8, 0, 0);

		// Media
		TextureAtlas atlas = mediaManager.getTextureAtlas("textures/spritesheets/" + spriteSheet);

		// load animation textures
		playerAnims = new ArrayMap<String,Animation>();
		playerAnims.put("n", new Animation(.2f, atlas.findRegions("n")));
		playerAnims.put("e", new Animation(.2f, atlas.findRegions("e")));
		playerAnims.put("s", new Animation(.2f, atlas.findRegions("s")));
		playerAnims.put("w", new Animation(.2f, atlas.findRegions("w")));

		recentAnim    = playerAnims.get("s");
		recentIdleImg = playerAnims.get("s").getKeyFrames()[0];
		this.sprite = new Sprite(recentIdleImg);
		this.sprite.setSize(.8f, 1);
		this.sprite.setPosition(
                dynBody.getPosition().x - fixture.getShape().getRadius(),
                dynBody.getPosition().y - fixture.getShape().getRadius());


        // Pools
        this.bulletPool = new Pool<Bullet>() {
            @Override
            protected Bullet newObject() {
                return new Bullet(dynBody.getPosition().x, dynBody.getPosition().y, .1f, .1f, normDir);
            }
        };
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
    public void shoot() {
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return; // maximum 10 bullets per second
        Bullet newBullet = bulletPool.obtain();                 // obtain new bullet from pool
        newBullet.init(                                         // initialize obtained bullet
                dynBody.getPosition().x,
                dynBody.getPosition().y,
                normDir);
        activeBullets.add(newBullet);                           // add initialized bullet to active bullets
        lastAttack = TimeUtils.millis();                        // remember spawn time
    }

    /**
     * Updates characters properties once a render loop
     * @param elapsedTime
     */
	public void update(float elapsedTime) {

        // set sprite image
		if(moving) this.sprite.setRegion(recentAnim.getKeyFrame(elapsedTime, true));
		else       this.sprite.setRegion(recentIdleImg);

        // set sprite position
		this.sprite.setPosition(
				dynBody.getPosition().x - fixture.getShape().getRadius(),
				dynBody.getPosition().y - fixture.getShape().getRadius()
		);

        this.attackCollider.x = sprite.getX();
        this.attackCollider.y = sprite.getY();

        // move characters light
		spriteLight.setPosition(dynBody.getPosition().x, dynBody.getPosition().y);

        // update network position container
        updateNetworkPosition();

        // update bullets
        updateBullets();

	}

    /**
     * Stop characters movement
     */
    public void stop() {
        this.dynBody.setLinearVelocity(0.0f, 0.0f); // set velocities to zero
        this.moving = false;                        // for sprite
    }

    /**
     * Update sprite to the image according to the recent movement
     * @param direction
     */
    private void updateSprite(Direction direction) {
        switch(direction) {
            case NORTH: recentAnim = playerAnims.get("n"); break;
            case SOUTH: recentAnim = playerAnims.get("s"); break;
            case WEST:  recentAnim = playerAnims.get("w"); break;
            case EAST:  recentAnim = playerAnims.get("e"); break;
        }
        recentIdleImg = recentAnim.getKeyFrames()[0];
    }

    /**
     * Change characters velocities in x and y direction according to the touch position
     * @param touchPos
     */
	public void move(Vector3 touchPos) {

        // calculate characters main moving direction for sprite choosing
		if(Math.abs(touchPos.x - dynBody.getPosition().x) > Math.abs(touchPos.y - dynBody.getPosition().y)) {
			if (touchPos.x > dynBody.getPosition().x) dir = Direction.EAST;
			else                                      dir = Direction.WEST;
		} else {
			if(touchPos.y > dynBody.getPosition().y)  dir = Direction.NORTH;
			else                                      dir = Direction.SOUTH;
		}

        // split up velocity vector in x and y component
		ax = (-1)*(dynBody.getPosition().x-touchPos.x);
		ay = (-1)*(dynBody.getPosition().y-touchPos.y);

		direction.x = ax;
		direction.y = ay;

        // normalize velocity vector
		normDir.x = (ax / Vector3.len(ax, ay, 0) * 5);
		normDir.y = (ay / Vector3.len(ax, ay, 0) * 5);

        // limit maximum velocity
		if (direction.len() > 4) {
			direction.x = normDir.x;
			direction.y = normDir.y;
		}

        // set velocity to zero, if below the given value
		if(direction.len() < 1) {
			direction.x = 0;
			direction.y = 0;
			moving = false;
		} else moving = true;

        // add calculated velocity to the dynamic body
		dynBody.setLinearVelocity(direction);

        // update sprite image
		updateSprite(this.dir);
	}

    /**
     * Updates bullets positions and returns them to the pool, if they reach the screens edge
     */
    public void updateBullets() {

        Iterator<Bullet> i = activeBullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            b.x += Gdx.graphics.getDeltaTime() * 2 * b.direction.x;
            b.y += Gdx.graphics.getDeltaTime() * 2 * b.direction.y;

            if(b.x > UnitConverter.pixelsToMeters(800) ||
               b.x < 0 || b.y > UnitConverter.pixelsToMeters(480) ||
               b.y < 0) {

                b.reset();
                i.remove();
            }
        }
    }

    public void updateNetworkPosition() {
        this.position.position.x = dynBody.getPosition().x;
        this.position.position.y = dynBody.getPosition().y;
        this.position.direction  = dir;
        this.position.isMoving   = moving;
    }

    public void updatePosition(Vector3 position, Direction dir, boolean moving) {
        this.dynBody.setTransform(position.x, position.y, 0.0f);
        this.dir = dir;
        this.updateSprite(dir);
    }


    public void drawCharacterStatus(ShapeRenderer shp) {

        // HP ..........................................................................................................

        shp.begin(ShapeRenderer.ShapeType.Filled);
        shp.setColor(Color.GREEN);
        shp.rect(
                this.dynBody.getPosition().x - .35f,
                this.dynBody.getPosition().y + .75f,
                .75f * (this.status.getHP() / Float.valueOf(this.status.getMaxHP())),
                .15f);
        shp.end();

        // MP ..........................................................................................................
        shp.begin(ShapeRenderer.ShapeType.Filled);
        shp.setColor(Color.BLUE);
        shp.rect(
                this.dynBody.getPosition().x - .35f,
                this.dynBody.getPosition().y + .65f,
                .75f,
                .1f);
        shp.end();

        // WEAPON ......................................................................................................
        shp.begin(ShapeRenderer.ShapeType.Line);
        shp.setColor(Color.DARK_GRAY);
        shp.rect(
                weapon.getCollider().x,
                weapon.getCollider().y,
                weapon.getCollider().getWidth(),
                weapon.getCollider().getHeight()
        );

        // BULLETS .....................................................................................................
        for(Bullet b : activeBullets)
            shp.rect(b.x, b.y, b.width, b.height);

        shp.end();

        // draw weapon
        shp.begin(ShapeRenderer.ShapeType.Filled);
        if(attacking) {
            System.out.println("Is attacking!");
            switch (dir) {
                case EAST:
                    shp.rect(
                            dynBody.getPosition().x + .5f,
                            dynBody.getPosition().y,
                            .5f,
                            .2f); break;
                case WEST:
                    shp.rect(
                            dynBody.getPosition().x - 1,
                            dynBody.getPosition().y,
                            .5f,
                            .2f); break;
                case NORTH:
                    shp.rect(
                            dynBody.getPosition().x,
                            dynBody.getPosition().y + .6f,
                            .2f,
                            .5f); break;
                case SOUTH:
                    shp.rect(
                            dynBody.getPosition().x,
                            dynBody.getPosition().y - 1.2f,
                            .2f,
                            .5f); break;
            }
        }
        shp.end();

    }

    /**
     * Draws a red rectangle where the characters collider for attacks is placed
     * @param shp   {@link ShapeRenderer} to draw to
     */
    public void debugRenderer(ShapeRenderer shp) {
        shp.begin(ShapeRenderer.ShapeType.Line);
        shp.setColor(1,0,0,1);
        shp.rect(attackCollider.x,attackCollider.y,attackCollider.width,attackCollider.height);
        shp.end();
    }

    /* ......................................................................................... SETTERS & GETTERS .. */


    public Rectangle convertFromPositionToCollider(Vector3 pos, Rectangle coll) {
        coll.x = pos.x - coll.width/2;
        coll.y = pos.y - coll.height/2;
        return coll;
    }

	public String getName() {
		return name;
	}

	public String getSex() {
		return sex;
	}


	public Vector2 getPosition() {
		return this.dynBody.getPosition();
	}
	
	public Vector2 getDirection() {
		return direction;
	}
	
	public boolean isMoving() {
		return moving;
	}

	public Status getStatus() {
		return status;
	}

	public Weapon getWeapon() {
		return weapon;
	}


    public Array<Bullet> getBullets() {
        return activeBullets;
    }

	public Sprite getSprite() {
		return sprite;
	}

//    public Rectangle getDamageCollider() {
//        return damageCollider;
//    }




}
