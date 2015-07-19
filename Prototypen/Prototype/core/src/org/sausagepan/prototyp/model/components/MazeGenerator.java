package org.sausagepan.prototyp.model.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class MazeGenerator {
	int mazeheight = 5;
	int mazewidth = 5;
	
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	
	TiledMap map = new TiledMap();
	MapLayers layers = map.getLayers();

	TiledMapTileLayer ground = new TiledMapTileLayer(mazewidth * 32 + 64, mazeheight * 32 + 32, 32, 32);
	TiledMapTileLayer walls = new TiledMapTileLayer(mazewidth * 32 + 64, mazeheight * 32 + 32, 32, 32);
	TiledMapTileLayer objects = new TiledMapTileLayer(mazewidth * 32 + 64, mazeheight * 32 + 32, 32, 32);
	TiledMapTileLayer tops = new TiledMapTileLayer(mazewidth * 32 + 64, mazeheight * 32 + 32, 32, 32);	
	
	int[][] positions = new int[5][2];
	
	MapLayer colliderWalls = new MapLayer();
	
	public MazeGenerator(){}
	
	private void generateMaze(){
		map = new TiledMap();
		
		for(int i = mazeheight; i > 0; i--){
			for(int j = mazewidth; j > 0; j--){
				addNewTile("tilemaps/maze" + atRandom() + ".tmx", i, j);
			}
		}
		
		addSaveZone();
		addTreasure();	
		
		map.getLayers().add((MapLayer)ground);
		map.getLayers().add((MapLayer)walls);
		map.getLayers().add((MapLayer)objects);
		map.getLayers().add((MapLayer)tops);
		map.getLayers().add(colliderWalls);
		
		
		
		if(map.getLayers() != null) System.out.println(map.getLayers().getCount());
	}
	
	private void addSaveZone(){
		addNewTile("tilemaps/room1.tmx", (int) Math.ceil(mazeheight / 2), 0);
		positions[0][0] =  (int) Math.ceil(mazeheight / 2) * 32 + 16;
		positions[0][1] =  -16;
		addNewTile("tilemaps/room2.tmx", 0, (int) Math.ceil(mazeheight / 2) + 1);
		positions[1][0] =  16;
		positions[1][1] =  (int) Math.ceil(mazeheight / 2) * 32 + 16;
		positions[2][0] =  16;
		positions[2][1] =  (int) Math.ceil(mazeheight / 2) * 32 + 17;
		addNewTile("tilemaps/room3.tmx", mazewidth, (int) Math.ceil(mazeheight / 2) + 1);
		positions[3][0] =  mazewidth * 32 + 16;
		positions[3][1] =  (int) Math.ceil(mazeheight / 2) * 32 + 16;
		positions[4][0] =  mazewidth * 32 + 16;
		positions[4][1] =  (int) Math.ceil(mazeheight / 2) *32 + 17;
		
	}
	
	private void addTreasure(){
		//addNewTile("treasure.tmx", (int) Math.ceil(mazewidth / 2) + 1, (int) Math.ceil(mazeheight / 2) + 1);
	}
	
	private void addNewTile(String tile, int x, int y){
		tiledMap = new TmxMapLoader().load(tile);
		
		
		for(int layer_nr = 0; layer_nr < 5; layer_nr++){
			if(layer_nr==4){
				addNewObjectTile(tiledMap, x, y);
				return;
			}
			TiledMapTileLayer test = (TiledMapTileLayer) tiledMap.getLayers().get(layer_nr);
			
			for(int k = 0; k < 32; k++){
				for(int l = 0; l < 32; l++){
						Cell cell = new Cell();
						
						if(test.getCell(k, l) != null)	cell.setTile(test.getCell(k, l).getTile());

						switch(layer_nr){
							case 0: ground.setCell(k  + (x-1)*32 + 32, l + (y-1)*32, cell);
							case 1: walls.setCell(k  + (x-1)*32 + 32, l + (y-1)*32, cell);
							case 2: objects.setCell(k  + (x-1)*32 + 32, l + (y-1)*32, cell);
							case 3: tops.setCell(k  + (x-1)*32 + 32, l + (y-1)*32, cell);
						}
				}
			}
		}
	}
	
	private void addNewObjectTile(TiledMap tile, int x, int y){
		MapLayer test = tile.getLayers().get(4);

		for(MapObject mo : test.getObjects()) {	
			
		     Float tet = mo.getProperties().get("x", Float.class);
		     mo.getProperties().put("x", tet / 32  + (x-1) * 32 * 32 + 32 * 32);
		     tet = mo.getProperties().get("y", Float.class);
		     mo.getProperties().put("y", tet / 32 + (y-1) * 32 * 32);
		     
		     colliderWalls.getObjects().add(mo);
		     
		}
		
		colliderWalls.setName("colliderWalls");
	}
	
	private int atRandom(){
		return (int) ((Math.random()*2)+1);
	}
	
	public TiledMap getMap(){
		return map;
	}
	
	public TiledMap createNewMap(){
		generateMaze();
		return map;
	}
	
	public void setParam(int mazewidth, int mazeheight){
		this.mazewidth = mazewidth;
		this.mazeheight = mazeheight;
	}
	
	public int[][] getStartPositions(){
		return positions;
	}
}
