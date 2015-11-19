package org.sausagepan.prototyp.model.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.entities.MapMonsterObject;
import org.sausagepan.prototyp.model.items.MapItem;


/**
 * Generates a tiled map by merging several tmx tiled map files into one
 */
public class MazeGenerator {

    /* ................................................................................................ ATTRIBUTES .. */
	int mazeHeight = 5; // grid height for generated tiled map
	int mazeWidth  = 5; // grid width for generated tiled map
	
	TiledMap    tiledMap;   // containing one of the tiled maps
	TiledMap    map;        // taking the new map merged from several other tiled maps

	// new map layers
	TiledMapTileLayer ground;
	TiledMapTileLayer walls;
	TiledMapTileLayer objects;
	TiledMapTileLayer tops;
    MapLayer          colliderWalls;
    MapLayer          lights;

    Array<Vector2> lightPositions;
    Array<Vector2> gameMasterSecretPositions;
    Array<Vector2> monsterPositions;
	Array<MapMonsterObject> monsterObjects;
    Array<MapItem> mapItems;

	float[][] positions;


    /* .............................................................................................. CONSTRUCTORS .. */

    /**
     * Factory for generating tiled maps from multiple given tiled maps by merging them into one according to a
     * randomly created grid
     */
	public MazeGenerator(){

        this.map = new TiledMap();

        this.ground  = new TiledMapTileLayer(mazeWidth * 32 + 64, mazeHeight * 32 + 32, 32, 32);
        this.walls   = new TiledMapTileLayer(mazeWidth * 32 + 64, mazeHeight * 32 + 32, 32, 32);
        this.objects = new TiledMapTileLayer(mazeWidth * 32 + 64, mazeHeight * 32 + 32, 32, 32);
        this.tops    = new TiledMapTileLayer(mazeWidth * 32 + 64, mazeHeight * 32 + 32, 32, 32);
        this.colliderWalls = new MapLayer();
        this.lights        = new MapLayer();

        this.lightPositions = new Array<Vector2>();
        this.monsterPositions = new Array<Vector2>();
        this.monsterObjects = new Array<MapMonsterObject>();
        this.gameMasterSecretPositions = new Array<Vector2>();
        this.mapItems = new Array<MapItem>();

        this.positions = new float[5][2]; //[Player][0 - x, 1 - y], GM is Player 0, Team 1 1+2, Team 2 3+4

    }


    /* ................................................................................................... METHODS .. */

    /**
     * initializes map generation
     * @param entries
     */
	private void generateMazeFromGrid(Map<Vector2, Integer> entries){

		map = new TiledMap();   // initialize new map
		
		// for each maze cell do
		for(int i = mazeHeight; i > 0; i--)
			for(int j = mazeWidth; j > 0; j--){
				if (i == (int) Math.ceil(mazeHeight / 2) && j == (int) Math.ceil(mazeWidth / 2))
					addTreasure();	// treasure cave
				else addNewMazeCell("tilemaps/maze" + entries.get(new Vector2(i, j)) + ".tmx", i, j);
			}				

		addSafeZone();  // safe spawning zone	
		addWall(); //wall around the whole maze

        // combine layers to a new tiled map
		map.getLayers().add(ground);        			// ground layer
		map.getLayers().add(walls);         			// walls layer
		map.getLayers().add(objects);       			// objects layer
		map.getLayers().add(tops);          			// layer rendered above character
		map.getLayers().add(colliderWalls); 			// layer containing collider rectangles
        map.getLayers().add(lights);
	}


    /**
     * Calculates safe zones for spawning players
     */
	private void addSafeZone(){
		// Game Masters Spawn Room
		addNewMazeCell("tilemaps/spawnRoomDragon.tmx", (int) Math.ceil(mazeWidth / 2), 0);
		positions[0][0] =  (int) Math.ceil(mazeWidth / 2) * 32 * 32 + 16 * 32;
		positions[0][1] =  16 * 32;

		// Team Reds Spawn Room
		addNewMazeCell("tilemaps/spawnRoomTeamRed.tmx", 0, (int) Math.ceil(mazeHeight / 2) + 1);
		positions[1][0] =  16 * 32;
		positions[1][1] =  (int) Math.ceil(mazeHeight / 2) * 32 * 32 + 16 * 32;
		positions[2][0] =  16 * 32;
		positions[2][1] =  (int) Math.ceil(mazeHeight / 2) * 32 * 32 + 17 * 32;

		// Team Blues Spawn Room
		addNewMazeCell("tilemaps/spawnRoomTeamBlue.tmx",
                mazeWidth + 1, (int) Math.ceil(mazeHeight / 2) + 1);
		positions[3][0] =  mazeWidth * 32 * 32 + 16 * 32;
		positions[3][1] =  (int) Math.ceil(mazeHeight / 2) * 32 * 32 + 16 * 32;
		positions[4][0] =  mazeWidth * 32 * 32 + 16 * 32;
		positions[4][1] =  (int) Math.ceil(mazeHeight / 2) * 32 * 32 + 17 * 32;
		
	}
	
	/**
	 * Adds the treasury
	 */
	private void addTreasure(){
		addNewMazeCell("tilemaps/treasureRoom.tmx", (int) Math.ceil(mazeWidth / 2), (int) Math.ceil(mazeHeight / 2));
	}
	
	/**
	 * Adds a wall around the whole maze in order to keep the players inside of it.
	 */
	private void addWall(){
		LinkedList<Rectangle> listOfWalls = new LinkedList<Rectangle>();
		Rectangle above = new Rectangle(
				32 * 32,
				(mazeHeight + 1) * 32 * 32,
				mazeWidth * 32 * 32,
				32
				);
		listOfWalls.add(above);
		
		Rectangle topLeft = new Rectangle(
				32 * 31,
				(int) Math.ceil(mazeHeight / 2) * 32 * 32 + 2 * 32 * 32,
				32,
				(int) Math.ceil(mazeHeight / 2) * 32 * 32 + 16 * 32
				);
		listOfWalls.add(topLeft);
		
		Rectangle topRight = new Rectangle(
				(mazeWidth + 1) * 32 * 32,
				(int) Math.ceil(mazeHeight / 2) * 32 * 32 + 2 * 32 * 32,
				32,
				(int) Math.ceil(mazeHeight / 2) * 32 * 32 + 16 * 32
				);
		listOfWalls.add(topRight);
		
		Rectangle downLeft = new Rectangle(
				32 * 31,
				(int) Math.ceil(mazeHeight / 2) * 32 * 32 - 32 * 32,
				32,
				(int) Math.ceil(mazeHeight / 2) * 32 * 32 + 16 * 32
				);
		listOfWalls.add(downLeft);
		
		Rectangle downRight = new Rectangle(
				(mazeWidth + 1) * 32 * 32,
				(int) Math.ceil(mazeHeight / 2) * 32 * 32 - 32 * 32,
				32,
				(int) Math.ceil(mazeHeight / 2) * 32 * 32 + 16 * 32
				);
		listOfWalls.add(downRight);
		
		Rectangle underneathLeft = new Rectangle(
				32 * 32,
				32 * 31,
				(int) Math.ceil (mazeWidth / 2) * 32 * 32 - 32 * 32,
				32
				);
		listOfWalls.add(underneathLeft);
		
		Rectangle underneathRight = new Rectangle(
				(int) Math.ceil (mazeWidth / 2) * 32 * 32 + 32 * 32,
				32 * 31,
				(int) Math.ceil (mazeWidth / 2) * 32 * 32,
				32
				);
		listOfWalls.add(underneathRight);
		
		for(Rectangle x : listOfWalls){
			RectangleMapObject help = new RectangleMapObject();
		
			help.getRectangle().set(x);
			colliderWalls.getObjects().add(help);
		}
		
		
	}

    /**
     * Adds next maze cell to the big general maze containing the whole world
     * @param tile  tiled map to add to the big map
     * @param x     x position in the maze grid
     * @param y     y position in the maze grid
     */
	private void addNewMazeCell(String tile, int x, int y){
		tiledMap = new TmxMapLoader().load(tile);
		calculateLightPositions(tiledMap, x, y);
        calculateObjectPositions(tiledMap, x, y);
		
		for(int layer_nr = 0; layer_nr < 6; layer_nr++){ 	//für alle Layer
			if(layer_nr == 4){								
				addNewColliderLayer(tiledMap, x, y, "colliderWalls");
				return;
			}
			
			if(layer_nr == 5){								
				addNewColliderLayer(tiledMap, x, y, "gameMasterColliderWalls");
				return;
			}

			// get Layer from Tiled Map
			TiledMapTileLayer newLayer = (TiledMapTileLayer) tiledMap.getLayers().get(layer_nr);
			
			for(int k = 0; k < 32; k++){					// füge alle 32*32 Positionen ein
				for(int l = 0; l < 32; l++){
						Cell cell = new Cell();
						
						if(newLayer.getCell(k, l) != null)	cell.setTile(newLayer.getCell(k, l).getTile()); //hol dir die Kachel falls die Zelle existiert

						switch(layer_nr){															//setze Kachel auf entsprechenden Layer an korrekte Position
							case 0: ground.setCell(k  + 32*x, l + 32*y, cell);
							case 1: walls.setCell(k  + 32*x, l + 32*y, cell);
							case 2: objects.setCell(k  + 32*x, l + 32*y, cell);
							case 3: tops.setCell(k  + 32*x, l + 32*y, cell);
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
    private void addNewColliderLayer(TiledMap map, int x, int y, String layer) {
        MapLayer colliderLayer = map.getLayers().get(layer); // get collider layer

        for (MapObject mo : colliderLayer.getObjects()) {    // for every object in the original collider layer
            RectangleMapObject nmo = new RectangleMapObject();  // create new rectangle map object

            // Store x, y, width, height in rectangle object
            Rectangle pos = new Rectangle(
                    mo.getProperties().get("x", Float.class) + x*32*32,
                    mo.getProperties().get("y", Float.class) + y*32*32,
                    mo.getProperties().get("width", Float.class),
                    mo.getProperties().get("height", Float.class)
            );

            if(mo.getName() != null && mo.getName().equals("lockedDoor"))
                nmo.setName("lockedDoor");

            if(mo.getName() != null && mo.getName().equals("secretWall"))
                nmo.setName("secretWall");

            // set rectangle objects rectangle properties to the new position and original width and height
            nmo.getRectangle().set(pos);

            // ad recently created new collider object to layer
            if (layer.equals("colliderWalls"))
            	colliderWalls.getObjects().add(nmo);

            colliderWalls.setName("colliderWalls");
        }
    }

    /**
     * Adds orange lights where objects called "light" exist in the layer "lights". For "secretGM"
     * it adds green lights, so GameMaster can see secret passages
     * @param map
     * @param x
     * @param y
     */
    private void calculateLightPositions(TiledMap map, int x, int y) {
        MapLayer lightsLayer;
        if(map.getLayers().get("lights") != null) {
            try {
                lightsLayer = map.getLayers().get("lights");   // get lights layer

                for (MapObject mo : lightsLayer.getObjects()) {
                    if(mo.getName() != null && mo.getName().equals("secretGM")) {
                        gameMasterSecretPositions.add(new Vector2(
                                mo.getProperties().get("x", Float.class) / 32 + x * 32 + .5f,
                                mo.getProperties().get("y", Float.class) / 32 + y * 32 + .5f
                        ));
                    }else{
                        lightPositions.add(new Vector2(
                                mo.getProperties().get("x", Float.class) / 32 + x * 32 + .5f,
                                mo.getProperties().get("y", Float.class) / 32 + y * 32 + .5f
                        ));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//            System.err.println("TMX does not contain a lights layer");
        }
    }

	private void calculateObjectPositions(TiledMap map, int x, int y) {
		MapLayer objectLayer;
		if(map.getLayers().get("mapObjects") != null) {
			try {
				objectLayer = map.getLayers().get("mapObjects");

				for (MapObject mo : objectLayer.getObjects()) {
                    System.out.print(mo.getName());
                    // Create Monsters .................................................... MONSTERS
                    if(mo.getName().equals("monster")) {
                        System.out.print("ma");
                        MapMonsterObject monster = new MapMonsterObject(
                                new Vector2(
                                        mo.getProperties().get("x", Float.class)/32 + x*32 + .5f,
                                        mo.getProperties().get("y", Float.class)/32 + y*32 + .5f
                                ),
                                null
                        );
                        if(mo.getProperties().get("type").equals("zombie"))
                            monster.characterClass = CharacterClass.MONSTER_ZOMBIE;
                        if(mo.getProperties().get("type").equals("skeleton"))
                            monster.characterClass = CharacterClass.MONSTER_SKELETON;
                        this.monsterObjects.add(monster);
                    }

                    // Create Items .......................................................... ITEMS
                    if(mo.getName().equals("item")) {
                        mapItems.add(new MapItem(
                                new Vector2(
                                        mo.getProperties().get("x", Float.class)/32 + x*32 + .5f,
                                        mo.getProperties().get("y", Float.class)/32 + y*32 + .5f
                                ),
                                mo.getProperties().get("type", String.class),
                                Float.parseFloat(mo.getProperties().get("value", String.class))));
                    }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	public TiledMap getMap(){
		return map;
	}


    /**
     * Re-initialize random map creation
     * @param entries   template grid
     * @return          generated tiled map
     */
	public TiledMap createNewMapFromGrid(Map<Vector2, Integer> entries){
		generateMazeFromGrid(entries);
		return map;
	}
	
	//Settings übernehmen
	public void setParam(int mazewidth, int mazeheight){
		this.mazeWidth = mazewidth;
		this.mazeHeight = mazeheight;
	}

    public Array<Vector2> getLightPositions() {
        return lightPositions;
    }

    public Array<Vector2> getMonsterPositions() {
        return monsterPositions;
    }

    public Array<MapMonsterObject> getMonsterObjects() {
        return  monsterObjects;
    }

    public Array<Vector2> getGameMasterSecretPositions() {
        return gameMasterSecretPositions;
    }

    public Array<MapItem> getMapItems() {
        return mapItems;
    }

    //übergibt Startpsotionen der Spieler
	public float[][] getStartPositions(){
		return positions;
	}
}
