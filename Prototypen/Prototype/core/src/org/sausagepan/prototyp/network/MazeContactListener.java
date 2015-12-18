package org.sausagepan.prototyp.network;

import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.ChaseComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.SERVERNetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Sara on 10.11.2015.
 */
public class MazeContactListener implements ContactListener {
    private SERVERNetworkTransmissionComponent sntc;

    /**
     * Default constructor
     */
    public MazeContactListener(SERVERNetworkTransmissionComponent sntc) {
        this.sntc = sntc;
    }

    @Override
    public void endContact(Contact contact) {
    	// check if the ended contact was the contact with the player the monster was chasing
        if(contact.getFixtureA().isSensor()
    		&& !contact.getFixtureB().isSensor()
            && contact.getFixtureA().getBody().getUserData() != null
            && !(contact.getFixtureA().getBody().getUserData() instanceof String)
            && contact.getFixtureB().getBody().getUserData() != null
            && !(contact.getFixtureB().getBody().getUserData() instanceof String)
            ) {
        		ChaseComponent chase = ((Entity)contact.getFixtureA().getBody().getUserData())
	        			.getComponent(ChaseComponent.class);
        		DynamicBodyComponent body = ((Entity)contact.getFixtureA().getBody().getUserData())
	        			.getComponent(DynamicBodyComponent.class);
        		
	        	if(chase != null && body != null && chase.body.equals(body.dynamicBody))
	        		((Entity)(contact.getFixtureA().getBody().getUserData())).remove(ChaseComponent.class);        	
        }
    }

    @Override
    public void beginContact(Contact contact) {
    	Fixture FixtureA = contact.getFixtureA();
    	Fixture FixtureB = contact.getFixtureB();
        if(FixtureA.getBody().getUserData() != null
                && FixtureA.getBody().getUserData() instanceof String
                && ((String)FixtureA.getBody().getUserData()).equals("ExitSensor")) {
            System.out.println("Sending Exit Request ...");
            this.sntc.networkMessagesToProcess.add(new Network.GameExitResponse(
            		((ServerCharacterEntity)FixtureB.getBody().getUserData()).getComponent(TeamComponent.class).TeamId
            	));
            return;
        }

        // Swap the fixtures if they are in the wrong order
        // right order: FixtureA is monster, FixtureB is character
        if(FixtureA.getBody().getUserData() instanceof ServerCharacterEntity
        		&& FixtureB.getBody().getUserData() instanceof MonsterEntity) {
        	FixtureB = contact.getFixtureA();
        	FixtureA = contact.getFixtureB();
        	System.out.println("Swapped fixtures");
        }
        
        
        // Ignore if first one isn't sensor
        boolean firstOneSensor
                = FixtureA.isSensor() && !FixtureB.isSensor();

        // Ignore static bodies at contact detection
        boolean oneStatic = (FixtureA.getBody().getType() == BodyDef.BodyType.StaticBody)
                || (FixtureB.getBody().getType() == BodyDef.BodyType.StaticBody);
        // Ignore contact between monsters
        boolean bothMonsters = FixtureA.getBody().getUserData() instanceof MonsterEntity
                && (FixtureB.getBody().getUserData() instanceof MonsterEntity);
        // Ignore contact party without UserData
        boolean oneWithoutEntityReference = FixtureA.getBody().getUserData() == null
                || FixtureB.getBody().getUserData() == null;

        // If everything is okay
        System.out.println(""+firstOneSensor + oneStatic + bothMonsters + oneWithoutEntityReference);
        if( firstOneSensor && !oneStatic && !bothMonsters && !oneWithoutEntityReference) {
            boolean selfDetection = FixtureA.getBody()
                    .equals(FixtureB.getBody());
            boolean oneMonsterOneCharacter = FixtureA.getBody().getUserData()
                    instanceof MonsterEntity && FixtureB.getBody().getUserData()
                    instanceof ServerCharacterEntity;

            // get Entities involved in contact
            // ignore self detecting entities
            if (!selfDetection && oneMonsterOneCharacter) {
                Entity detectingEntity
                    = (MonsterEntity) FixtureA.getBody().getUserData();
                Entity detectedEntity
                    = (ServerCharacterEntity) FixtureB.getBody().getUserData();

                //checking for teamID so GM doesn't get chased
                if(detectedEntity.getComponent(TeamComponent.class).TeamId != 0) {
                    detectingEntity.add(new ChaseComponent(
                            detectedEntity.getComponent(DynamicBodyComponent.class).dynamicBody));

                    if(GlobalSettings.DEBUGGING_ACTIVE) {
                        System.out.println("Involved Monster: " + detectingEntity.getComponent(IdComponent.class).id);
                        System.out.println("Involved Character: " + detectedEntity.getComponent(IdComponent.class).id);
                    }
                }

            } else {
                return;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
