package org.sausagepan.prototyp.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;
import org.sausagepan.prototyp.enums.Direction;

import java.util.Iterator;

import static org.sausagepan.prototyp.enums.Direction.*;

public class Character {

	/* ........................................................ ATTRIBUTES .. */
	private String name;
	private String sex;
	private Direction spriteDir = SOUTH;

	// GEORMETRY
	private Vector3 position;
	private Vector3 direction;
	private Vector2 normDir;
	private Rectangle collider = new Rectangle(0,0,28,36);
    private Array<Bullet> bullets;

	// MEDIA
	private Array<TextureRegion> spriteSheet;
	private ArrayMap<Direction,Animation> anims;
	private Animation currentAnim;
	private ArrayMap<Direction,TextureRegion> idleImgs;
	private TextureRegion currentIdleImg;

	// PARTS
	private Status status;
	private Weapon weapon;

	private boolean moving    = false;
	private boolean attacking = false;

    private long lastAttack = 0;


	/* ...................................................... CONSTRUCTORS .. */

	public Character(String name, String sex, String spriteSheet, Status status, Weapon weapon) {

		this.name = name;

		if(!sex.equals("m") && !sex.equals("f")) throw new IllegalArgumentException();
		this.sex = sex;

		// CHARACTERS PROPERTIES
		this.status = status;
		this.weapon = weapon;

		// GEOMETRY
		position  = new Vector3(400,240,0);
		direction = new Vector3();
		normDir   = new Vector2();
        bullets   = new Array<Bullet>();

		// MEDIA
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("textures/spritesheets/" + spriteSheet));
		this.spriteSheet   = new Array<TextureRegion>();
		for(Integer i=0; i<12; i++)
			this.spriteSheet.add(atlas.findRegion(i.toString()));

		// ANIMATIONS
		anims = new ArrayMap<Direction,Animation>();
		// Walk Animations
			//left
		anims.put(WEST,
				new Animation(0.1f,
						atlas.findRegion(Integer.toString(9)),
						atlas.findRegion(Integer.toString(10)),
						atlas.findRegion(Integer.toString(11))));
			//right
		anims.put(EAST,
				new Animation(0.1f,
						atlas.findRegion(Integer.toString(3)),
						atlas.findRegion(Integer.toString(4)),
						atlas.findRegion(Integer.toString(5))));
			//up
		anims.put(NORTH,
				new Animation(0.1f,
						atlas.findRegion(Integer.toString(0)),
						atlas.findRegion(Integer.toString(1)),
						atlas.findRegion(Integer.toString(2))));
			//down
		anims.put(SOUTH,
				new Animation(0.1f,
						atlas.findRegion(Integer.toString(6)),
						atlas.findRegion(Integer.toString(7)),
						atlas.findRegion(Integer.toString(8))));
		
		// Set standard animation
		currentAnim = anims.get(SOUTH);

		// IMAGES
		this.idleImgs = new ArrayMap<Direction,TextureRegion>();
		this.idleImgs.put(SOUTH, atlas.findRegion("7"));
		this.idleImgs.put(NORTH, atlas.findRegion("0"));
		this.idleImgs.put(EAST,  atlas.findRegion("3"));
		this.idleImgs.put(WEST,  atlas.findRegion("9"));
		this.currentIdleImg = this.idleImgs.get(SOUTH);

		this.collider.x = position.x;
		this.collider.y = position.y;
	}
	
	
	/* ........................................................... METHODS .. */
	public void handleTouchInput(Vector3 touchPos) {
		// Debugging
//		System.out.println("a_x: " + (position.x - touchPos.x) * (-1) * 0.03);
//		System.out.println("a_y: " + (position.y - touchPos.y) * (-1) * 0.03);
		
		float ax,ay;	// Acceleration
		ax = (position.x - touchPos.x) * (-1) * 0.03f;
		ay = (position.y - touchPos.y) * (-1) * 0.03f;
		
		// normalize direction vector
		direction.x = (ax);
		direction.y = (ay);

		normDir.x = (ax/Vector3.len(ax, ay, 0)*5);
		normDir.y = (ay/Vector3.len(ax, ay, 0)*5);

		if(direction.len() > 5) {
			direction.x = normDir.x;
			direction.y = normDir.y;
		}
		
		position.x += direction.x;
		position.y += direction.y;
		
		if(position.x > 800-28-16) position.x=800 - 28 - 16;
		if(position.x < 0 + 16)    position.x=0 + 16;
		if(position.y > 480-36-32) position.y=480 - 36 - 32;
		if(position.y < 0 + 16)    position.y = 0 + 16;
		
		setAnimation(ax, ay);

		weapon.setDirection(normDir);
		weapon.getCollider().x = position.x + 14 + weapon.getDirection().x * 5 - 10;
		weapon.getCollider().y = position.y + 16 + weapon.getDirection().y * 5 - 10;

		collider.x = position.x;
		collider.y = position.y;
	}

	public void attack() {
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return;
		attacking = true;
        lastAttack = TimeUtils.millis();
	}

    public void shoot() {
        if(TimeUtils.timeSinceMillis(lastAttack) < 100) return;
        bullets.add(new Bullet(position.x + GlobalSettings.charSpriteWidth/2,
                position.y + GlobalSettings.charSpriteHeight/2, 2, 2, normDir));
        lastAttack = TimeUtils.millis();
    }
	
	/**
	 * Set the animation to idle or walking: left, right, up, down
	 * @param ax	hor difference between position and touch
	 * @param ay	ver difference between position and touch
	 */
	public void setAnimation(float ax, float ay) {
		
		if (ax*ax > ay*ay) {
			// x component dominates
			if(ax < 0) spriteDir = WEST;
			else       spriteDir = EAST;
		} else {
			// y component dominates
			if(ay > 0) spriteDir = NORTH; // character goes up
			else       spriteDir = SOUTH; // character goes down
		}

		// Set animation and idle img
		currentAnim    = anims.get(spriteDir);// character moves left
		currentIdleImg = idleImgs.get(spriteDir);
	}

	public void update() {
		moving = false;
		attacking = false;
        Iterator<Bullet> i = bullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            b.x += Gdx.graphics.getDeltaTime() * 80 * b.direction.x;
            b.y += Gdx.graphics.getDeltaTime() * 80 * b.direction.y;
            if(b.x > 800 || b.x < 0 || b.y > 480 || b.y < 0) i.remove();
        }
	}


	public void drawCharacter(SpriteBatch batch, float elapsedTime) {

		if(moving) batch.draw( currentAnim.getKeyFrame(elapsedTime, true), position.x, position.y);
		else       batch.draw( currentIdleImg, position.x, position.y);

	}

	public void drawCharacterStatus(ShapeRenderer shp) {

			// HP .................................................
			shp.begin(ShapeRenderer.ShapeType.Line);
			shp.setColor(Color.WHITE);
			shp.rect(
                    this.position.x + 2,
                    this.position.y + 40,
                    24, 5
            );
			shp.end();

			shp.begin(ShapeRenderer.ShapeType.Filled);
			shp.setColor(Color.GREEN);
			shp.rect(this.position.x + 3, this.position.y + 41,
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
								position.x + GlobalSettings.charSpriteWidth,
								position.y + GlobalSettings.charSpriteHeight/2,
								14,
								4); break;
					case WEST:
						shp.rect(
								position.x - 14,
								position.y + GlobalSettings.charSpriteHeight/2,
								14,
								4); break;
					case NORTH:
						shp.rect(
								position.x + GlobalSettings.charSpriteWidth/2 - 2,
								position.y + GlobalSettings.charSpriteHeight,
								4,
								14); break;
					case SOUTH:
						shp.rect(
								position.x + GlobalSettings.charSpriteWidth/2 - 2,
								position.y - 14,
								4,
								14); break;
				}
			}
			shp.end();

			// COLLIDER ............................................
			shp.begin(ShapeRenderer.ShapeType.Line);
			shp.setColor(Color.RED);
			shp.rect(
					collider.x,
					collider.y,
					collider.getWidth(),
					collider.getHeight()
			);
			shp.end();

			// MP .................................................
			shp.begin(ShapeRenderer.ShapeType.Filled);
			shp.setColor(Color.BLUE);
			shp.rect( position.x + 3, position.y + 38, 22, 2);
			shp.end();

	}

	
	/* ................................................. GETTERS & SETTERS .. */
	public String getName() {
		return name;
	}

	public String getSex() {
		return sex;
	}

	public Array<TextureRegion> getSpriteSheet() {
		return spriteSheet;
	}


	public Vector3 getPosition() {
		return position;
	}
	
	public Animation getAnimation() {
		return currentAnim;
	}

	public Status getStatus() {
		return status;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public Rectangle getCollider() {
		return  collider;
	}

    public Array<Bullet> getBullets() {
        return bullets;
    }
}
