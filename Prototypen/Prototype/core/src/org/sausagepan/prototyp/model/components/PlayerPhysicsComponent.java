package org.sausagepan.prototyp.model.components;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.model.Bullet;
import org.sausagepan.prototyp.model.components.PlayerComponent;
import org.sausagepan.prototyp.network.Position;

/**
 * Created by Georg on 14.07.2015.
 */
public class PlayerPhysicsComponent extends PlayerComponent {

    /* ................................................................................................ ATTRIBUTES .. */

    // Geometry
    private Direction dir;                  // looking direction of character (N,S,W,E)
    private Vector2   direction;            // vector from character to touch position
    private Vector2   normDir;              // normalized direction vector with length 1
    private float     ax, ay;               // direction vectors components

    // Physics
    private boolean moving;         // whether character is moving or not
    private Body    dynBody;        // dynamic box2d body for physics
    private Fixture fixture;        // characters fixture for shape, collision, material a.s.o., child of body

    // Light
    private PointLight spriteLight; // sprites light source


    /* .............................................................................................. CONSTRUCTORS .. */

    public PlayerPhysicsComponent(World world, RayHandler rayHandler) {
        // Geometry
        this.dir       = Direction.SOUTH;
        this.direction = new Vector2(0,0);
        this.normDir   = new Vector2(0,0);

        // Physics
        this.moving = false;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;    // set up body definition for player
        bodyDef.position.set(1.1f, 1.1f);               // set players bodys position
        dynBody = world.createBody(bodyDef);            // add body to the world
        CircleShape circle = new CircleShape();         // give body a shape
        circle.setRadius(.4f);                          // set the shapes radius
        FixtureDef fixDef = new FixtureDef();           // create players fixture
        fixDef.shape       = circle;                    // give shape to fixture
        fixDef.density     = 0.5f;                      // objects density
        fixDef.friction    = 0.4f;                      // objects friction on other objects
        fixDef.restitution = 0.0f;                      // bouncing
        fixture = dynBody.createFixture(fixDef);        // add fixture to body
        circle.dispose();                               // dispose shapes

        // Light
        spriteLight = new PointLight(rayHandler, 256, new Color(1,1,1,1), 8, 0, 0);
    }

    /* ................................................................................................... METHODS .. */

    @Override
    public void update(float elapsedTime) {
        // add calculated velocity to the dynamic body
        dynBody.setLinearVelocity(direction);

        // move characters light
        spriteLight.setPosition(dynBody.getPosition().x, dynBody.getPosition().y);
    }

    /**
     * Stop characters movement
     */
    public void stop() {
        this.moving = false;                        // for sprite
        this.direction.set(0,0);                    // set velocities to zero
    }

    /**
     * Change characters velocities in x and y direction according to the touch position
     * @param touchPos
     */
    public void move(Vector3 touchPos) {

        // calculate characters main moving direction for sprite choosing
        if(Math.abs(touchPos.x - dynBody.getPosition().x) > Math.abs(touchPos.y - dynBody.getPosition().y)) {
            if (touchPos.x > dynBody.getPosition().x) dir = Direction.EAST;
            else                                      dir = Direction.WEST;
        } else {
            if(touchPos.y > dynBody.getPosition().y)  dir = Direction.NORTH;
            else                                      dir = Direction.SOUTH;
        }

        // split up velocity vector in x and y component
        ax = (-1)*(dynBody.getPosition().x-touchPos.x);
        ay = (-1)*(dynBody.getPosition().y-touchPos.y);

        direction.x = ax;
        direction.y = ay;

        // normalize velocity vector
        normDir.x = (ax / Vector3.len(ax, ay, 0) * 5);
        normDir.y = (ay / Vector3.len(ax, ay, 0) * 5);

        // limit maximum velocity
        if (direction.len() > 4) {
            direction.x = normDir.x;
            direction.y = normDir.y;
        }

        // set velocity to zero, if below the given value
        if(direction.len() < 1) {
            direction.x = 0;
            direction.y = 0;
            moving = false;
        } else moving = true;

    }

    /* ......................................................................................... GETTERS & SETTERS .. */

    public Vector2 getPosition() {
        return dynBody.getPosition();
    }


}

