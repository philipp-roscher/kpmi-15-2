package org.sausagepan.prototyp.network;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.ChaseComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;

/**
 * Created by Sara on 10.11.2015.
 */
public class MonsterListener extends EntitySystem implements ContactListener {
    @Override
    public void endContact(Contact contact) {
        if(contact.getFixtureA().isSensor() &&
                contact.getFixtureA().getBody().getUserData() != null)
            ((Entity)(contact.getFixtureA().getBody().getUserData())).remove(ChaseComponent.class);
    }

    @Override
    public void beginContact(Contact contact) {

        // Ignore if first one isn't sensor
        boolean firstOneSensor
                = contact.getFixtureA().isSensor() && !contact.getFixtureB().isSensor();

        // Ignore static bodies at contact detection
        boolean oneStatic = (contact.getFixtureA().getBody().getType() == BodyDef.BodyType.StaticBody)
                || (contact.getFixtureB().getBody().getType() == BodyDef.BodyType.StaticBody);
        // Ignore contact between monsters
        boolean bothMonsters = contact.getFixtureA().getBody().getUserData() instanceof MonsterEntity
                && (contact.getFixtureB().getBody().getUserData() instanceof MonsterEntity);
        // Ignore contact party without UserData
        boolean oneWithoutEntityReference = contact.getFixtureA().getBody().getUserData() == null
                || contact.getFixtureB().getBody().getUserData() == null;

        // If everything is okay
        if( firstOneSensor && !oneStatic && !bothMonsters && !oneWithoutEntityReference) {
            boolean selfDetection = contact.getFixtureA().getBody()
                    .equals(contact.getFixtureB().getBody());
            boolean oneMonsterOneCharacter = contact.getFixtureA().getBody().getUserData()
                    instanceof MonsterEntity && contact.getFixtureB().getBody().getUserData()
                    instanceof ServerCharacterEntity;

            // get Entities involved in contact
            // ignore self detecting entities
            if (!selfDetection && oneMonsterOneCharacter) {
                Entity detectingEntity
                    = (MonsterEntity) contact.getFixtureA().getBody().getUserData();
                Entity detectedEntity
                    = (ServerCharacterEntity) contact.getFixtureB().getBody().getUserData();

                detectingEntity.add(new ChaseComponent(
                        detectedEntity.getComponent(DynamicBodyComponent.class).dynamicBody));

                if(GlobalSettings.DEBUGGING_ACTIVE) {
                    System.out.println("Involved Monster: " + detectingEntity.getComponent(IdComponent.class).id);
                    System.out.println("Involved Character: " + detectedEntity);
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
