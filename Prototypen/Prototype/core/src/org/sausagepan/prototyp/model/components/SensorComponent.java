package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.model.CollisionFilter;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by georg on 15.12.15.
 */
public class SensorComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Body sensor;
    public Fixture fixture;
    /* ........................................................................... CONSTRUCTOR .. */

    public SensorComponent(World world, Entity childOf) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;    // set up body definition for player
        bodyDef.position.set(0,0); // set players bodys position
        sensor = world.createBody(bodyDef);        // add body to the world
        sensor.setUserData(childOf);

        CircleShape circle = new CircleShape();         // give body a shape
        circle.setRadius(4);

        FixtureDef fixDef = new FixtureDef();           // create players fixture
        fixDef.shape       = circle;                           // bouncing
        fixture = sensor.createFixture(fixDef);    // add fixture to body
        fixture.setSensor(true);
        
        Filter filter = fixture.getFilterData();
        filter.categoryBits = CollisionFilter.CATEGORY_SENSOR;
        fixture.setFilterData(filter);


        circle.dispose();
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
