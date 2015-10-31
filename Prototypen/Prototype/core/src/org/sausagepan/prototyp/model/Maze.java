package org.sausagepan.prototyp.model;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
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
import org.sausagepan.prototyp.model.components.MazeGenerator;
import org.sausagepan.prototyp.model.components.SpriteComponent;
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

    /* ........................................................................... CONSTRUCTOR .. */

    public Maze(Network.MapInformation mapInformation, World world, MediaManager mediaManager) {
        this.mapInformation = mapInformation;
        setUpTiledMap(world);
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
        // create static bodies from colliders
        Rectangle r;
        for(MapObject mo : tiledMap.getLayers().get("colliderWalls").getObjects()) {
            r = ((RectangleMapObject) mo).getRectangle();
            BodyDef groundBodyDef  = new BodyDef();
            groundBodyDef.type     = BodyDef.BodyType.StaticBody;
            groundBodyDef.position.set(new Vector2(r.x/32f+r.width/64f, r.y/32f + r.height/64f));
            Body groundBody        = world.createBody(groundBodyDef);
            PolygonShape groundBox = new PolygonShape();
            groundBox.setAsBox(r.width/64f, r.height/64f);
            groundBody.createFixture(groundBox, 0.0f);
            groundBox.dispose();
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */
    public void addPlayer(Player player) {
        this.tiledMapRenderer.addPlayer(player);
    }
    public void removePlayer(Player player) {
        this.tiledMapRenderer.removePlayer(player);
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
}
