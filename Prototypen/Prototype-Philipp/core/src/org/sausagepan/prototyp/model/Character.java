package org.sausagepan.prototyp.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class Character {

	/* ........................................................ ATTRIBUTES .. */
	private String name;
	private String sex;
	private Array<TextureRegion> spriteSheet;
	private Vector3 position;
	private Vector3 direction;
	private ArrayMap<String,Animation> anims;
	private Animation currentAnim;
	
	/* ...................................................... CONSTRUCTORS .. */
	public Character(String name, String sex, String spriteSheet) {
		this.name = name;
		if(!sex.equals("m") && !sex.equals("f"))
			throw new IllegalArgumentException();
		this.sex = sex;
		
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("textures/spritesheets/" + spriteSheet));
		this.spriteSheet = new Array<TextureRegion>();

		for(Integer i=0; i<12; i++) {
			this.spriteSheet.add(atlas.findRegion(i.toString()));
		}
		
		position  = new Vector3(400,240,0);
		direction = new Vector3();
		
		anims = new ArrayMap<String,Animation>();
		// Walk Animations
			//left
		anims.put("left",
				new Animation(0.1f,
						atlas.findRegion(Integer.toString(9)),
						atlas.findRegion(Integer.toString(10)),
						atlas.findRegion(Integer.toString(11))));
			//right
		anims.put("right",
				new Animation(0.1f,
						atlas.findRegion(Integer.toString(3)),
						atlas.findRegion(Integer.toString(4)),
						atlas.findRegion(Integer.toString(5))));
			//up
		anims.put("up",
				new Animation(0.1f,
						atlas.findRegion(Integer.toString(0)),
						atlas.findRegion(Integer.toString(1)),
						atlas.findRegion(Integer.toString(2))));
			//down
		anims.put("down",
				new Animation(0.1f,
						atlas.findRegion(Integer.toString(6)),
						atlas.findRegion(Integer.toString(7)),
						atlas.findRegion(Integer.toString(8))));
			//idle
		anims.put("idle",
				new Animation(0.1f,
						atlas.findRegion(Integer.toString(7))));

		
		// Set standard animation
		currentAnim = anims.get("idle");
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
		
		if(direction.len() > 5) {
			direction.x = (ax/Vector3.len(ax, ay, 0)*5);
			direction.y = (ay/Vector3.len(ax, ay, 0)*5);
		}
		
		position.x += direction.x;
		position.y += direction.y;
		
		if(position.x > 800-28-16) position.x=800 - 28 - 16;
		if(position.x < 0 + 16)    position.x=0 + 16;
		if(position.y > 480-36-32) position.y=480 - 36 - 32;
		if(position.y < 0 + 16)    position.y=0 + 16;
		
		setAnimation(ax, ay);
	}
	
	/**
	 * Set the animation to idle or walking: left, right, up, down
	 * @param ax	hor difference between position and touch
	 * @param ay	ver difference between position and touch
	 */
	public void setAnimation(float ax, float ay) {
		if(ax < 0.1 && ax > -0.1 && ay < 0.1 && ay > -0.1) currentAnim = anims.get("idle");
		
		if (ax*ax > ay*ay) {
			// x component dominates
			if(ax < 0) currentAnim = anims.get("left");// character moves left
			else       currentAnim = anims.get("right"); // character moves right
		} else {
			// y component dominates
			if(ay > 0) currentAnim = anims.get("up");// character goes up
			else       currentAnim = anims.get("down"); // character goes down
		}
	}
	
	public void update() {
		currentAnim = anims.get("idle");
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
	
	
	
	
}
