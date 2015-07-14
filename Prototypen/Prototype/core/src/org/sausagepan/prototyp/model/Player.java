package org.sausagepan.prototyp.model;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;

import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.network.Position;

import java.util.Iterator;

import static org.sausagepan.prototyp.enums.Direction.*;

public class Player {

	/* ........................................................ ATTRIBUTES .. */
	private String name;
	private String sex;
	private Direction spriteDir = SOUTH;

	// Geometry
	private Direction dir;          // looking direction of character (N,S,W,E)
	private Vector3   touchPos;     // screen touch position
	private Vector2   direction;    // vector from character to touch position
	private Vector2   normDir;      // normalized direction vector with length 1
	private float     ax, ay;       // direction vectors components
	private Array<Bullet> bullets;
    public Position position;

	// Media
	private ArrayMap<String,Animation> playerAnims;     // characters animations (N,S,W,E)
	private Animation                  recentAnim;      // alive animation
	private TextureRegion              recentIdleImg;   // alive idle image
	private Sprite                     sprite;          // characters sprite

	// Physics
	private boolean moving;         // whether character is moving or not
	private Body dynBody;        // dynamic box2d body for physics
	private Fixture fixture;        // characters fixture for shape, collision, material a.s.o., child of body

	// Light
	private PointLight spriteLight; // sprites light source

	// PARTS
	private Status status;
	private Weapon weapon;

	private boolean attacking = false;

    private long lastAttack = 0;


	/* ...................................................... CONSTRUCTORS .. */

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
		this.touchPos  = new Vector3(0,0,0);
		this.direction = new Vector2(0,0);
		this.normDir   = new Vector2(0,0);
		this.bullets   = new Array<Bullet>();
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

	}
	
	
	/* ........................................................... METHODS .. */

	public void attack() {
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return;
		attacking = true;
        lastAttack = TimeUtils.millis();
	}

    public void shoot() {
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return;
        bullets.add(new Bullet(dynBody.getPosition().x, dynBody.getPosition().y, 2, 2, normDir));
        lastAttack = TimeUtils.millis();
    }

	public void update(float elapsedTime) {
		if(moving)
			this.sprite.setRegion(recentAnim.getKeyFrame(elapsedTime, true));
		else
			this.sprite.setRegion(recentIdleImg);

		this.sprite.setPosition(
				dynBody.getPosition().x - fixture.getShape().getRadius(),
				dynBody.getPosition().y - fixture.getShape().getRadius()
		);

		spriteLight.setPosition(dynBody.getPosition().x, dynBody.getPosition().y);
        updateNetworkPosition();

	}

    /* ......................................................................................... GETTERS & SETTERS .. */


	public void move(Vector3 touchPos) {
		if(Math.abs(touchPos.x - dynBody.getPosition().x) > Math.abs(touchPos.y - dynBody.getPosition().y)) {
			if (touchPos.x > dynBody.getPosition().x)
				dir = Direction.EAST;
			else
				dir = Direction.WEST;
		} else {
			if(touchPos.y > dynBody.getPosition().y)
				dir = Direction.NORTH;
			else
				dir = Direction.SOUTH;
		}



		ax = (-1)*(dynBody.getPosition().x-touchPos.x);
		ay = (-1)*(dynBody.getPosition().y-touchPos.y);

		direction.x = ax;
		direction.y = ay;

		normDir.x = (ax / Vector3.len(ax, ay, 0) * 5);
		normDir.y = (ay / Vector3.len(ax, ay, 0) * 5);

		if (direction.len() > 4) {
			direction.x = normDir.x;
			direction.y = normDir.y;
		}

		if(direction.len() < 1) {
			direction.x = 0;
			direction.y = 0;
			moving = false;
		} else {
			moving = true;
		}

		dynBody.setLinearVelocity(direction);
		updateSprite(this.dir);
	}

	public void stop() {
		this.dynBody.setLinearVelocity(0.0f,0.0f);
		this.moving = false;
	}

	private void updateSprite(Direction direction) {
		switch(direction) {
			case NORTH: recentAnim = playerAnims.get("n"); break;
			case SOUTH: recentAnim = playerAnims.get("s"); break;
			case WEST: recentAnim = playerAnims.get("w"); break;
			case EAST: recentAnim = playerAnims.get("e"); break;
		}
		recentIdleImg = recentAnim.getKeyFrames()[0];
	}


    /* ......................................................................................... SETTERS & GETTERS .. */

	public void updateBullets(Array<Rectangle> colliders, Vector3 touchPos, float elapsedTime) {

        Iterator<Bullet> i = bullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            b.x += Gdx.graphics.getDeltaTime() * 80 * b.direction.x;
            b.y += Gdx.graphics.getDeltaTime() * 80 * b.direction.y;
            if(b.x > 800 || b.x < 0 || b.y > 480 || b.y < 0) i.remove();
        }
	}


	public void drawCharacterStatus(ShapeRenderer shp) {

			// HP .................................................

			shp.begin(ShapeRenderer.ShapeType.Filled);
			shp.setColor(Color.GREEN);
			shp.rect(this.dynBody.getPosition().x + 3, this.dynBody.getPosition().y + 41,
                    22 * (this.status.getHP() / Float.valueOf(this.status.getMaxHP())), 4);
			shp.end();

			// WEAPON ............................................
			shp.begin(ShapeRenderer.ShapeType.Line);
			shp.setColor(Color.DARK_GRAY);
			shp.rect(
                    weapon.getCollider().x,
                    weapon.getCollider().y,
                    weapon.getCollider().getWidth(),
                    weapon.getCollider().getHeight()
            );

            // BULLETS ..........................................
            for(Bullet b : bullets)
                shp.rect(b.x, b.y, b.width, b.height);

        shp.end();

        shp.begin(ShapeRenderer.ShapeType.Filled);
			if(attacking) {
                System.out.println("Is attacking!");
				switch (spriteDir) {
					case EAST:
						shp.rect(
								dynBody.getPosition().x + GlobalSettings.charSpriteWidth,
								dynBody.getPosition().y + GlobalSettings.charSpriteHeight/2,
								14,
								4); break;
					case WEST:
						shp.rect(
								dynBody.getPosition().x - 14,
								dynBody.getPosition().y + GlobalSettings.charSpriteHeight/2,
								14,
								4); break;
					case NORTH:
						shp.rect(
								dynBody.getPosition().x + GlobalSettings.charSpriteWidth/2 - 2,
								dynBody.getPosition().y + GlobalSettings.charSpriteHeight,
								4,
								14); break;
					case SOUTH:
						shp.rect(
								dynBody.getPosition().x + GlobalSettings.charSpriteWidth/2 - 2,
								dynBody.getPosition().y - 14,
								4,
								14); break;
				}
			}
			shp.end();


			// MP .................................................
			shp.begin(ShapeRenderer.ShapeType.Filled);
			shp.setColor(Color.BLUE);
			shp.rect( dynBody.getPosition().x + 3, dynBody.getPosition().y + 38, 22, 2);
			shp.end();

	}

    public Rectangle convertFromPositionToCollider(Vector3 pos, Rectangle coll) {
        coll.x = pos.x - coll.width/2;
        coll.y = pos.y - coll.height/2;
        return coll;
    }

	
	/* ................................................. GETTERS & SETTERS .. */
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
        return bullets;
    }

	public Sprite getSprite() {
		return sprite;
	}

//    public Rectangle getDamageCollider() {
//        return damageCollider;
//    }

    public void setMoving(boolean moving) {
        this.moving = moving;
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

}
