package org.sausagepan.prototyp.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.components.PlayerComponent;
import org.sausagepan.prototyp.network.NetworkPosition;

import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * Created by georg on 16.10.15.
 *
 * Contains all attributes that have to be accessed by different components. This way components
 * stay decoupled and are informed about changes by a simple messaging system.
 */
public class PlayerAttributeContainer {

    /* ............................................................................ ATTRIBUTES .. */

    // Physics --------------------------------------------------------------------------------|
    private boolean moving;     // whether character is moving or not

    // Light
    private PointLight spriteLight; // sprites light source

    // Graphics -------------------------------------------------------------------------------|
    private ArrayMap<String,Animation>  playerAnims;     // characters animations (N,S,W,E)
    private Animation                   recentAnim;      // alive animation
    private TextureRegion               recentIdleImg;   // alive idle image
    private Sprite                      sprite;          // characters sprite

    // Geometry -------------------------------------------------------------------------------|
    private Direction   dir;            // looking direction of character (N,S,W,E)
    private Vector2     direction;      // vector from character to touch netPos
    private Vector2     normDir;        // normalized direction vector with length 1
    private NetworkPosition netPos;       // netPos container for network transmission
    private Rectangle   playerDimensions;
    private Vector2     position;

    // Other ----------------------------------------------------------------------------------|
    private Array<PlayerComponent> observers;
    private Status status;
    private Weapon weapon;

    /* .......................................................................... CONSTRUCTORS .. */
    public PlayerAttributeContainer(
            MediaManager mediaManager,
            String spriteSheet,
            World world,
            RayHandler rayHandler,
            Status status,
            Weapon weapon,
            Vector2 initialPosition) {

        // Observers ------------------------------------------------------------------------------|
        this.observers = new Array<PlayerComponent>();

        this.status = status;
        this.weapon = weapon;

        // Geometry -------------------------------------------------------------------------------|
        this.dir       = Direction.SOUTH;
        this.direction = new Vector2(0,0);
        this.normDir   = new Vector2(0,0);
        this.position  = new Vector2(initialPosition.x, initialPosition.y);
        this.netPos = new NetworkPosition(
                new Vector3(position.x,position.y,0), Direction.SOUTH, false);
        this.playerDimensions = new Rectangle(position.x - .4f, position.x - .4f, .8f, .8f);

        // Physics --------------------------------------------------------------------------------|
        this.moving = false;

        // Light ----------------------------------------------------------------------------------|
        spriteLight = new PointLight(rayHandler, 256, new Color(1,1,1,1), 8, 0, 0);

        // Graphics -------------------------------------------------------------------------------|
        // Media
        TextureAtlas atlas = mediaManager.getTextureAtlas("textures/spritesheets/" + spriteSheet);

        // load animation textures
        playerAnims = new ArrayMap<String,Animation>();
        playerAnims.put("n", new Animation(.2f, atlas.findRegions("n")));
        playerAnims.put("e", new Animation(.2f, atlas.findRegions("e")));
        playerAnims.put("s", new Animation(.2f, atlas.findRegions("s")));
        playerAnims.put("w", new Animation(.2f, atlas.findRegions("w")));

        recentAnim    = playerAnims.get("s");
        recentIdleImg = playerAnims.get("s").getKeyFrames()[0];
        this.sprite = new Sprite(recentIdleImg);
        this.sprite.setSize(.8f, 1);
        this.sprite.setPosition(position.x - sprite.getWidth()/2,
                                position.y - playerDimensions.getHeight()/2);
    }


    /* ............................................................................... METHODS .. */

    /**
     * Subscribe objects that want to get informed about changes in this container
     * @param comp
     */
    public void subscribe(PlayerComponent comp) {
        observers.add(comp);
    }

    public void notifyObservers(ContainerMessage message) {
        for(PlayerComponent comp : observers)
            comp.update(message);
    }

    /* ............................................................................... UPDATES .. */
    public void updatePosition(Vector2 position) {
        this.netPos.position.x = position.x;
        this.netPos.position.y = position.y;
        this.position.x = position.x;
        this.position.y = position.y;
        this.playerDimensions.x = position.x;
        this.playerDimensions.y = position.y;
        notifyObservers(ContainerMessage.POSITION);
    }


    /* ...................................................................... GETTERS & SETTERS . */

    public Vector2 getDirection() {
        return direction;
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public PointLight getSpriteLight() {
        return spriteLight;
    }

    public ArrayMap<String, Animation> getPlayerAnims() {
        return playerAnims;
    }

    public Animation getRecentAnim() {
        return recentAnim;
    }

    public void setRecentAnim(Animation recentAnim) {
        this.recentAnim = recentAnim;
    }

    public TextureRegion getRecentIdleImg() {
        return recentIdleImg;
    }

    public void setRecentIdleImg(TextureRegion recentIdleImg) {
        this.recentIdleImg = recentIdleImg;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        notifyObservers(ContainerMessage.DIRECTION);
        this.dir = dir;
    }

    public Vector2 getNormDir() {
        return normDir;
    }

    public NetworkPosition getNetPos() {
        return netPos;
    }

    public void setNetPos(NetworkPosition netPos) {
        this.netPos = netPos;
    }

    public Status getStatus() {
        return status;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public Rectangle getPlayerDimensions() {
        return playerDimensions;
    }

    public Vector2 getPosition() {
        return position;
    }
}
