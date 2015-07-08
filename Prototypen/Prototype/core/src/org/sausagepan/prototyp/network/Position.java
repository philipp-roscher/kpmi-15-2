package org.sausagepan.prototyp.network;

import com.badlogic.gdx.math.Vector3;

public class Position {
	public Vector3 position;
	public Vector3 direction;
	public boolean isMoving;
	
	public Position() {
	}
	
	public Position(Vector3 position, Vector3 direction, boolean isMoving) {
		super();
		this.position = position;
		this.direction = direction;
		this.isMoving = isMoving;
	}
}