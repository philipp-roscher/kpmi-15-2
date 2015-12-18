package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.CollisionFilter;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by georg on 21.10.15.
 */
public class DynamicBodyComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Vector2 startPosition;
    
    public Body dynamicBody;
    public Fixture fixture;
    public Vector2 direction;

    /* ........................................................................... CONSTRUCTOR .. */
    public DynamicBodyComponent(
            World world, Vector2 startPosition, CharacterClass characterClass, Entity childOf) {
    	this.startPosition = startPosition;
    	
        direction = new Vector2(0,-5);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;    // set up body definition for player
        bodyDef.position.set(startPosition.x, startPosition.y); // set players bodys position
        dynamicBody = world.createBody(bodyDef);        // add body to the world
        dynamicBody.setUserData(childOf);

        CircleShape circle = new CircleShape();         // give body a shape
        //bigger size for dragon/GM
        if(characterClass == CharacterClass.DRAGON)  circle.setRadius(.4f*2);
        else circle.setRadius(.4f);
        
        FixtureDef fixDef = new FixtureDef();           // create players fixture
        fixDef.shape       = circle;                    // give shape to fixture
        fixDef.density     = 0.5f;                      // objects density
        fixDef.friction    = 0.4f;                      // objects friction on other objects
        fixDef.restitution = 0.0f;                      // bouncing
        fixture = dynamicBody.createFixture(fixDef);    // add fixture to body
        
        Filter filter = fixture.getFilterData();
        if(childOf.getClass().equals(ServerCharacterEntity.class) || childOf.getClass().equals(CharacterEntity.class))
        	if(characterClass == CharacterClass.DRAGON)
        		filter.categoryBits = CollisionFilter.CATEGORY_GAME_MASTER;
        	else
        		filter.categoryBits = CollisionFilter.CATEGORY_PLAYER;
        fixture.setFilterData(filter);
        
        circle.dispose();
    }

    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
