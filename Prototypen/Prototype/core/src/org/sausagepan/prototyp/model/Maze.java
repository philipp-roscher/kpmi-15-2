package org.sausagepan.prototyp.model;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.enums.ObjectGroup;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.MazeGenerator;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.view.OrthogonalTiledMapRendererWithPlayers;

/**
 * Created by georg on 18.10.15.
 */
public class Maze extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    //Tiled Map for map creation and collision detection
    private Network.MapInformation mapInformation;
    private TiledMap tiledMap;         // contains the layers of the tiled map
    private OrthogonalTiledMapRendererWithPlayers tiledMapRenderer; // renders the tiled map, players and items
    private Array<Vector2> lightPositions;
    private Array<Vector2> monsterPositions;
    private Array<Vector2> gameMasterSecretPositions;
    private World world;
    private Array<Body> doorLockerBodies;
    private Array<BodyDef> doorLockerBodyDefs;
    private Array<Rectangle> doorLockerRectangles;
    private Array<Body> secretWalls;

    /* ........................................................................... CONSTRUCTOR .. */

    public Maze(Network.MapInformation mapInformation, World world, MediaManager mediaManager) {
        this.mapInformation = mapInformation;
        this.doorLockerBodies = new Array<Body>();      // Array with treasure room locking bodies
        this.doorLockerBodyDefs = new Array<BodyDef>(); // Array with their definition fo recreate
        this.doorLockerRectangles = new Array<Rectangle>();
        this.secretWalls = new Array<Body>();
        setUpTiledMap(world);
        this.world = world;
        // set up map renderer and scale
        tiledMapRenderer = new OrthogonalTiledMapRendererWithPlayers(tiledMap, 32, mediaManager);
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

        MazeGenerator generator = new MazeGenerator();
        generator.setParam(mapInformation.width, mapInformation.height);
        tiledMap = generator.createNewMapFromGrid(mapInformation.entries);
        lightPositions = generator.getLightPositions();
        monsterPositions = generator.getMonsterPositions();
        gameMasterSecretPositions = generator.getGameMasterSecretPositions();
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
        for(Body b : doorLockerBodies)
            world.destroyBody(b);
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
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */
    public void addCharacterSpriteComponent(CharacterSpriteComponent spriteComponent) {
        this.tiledMapRenderer.addCharacterSpriteComponent(spriteComponent);
    }
    
    public void addWeaponComponent(WeaponComponent weaponComponent) {
    	this.tiledMapRenderer.addWeaponComponent(weaponComponent);
    }
	public void removeCharacterSpriteComponent(
			CharacterSpriteComponent component) {
		this.tiledMapRenderer.removeCharacterSpriteComponent(component);
		
	}
	public void removeWeaponComponent(WeaponComponent component) {
		this.tiledMapRenderer.removeWeaponComponent(component);		
	}

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

    public MapObjects getColliders() {
        return tiledMap.getLayers().get("colliderWalls").getObjects();
    }
}
