package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by georg on 21.10.15.
 */
public class DynamicBodyComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public float x = 0.0f;
    public float y = 0.0f;
    public Body dynamicBody;
    public Fixture fixture;

    /* ........................................................................... CONSTRUCTOR .. */
    public DynamicBodyComponent(World world, Vector2 startPosition) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;    // set up body definition for player
        bodyDef.position.set(startPosition.x, startPosition.y); // set players bodys position
        dynamicBody = world.createBody(bodyDef);        // add body to the world
        CircleShape circle = new CircleShape();         // give body a shape
        circle.setRadius(.4f);                          // set the shapes radius
        FixtureDef fixDef = new FixtureDef();           // create players fixture
        fixDef.shape       = circle;                    // give shape to fixture
        fixDef.density     = 0.5f;                      // objects density
        fixDef.friction    = 0.4f;                      // objects friction on other objects
        fixDef.restitution = 0.0f;                      // bouncing
        fixture = dynamicBody.createFixture(fixDef);    // add fixture to body
        circle.dispose();
    }


    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
