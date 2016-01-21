package org.sausagepan.prototyp.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;

public class Minimap {
	private int height;
	private int width;
	private Color[][] tableMap;
	
	/*
	 * Generates MapData Array from given Maze
	 */
	public Minimap (int height, int width, MapLayer colliderWalls){
		this.height = (height + 1)*32+9;
		this.width = (width + 2)*32;
		MapDataToTable(ColliderLayerToArray(colliderWalls));
	}
	
	/*
	 * Takes the ColliderLayer and sets mapData true whereever there is a collider.
	 */	
	private boolean[][] ColliderLayerToArray(MapLayer layer){
		boolean[][] mapData = new boolean[this.width][this.height];
		
		for (MapObject mo : layer.getObjects()){
			for (int i = (int) ((RectangleMapObject) mo).getRectangle().width / 32; i > 0; i--){
				for (int j = (int) ((RectangleMapObject) mo).getRectangle().height / 32; j > 0; j--){
					mapData[(int) ((RectangleMapObject) mo).getRectangle().x / 32 + i - 1][(int) ((RectangleMapObject) mo).getRectangle().y / 32 + j - 1] = true;
				}
			}
		}
		
		return mapData;
	}
	
	/*
	* Visualize MapData array as an Array of Images.
	*/	
	private void MapDataToTable(boolean[][] mapData){
		tableMap = new Color[width][height];
		
		for (int i = 0; i < width; i++){
			for (int j = 0; j < height; j++){	
				if(mapData[i][j])
					tableMap[i][j] = Color.BLACK;
				else
					tableMap[i][j] = Color.WHITE;
			}
		}
	}
	
	public Color[][] getTableMap(){
		return tableMap;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}



