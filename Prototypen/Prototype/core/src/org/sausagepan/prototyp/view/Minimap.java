package org.sausagepan.prototyp.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class Minimap {
	private boolean[][] mapData;
	private MapLayer colliderWalls = new MapLayer();;
	
	private int height;
	private int width;
	
	private TiledMap map;
	private Array<Image> tableMap;
	
	/*
	 * Generates MapData Array from given Maze
	 */
	public Minimap (int height, int width, MapLayer colliderWalls){
		this.mapData = new boolean[(width+3) * 32][(height+3) * 32];
		this.colliderWalls = colliderWalls;
		
		this.height = height + 3;
		this.width = width + 3;
		
		//set whole mapData to false
		for (int i = this.width * 32 - 1; i >= 0; i--){
			for (int j = this.height * 32 - 1; j >= 0; j--){
				mapData[i][j] = false;
			}
		}

		ColliderLayerToArray(this.colliderWalls);
		MapDataToTable();
	}
	
	/*
	 * Takes the ColliderLayer and sets mapData true whereever there is a collider.
	 */	
	private void ColliderLayerToArray(MapLayer layer){
		for (MapObject mo : layer.getObjects()){
			mapData[(int) ((RectangleMapObject) mo).getRectangle().x / 32][(int) ((RectangleMapObject) mo).getRectangle().y / 32] = true;
			
			for (int i = (int) ((RectangleMapObject) mo).getRectangle().width / 32; i > 0; i--){
				for (int j = (int) ((RectangleMapObject) mo).getRectangle().height / 32; j > 0; j--){
					mapData[(int) ((RectangleMapObject) mo).getRectangle().x / 32 + i][(int) ((RectangleMapObject) mo).getRectangle().y / 32 + j] = true;
				}
			}
		}
	}
	
	/*
	* Visualize MapData array as an Array of Images.
	*/	
	private void MapDataToTable(){
		Array<Image> table = new Array<Image>();
		Texture wt = new Texture(Gdx.files.internal("UI/minimap_white.png"));
		Texture bt = new Texture(Gdx.files.internal("UI/minimap_black.png"));
				
		for (int i = 0; i < 224; i++){
			for (int j = 0; j < 224; j++){	
				Image white = new Image(wt);
				Image black = new Image(bt);
				
				if(mapData[i][j] == true) table.add(black); else table.add(white);
			}
		}
		
		tableMap = table;
	}
	
	public boolean[][] getMapData(){
		return mapData;
	}
	
	public TiledMap getMap(){
		return map;
	}
	
	public Array<Image> getTableMap(){
		return tableMap;
	}
}



