package org.sausagepan.prototyp.network;

import com.badlogic.gdx.math.Vector3;
import org.sausagepan.prototyp.enums.Direction;

public class NetworkPosition {
	public Vector3 position;
	public Direction direction;
	public boolean isMoving;
	
	public NetworkPosition() {
	}
	
	public NetworkPosition(Vector3 position, Direction direction, boolean isMoving) {
		super();
		this.position = position;
		this.direction = direction;
		this.isMoving = isMoving;
	}
}
