package org.sausagepan.prototyp.network;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Created by Sara on 10.11.2015.
 */
public abstract class MonsterListener implements ContactListener {
    @Override
    public void endContact(Contact contact) {
        //stop following and go back to walking in circles
        System.out.println("Monster lost sight of Client.");
    }

    @Override
    public void beginContact(Contact contact) {
        //get Fixtures that had contact
        Fixture A = contact.getFixtureA();
        Fixture B = contact.getFixtureB();

        System.out.println(B.getBody().getUserData());

        System.out.println(A.getBody().getUserData());





        //start following client
        System.out.println("Monster detected Client!");
    }


}