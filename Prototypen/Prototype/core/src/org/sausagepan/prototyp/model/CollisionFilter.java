package org.sausagepan.prototyp.model;

public class CollisionFilter {
	//public static final short CATEGORY_PLAYER = 0x0001;
	//public static final short CATEGORY_WALL = 0x0002;
	public static final short CATEGORY_LIGHT = 0x0004;
	public static final short CATEGORY_SENSOR = 0x0008;
	
	public static final short MASK_LIGHT = ~CATEGORY_SENSOR;
}
