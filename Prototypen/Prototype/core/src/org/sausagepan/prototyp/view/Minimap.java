package org.sausagepan.prototyp.view;

import org.sausagepan.prototyp.model.GlobalSettings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class Minimap {
	private boolean[][] mapData;
	
	private int height;
	private int width;
	
	private TiledMap map;
	private Color[][] tableMap;
	
	/*
	 * Generates MapData Array from given Maze
	 */
	public Minimap (int height, int width, MapLayer colliderWalls){
		this.height = (height + 2)*32 + 1;
		this.width = (width + 2)*32 + 1;
		
		this.mapData = new boolean[this.width][this.height];
		
		//set whole mapData to false
		for (int i = this.width - 1; i >= 0; i--){
			for (int j = this.height - 1; j >= 0; j--){
				mapData[i][j] = false;
			}
		}

		ColliderLayerToArray(colliderWalls);
		MapDataToTable();
	}
	
	/*
	 * Takes the ColliderLayer and sets mapData true whereever there is a collider.
	 */	
	private void ColliderLayerToArray(MapLayer layer){
		for (MapObject mo : layer.getObjects()){
			for (int i = (int) ((RectangleMapObject) mo).getRectangle().width / 32; i > 0; i--){
				for (int j = (int) ((RectangleMapObject) mo).getRectangle().height / 32; j > 0; j--){
					mapData[(int) ((RectangleMapObject) mo).getRectangle().x / 32 + i - 1][(int) ((RectangleMapObject) mo).getRectangle().y / 32 + j - 1] = true;
				}
			}
		}
	}
	
	/*
	* Visualize MapData array as an Array of Images.
	*/	
	private void MapDataToTable(){
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
	
	public boolean[][] getMapData(){
		return mapData;
	}
	
	public TiledMap getMap(){
		return map;
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



