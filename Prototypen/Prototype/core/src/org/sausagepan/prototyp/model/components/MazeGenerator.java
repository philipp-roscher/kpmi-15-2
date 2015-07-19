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

	//einzelne Layer der Map
	TiledMapTileLayer ground = new TiledMapTileLayer(mazewidth * 32 + 64, mazeheight * 32 + 32, 32, 32);
	TiledMapTileLayer walls = new TiledMapTileLayer(mazewidth * 32 + 64, mazeheight * 32 + 32, 32, 32);
	TiledMapTileLayer objects = new TiledMapTileLayer(mazewidth * 32 + 64, mazeheight * 32 + 32, 32, 32);
	TiledMapTileLayer tops = new TiledMapTileLayer(mazewidth * 32 + 64, mazeheight * 32 + 32, 32, 32);	
	
	int[][] positions = new int[5][2];
	
	MapLayer colliderWalls = new MapLayer();
	
	public MazeGenerator(){}
	
	//Hauptfunktion, leitet gesamte Generierung ein
	private void generateMaze(){
		map = new TiledMap();
		
		//so hoch un breit wie in den Settings festgelegt
		for(int i = mazeheight; i > 0; i--){
			for(int j = mazewidth; j > 0; j--){
				addNewTile("tilemaps/maze" + atRandom() + ".tmx", i, j);
			}
		}
		
		//gesonderte Bereiche einfügen
		addSaveZone();
		addTreasure();	
		
		//Layer wieder zu kompleter TiledMap zusammenfügen
		map.getLayers().add((MapLayer)ground);
		map.getLayers().add((MapLayer)walls);
		map.getLayers().add((MapLayer)objects);
		map.getLayers().add((MapLayer)tops);
		map.getLayers().add(colliderWalls);
	}
	
	//setzt Spawnräume und berechnet Ausgangspositionen für die Spieler
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
	
	//Platzierung der Schatzkammer
	private void addTreasure(){
		//addNewTile("treasure.tmx", (int) Math.ceil(mazewidth / 2) + 1, (int) Math.ceil(mazeheight / 2) + 1);
	}
	
	//rendert neues Tile mit all seinen Layern, ObjectLayer werden weitergereicht
	private void addNewTile(String tile, int x, int y){
		tiledMap = new TmxMapLoader().load(tile);
		
		
		for(int layer_nr = 0; layer_nr < 5; layer_nr++){ 	//für alle Layer
			if(layer_nr==4){								//solange kein ObjectLayer
				addNewColliderLayer(tiledMap, x, y);
				return;
			}
			TiledMapTileLayer test = (TiledMapTileLayer) tiledMap.getLayers().get(layer_nr);
			
			for(int k = 0; k < 32; k++){					// füge alle 32*32 Positionen ein
				for(int l = 0; l < 32; l++){
						Cell cell = new Cell();
						
						if(test.getCell(k, l) != null)	cell.setTile(test.getCell(k, l).getTile()); //hol dir die Kachel falls die Zelle existiert

						switch(layer_nr){															//setze Kachel auf entsprechenden Layer an korrekte Position
							case 0: ground.setCell(k  + (x-1)*32 + 32, l + (y-1)*32, cell);
							case 1: walls.setCell(k  + (x-1)*32 + 32, l + (y-1)*32, cell);
							case 2: objects.setCell(k  + (x-1)*32 + 32, l + (y-1)*32, cell);
							case 3: tops.setCell(k  + (x-1)*32 + 32, l + (y-1)*32, cell);
						}
				}
			}
		}
	}

    /**
     * Method for converting collider objects and merging them into one layer
     * @param map   map to fetch colliders from
     * @param x     x offset of colliders
     * @param y     y offset of colliders
     */
    private void addNewColliderLayer(TiledMap map, int x, int y) {
        MapLayer colliderLayer = map.getLayers().get(4); // get collider layer

        for (MapObject mo : colliderLayer.getObjects()) {    // for every object in the original collider layer
            RectangleMapObject nmo = new RectangleMapObject();  // create new rectangle map object

            // Coordinates before conversion
            System.out.println("Before: "
                    + mo.getProperties().get("x", Float.class)
                    + " "
                    + mo.getProperties().get("y", Float.class)
                    + " "
                    + mo.getProperties().get("width", Float.class)
                    + " "
                    + mo.getProperties().get("height", Float.class));

            // Store x and y coordinates in a 2D vector
            Vector2 pos = new Vector2(
                    mo.getProperties().get("x", Float.class) + x*32*32,
                    mo.getProperties().get("y", Float.class) + y*32*32
            );

            // set rectangle objects rectangle properties to the new position and original width and height
            nmo.getRectangle().set(
                    pos.x,
                    pos.y,
                    mo.getProperties().get("width", Float.class),
                    mo.getProperties().get("height", Float.class)
            );

            // ad recently created new collider object to layer
            colliderWalls.getObjects().add(nmo);

            System.out.println("After: "
                    + nmo.getRectangle().x
                    + " "
                    + nmo.getRectangle().y
                    + " "
                    + nmo.getRectangle().width
                    + " "
                    + nmo.getRectangle().height);


            colliderWalls.setName("colliderWalls");
        }
    }
	
	//Rendering für ObjectLayer
	private void addNewObjectTile(TiledMap tile, int x, int y){
		MapLayer test = tile.getLayers().get(4);

		for(MapObject mo : test.getObjects()) {										//alle Objekte aus dem ObjectLayer holen
			
		     Float tet = mo.getProperties().get("x", Float.class);					//berechne Positionierung in neuer Map
		     mo.getProperties().put("x", tet / 32 + (x - 1) * 32 * 32 + 32 * 32);
		     tet = mo.getProperties().get("y", Float.class);
		     mo.getProperties().put("y", tet / 32 + (y - 1) * 32 * 32);
		     
		     colliderWalls.getObjects().add(mo);									//in entsprechenden Layer einfügen
		     
		}
		
		colliderWalls.setName("colliderWalls");
	}
	
	private int atRandom(){
		return (int) ((Math.random()*2)+1);
	}
	
	public TiledMap getMap(){
		return map;
	}
	
	//für Neugenerierung
	public TiledMap createNewMap(){
		generateMaze();
		return map;
	}
	
	//Settings übernehmen
	public void setParam(int mazewidth, int mazeheight){
		this.mazewidth = mazewidth;
		this.mazeheight = mazeheight;
	}
	
	//übergibt Startpsotionen der Spieler
	public int[][] getStartPositions(){
		return positions;
	}
}
