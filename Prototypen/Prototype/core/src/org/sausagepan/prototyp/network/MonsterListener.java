package org.sausagepan.prototyp.network;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.MonsterEntity;

/**
 * Created by Sara on 10.11.2015.
 */
public class MonsterListener extends EntitySystem implements ContactListener {
    @Override
    public void endContact(Contact contact) {
        //stop following and go back to walking in circles
        if(!contact.getFixtureA().getBody().equals(contact.getFixtureB().getBody())) {
            if (contact.getFixtureA().getBody().getUserData() instanceof MonsterEntity
                    && contact.getFixtureB().getBody().getUserData() instanceof CharacterEntity) {
                Entity detectingEntity = (MonsterEntity)contact.getFixtureA().getBody().getUserData();
                Entity detectedEntity = (CharacterEntity)contact.getFixtureB().getBody().getUserData();

                System.out.println("Involved Monster: " + detectingEntity.getComponent
                        (IdComponent.class).id);
                System.out.println("Involved Character: " + detectedEntity);
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {

        if(!(contact.getFixtureA().getBody().getUserData() instanceof MonsterEntity) &&
                !(contact.getFixtureB().getBody().getUserData() instanceof MonsterEntity) &&
                !(contact.getFixtureA().getBody().getUserData() == null) &&
                !(contact.getFixtureB().getBody().getUserData() == null)){
            System.out.println(contact.getFixtureA().getBody().getUserData().getClass());
            System.out.println(contact.getFixtureB().getBody().getUserData().getClass());
        }

        // get Entities involved in contact
        // ignore self detecting entities
        if(!contact.getFixtureA().getBody().equals(contact.getFixtureB().getBody())) {
            if(contact.getFixtureA().getBody().getUserData() instanceof MonsterEntity
                    &&contact.getFixtureB().getBody().getUserData() instanceof CharacterEntity) {
                Entity detectingEntity = (MonsterEntity)contact.getFixtureA().getBody().getUserData();
                Entity detectedEntity = (CharacterEntity)contact.getFixtureB().getBody().getUserData();

                System.out.println("Involved Monster: " + detectingEntity.getComponent
                        (IdComponent.class).id);
                System.out.println("Involved Character: " + detectedEntity);
            }
        } else {
            return;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }


}
