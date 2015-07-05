package org.sausagepan.prototyp.model;

import com.badlogic.gdx.math.Vector3;

public class Position {
	private Vector3 position;
	private Vector3 direction;

	public Position() {
	}
	
	public Position(Vector3 position, Vector3 direction) {
		super();
		this.position = position;
		this.direction = direction;
	}
	
	public Vector3 getPosition() {
		return position;
	}
	public void setPosition(Vector3 position) {
		this.position = position;
	}
	public Vector3 getDirection() {
		return direction;
	}
	public void setDirection(Vector3 direction) {
		this.direction = direction;
	}
}
