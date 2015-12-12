package org.sausagepan.prototyp.model;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.components.MazeGenerator;
import org.sausagepan.prototyp.model.entities.MapMonsterObject;
import org.sausagepan.prototyp.model.items.MapItem;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.view.OrthogonalTiledMapRendererWithPlayers;

/**
 * Created by georg on 18.10.15.
 */
public class Maze extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    //Tiled Map for map creation and collision detection
    private Network.MapInformation mapInformation;
    private int width;
    private int height;
    private MazeGenerator generator;
    private TiledMap tiledMap;         // contains the layers of the tiled map
    private OrthogonalTiledMapRendererWithPlayers tiledMapRenderer; // renders the tiled map, players and items
    private Array<Vector2> lightPositions;
    private Array<Vector2> monsterPositions;
    private Array<Vector2> gameMasterSecretPositions;
    private Array<MapItem> mapItems;
    private Array<MapMonsterObject> mapMonsterObjects;
    private World world;
    private Array<Body> entranceDoorBodies;
    private Array<Body> doorLockerBodies;
    private Array<BodyDef> doorLockerBodyDefs;
    private Array<Rectangle> doorLockerRectangles;
    private Array<Body> secretWalls;
    private boolean treasureRoomOpen = false;

    /* ........................................................................... CONSTRUCTOR .. */

    public Maze(Network.MapInformation mapInformation, World world, MediaManager mediaManager, boolean gameReady) {
        this(mapInformation, world, gameReady);
        
        // set up map renderer and scale
        tiledMapRenderer = new OrthogonalTiledMapRendererWithPlayers(tiledMap, 32, mediaManager);
    }
    public Maze(Network.MapInformation mapInformation, World world, boolean gameReady) {
        this.mapInformation = mapInformation;
        this.width = mapInformation.width;
        this.height = mapInformation.height;
        this.entranceDoorBodies = new Array<Body>();      // Array with treasure room locking bodies
        this.doorLockerBodies = new Array<Body>();      // Array with treasure room locking bodies
        this.doorLockerBodyDefs = new Array<BodyDef>(); // Array with their definition fo recreate
        this.doorLockerRectangles = new Array<Rectangle>();
        this.secretWalls = new Array<Body>();
        generator = new MazeGenerator(width, height);
        setUpTiledMap(world);
        this.world = world;

        if(gameReady)
            openEntranceDoors();
    }
    /* ............................................................................... METHODS .. */
    public void render(OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    /**
     * Sets up the {@link TiledMap} and {@link OrthogonalTiledMapRendererWithPlayers} for the game
     */
    public void setUpTiledMap(World world) {
        tiledMap = generator.createNewMapFromGrid(mapInformation.entries);
        lightPositions = generator.getLightPositions();
        monsterPositions = generator.getMonsterPositions();
        mapItems = generator.getMapItems();
        gameMasterSecretPositions = generator.getGameMasterSecretPositions();
        mapMonsterObjects = generator.getMonsterObjects();
        // create static bodies from colliders
        Rectangle r;
        for(MapObject mo : tiledMap.getLayers().get("colliderWalls").getObjects()) {
            r = ((RectangleMapObject) mo).getRectangle();
            BodyDef groundBodyDef  = new BodyDef();
            groundBodyDef.type     = BodyDef.BodyType.StaticBody;
            groundBodyDef.position.set(new Vector2(r.x / 32f + r.width / 64f, r.y / 32f + r.height / 64f));
            Body groundBody        = world.createBody(groundBodyDef);

            // Look for door objects
            if(mo.getName().equals("lockedDoor")) {
                doorLockerRectangles.add(r);
                doorLockerBodies.add(groundBody);
                doorLockerBodyDefs.add(groundBodyDef);
            }

            // Look for door objects
            if(mo.getName().equals("entranceDoor")) {
                entranceDoorBodies.add(groundBody);
            }

            // List Game Masters secret passages
            if(mo.getName() != null && mo.getName().equals("secretWall"))
                secretWalls.add(groundBody);

            PolygonShape groundBox = new PolygonShape();
            groundBox.setAsBox(r.width/64f, r.height/64f);
            groundBody.createFixture(groundBox, 0.0f);
            groundBox.dispose();
        }
    }

    /**
     * Destroys bodies blocking the way into the treasure room
     */
    public void openTreasureRoom() {
    	if(!treasureRoomOpen) {
	        for(Body b : doorLockerBodies)
	        	world.destroyBody(b);
            doorLockerBodies.clear();
	        treasureRoomOpen = true;
    	}
    }

    /**
     * Destroys bodies blocking secret passages
     */
    public void openSecretPassages() {
        for(Body b : secretWalls)
            world.destroyBody(b);
    }

    /**
     * Recreate bodies blocking way into treasure room, when character loses key parts
     */
    public void lockTreasureRoom() {
        int i=0;
        Body body;
        for(BodyDef bd : doorLockerBodyDefs) {
            body = world.createBody(bd);
            PolygonShape box = new PolygonShape();
            box.setAsBox(
                    doorLockerRectangles.get(i).width/64f,
                    doorLockerRectangles.get(i).height/64f);
            body.createFixture(box, 0.0f);
            box.dispose();
            i++;

            doorLockerBodies.add(body);
        }
        treasureRoomOpen = false;
    }

    public void openEntranceDoors() {
        for(Body b : entranceDoorBodies)
            world.destroyBody(b);
    }

    /* ..................................................................... GETTERS & SETTERS .. */
    public OrthogonalTiledMapRendererWithPlayers getTiledMapRenderer() {
        return tiledMapRenderer;
    }

    public Array<Vector2> getLightPositions() {
        return lightPositions;
    }

    public Array<Vector2> getMonsterPositions() {
        return monsterPositions;
    }

    public Array<Vector2> getGameMasterSecretPositions() {
        return gameMasterSecretPositions;
    }

    public Array<MapItem> getMapItems() {
        return mapItems;
    }

    public Array<MapMonsterObject> getMapMonsterObjects() {
        return mapMonsterObjects;
    }

    public MapObjects getColliders() {
        return tiledMap.getLayers().get("colliderWalls").getObjects();
    }
    
    public float[][] getStartPositions() {
    	return generator.getStartPositions();
    }
}
