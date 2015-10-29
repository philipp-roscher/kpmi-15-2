package org.sausagepan.prototyp.model.components;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.model.ContainerMessage;
import org.sausagepan.prototyp.model.PlayerAttributeContainer;

@Deprecated
/**
 * Created by Georg on 14.07.2015.
 */
public class PlayerPhysicsComponent extends PlayerComponent {

    /* ................................................................................................ ATTRIBUTES .. */
    private Body dynBody;       // dynamic box2d body for physics
    private Fixture fixture;    // characters fixture for shape, collision, material a.s.o., child of body
    private float ax, ay;       // direction vectors components

    /* .............................................................................................. CONSTRUCTORS .. */

    public PlayerPhysicsComponent(
            PlayerAttributeContainer attributes,
            World world,
            RayHandler rayHandler) {

        super(attributes);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;    // set up body definition for player
        bodyDef.position.set(attributes.getPosition()); // set players bodys position
        dynBody = world.createBody(bodyDef);            // add body to the world
        CircleShape circle = new CircleShape();         // give body a shape
        circle.setRadius(.4f);                          // set the shapes radius
        FixtureDef fixDef = new FixtureDef();           // create players fixture
        fixDef.shape       = circle;                    // give shape to fixture
        fixDef.density     = 0.5f;                      // objects density
        fixDef.friction    = 0.4f;                      // objects friction on other objects
        fixDef.restitution = 0.0f;                      // bouncing
        fixture = dynBody.createFixture(fixDef);        // add fixture to body
        circle.dispose();
    }

    /* ................................................................................................... METHODS .. */

    @Override
    public void update(float elapsedTime) {
        // add calculated velocity to the dynamic body
        dynBody.setLinearVelocity(attributes.getDirection());
        attributes.notifyObservers(ContainerMessage.DYNAMIC_BODY);

        // move characters light
        attributes.getSpriteLight().setPosition(
                dynBody.getPosition().x,
                dynBody.getPosition().y);
        attributes.updatePosition(dynBody.getPosition());
        dynBody.setLinearVelocity(attributes.getDirection());
    }

    @Override
    public void update(ContainerMessage message) {
        // TODO
    }

    /**
     * Stop characters movement
     */
    public void stop() {
        attributes.setMoving(false);                        // for sprite
        attributes.getDirection().set(0,0);                 // set velocities to zero
        attributes.notifyObservers(ContainerMessage.DIRECTION);
    }

    /**
     * Change characters velocities in x and y direction according to the touch position
     * @param touchPos
     */
    public void update(Vector3 touchPos) {

        // calculate characters main moving direction for sprite choosing
        if(Math.abs(touchPos.x - dynBody.getPosition().x)
                > Math.abs(touchPos.y - dynBody.getPosition().y)) {
            if(touchPos.x > dynBody.getPosition().x)
                attributes.setDir(Direction.EAST);
            else
                attributes.setDir(Direction.WEST);
        } else {
            if(touchPos.y > dynBody.getPosition().y)
                attributes.setDir(Direction.NORTH);
            else
                attributes.setDir(Direction.SOUTH);
        }

        // split up velocity vector in x and y component
        ax = (-1)*(dynBody.getPosition().x-touchPos.x);
        ay = (-1)*(dynBody.getPosition().y-touchPos.y);

        attributes.getDirection().x = ax;
        attributes.getDirection().y = ay;

        // normalize velocity vector
        attributes.getNormDir().x = (ax / Vector3.len(ax, ay, 0) * 5);
        attributes.getNormDir().y = (ay / Vector3.len(ax, ay, 0) * 5);

        // limit maximum velocity
        if (attributes.getDirection().len() > 4) {
            attributes.getDirection().x = attributes.getNormDir().x;
            attributes.getDirection().y = attributes.getNormDir().y;
        }

        // set velocity to zero, if below the given value
        if(attributes.getDirection().len() < 1) {
            attributes.getDirection().x = 0;
            attributes.getDirection().y = 0;
            attributes.setMoving(false);
        } else attributes.setMoving(true);

    }

    /* ..................................................................... GETTERS & SETTERS .. */

    public Body getDynBody() {
        return dynBody;
    }
}

