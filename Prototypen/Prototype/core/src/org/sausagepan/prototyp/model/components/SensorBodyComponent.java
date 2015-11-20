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
 * Created by Sara on 10.11.15.
 */
public class SensorBodyComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public float x = 0.0f;
    public float y = 0.0f;
    public Body sensorBody;
    public Fixture fixture;
        public Vector2 direction;

    /* ........................................................................... CONSTRUCTOR .. */
    public SensorBodyComponent(World world, Vector2 startPosition) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;    // set up body definition for player
        bodyDef.position.set(startPosition.x, startPosition.y); // set players bodys position
        sensorBody = world.createBody(bodyDef);        // add body to the world
        CircleShape circle = new CircleShape();         // give body a shape

            circle.setRadius(5.0f);                    // set the shapes radius

        FixtureDef fixDef = new FixtureDef();           // create players fixture
        fixDef.shape       = circle;                    // give shape to fixture
        fixDef.isSensor = true;                         //makes it a sensor
        fixture = sensorBody.createFixture(fixDef);    // add fixture to body
        circle.dispose();
        direction = new Vector2(0,-1);
    }


    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
