package org.sausagepan.prototyp.User_Interface.Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.sausagepan.prototyp.model.Key;

/**
 * Created by Bettina on 02.11.2015.
 */
public class KeyActor extends Actor {

    private Key key;
    private Texture texture;

    public KeyActor(Key key)
    {
        this.key = key;
        switch(key.getKeySection())
        {
            case PartOne: texture = new Texture(Gdx.files.internal("textures/User Interface/KeyPartOne.png")); break;
            case PartTwo: texture = new Texture(Gdx.files.internal("textures/User Interface/KeyPartTwo.png")); break;
            case PartThree: texture = new Texture(Gdx.files.internal("textures/User Interface/KeyPartThree.png")); break;
        }
    }

    @Override
    public void draw(Batch batch, float alpha)
    {
        batch.draw(texture, Gdx.graphics.getWidth() - texture.getWidth()*2 -2, Gdx.graphics.getHeight() - texture.getHeight()*2 -2, texture.getWidth()*2, texture.getHeight()*2);
    }
}
