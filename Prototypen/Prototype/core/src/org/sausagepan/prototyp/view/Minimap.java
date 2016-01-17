package org.sausagepan.prototyp.view;

import java.io.PrintWriter;
import java.io.BufferedWriter; 
import java.io.FileWriter; 
import java.io.IOException; 

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
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
		this.mapData = new boolean[(width+5) * 32][(height+5) * 32];
		this.colliderWalls = colliderWalls;
		
		this.height = height + 5;
		this.width = width + 5;
		
		//set whole mapData to false
		for (int i = this.width * 32 - 1; i >= 0; i--){
			for (int j = this.height * 32 - 1; j >= 0; j--){
				mapData[i][j] = false;
			}
		}

		ColliderLayerToArray(this.colliderWalls);
		//printToFile();
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
		Array<Image> table = new Array<Image>();

		Texture white = new Texture(Gdx.files.internal("UI/minimap_white.png"));
		Texture black = new Texture(Gdx.files.internal("UI/minimap_black.png"));
		
		for (int i = 0; i < 100; i++){
			for (int j = 0; j < 100; j++){	
				if(mapData[i][j])
					table.add(new Image(black));
				else
					table.add(new Image(white));
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
	
	/*only for testing purpose*/
	public void printToFile(){
		PrintWriter pWriter = null; 
        try { 
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter("test.txt")));
            for (int j = height * 32 - 1; j >= 0; j--){
            	for (int i = width * 32 - 1; i >= 0; i--){ 			
    				if(mapData[i][j])pWriter.print(1);
    				else pWriter.print(0);
    				if(i == 0)pWriter.println();
    			}
    		}
             
        } catch (IOException ioe) { 
            ioe.printStackTrace(); 
        } finally { 
            if (pWriter != null){ 
                pWriter.flush(); 
                pWriter.close(); 
            } 
        } 
	}
}



