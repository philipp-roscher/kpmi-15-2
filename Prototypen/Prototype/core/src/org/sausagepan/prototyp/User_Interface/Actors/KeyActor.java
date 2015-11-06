package org.sausagepan.prototyp.User_Interface.Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.model.Key;

/**
 * Created by Bettina on 02.11.2015.
 */
public class KeyActor extends Actor {

    private Texture texture;
    private KeySection keySection;

    public KeyActor(Texture texture, KeySection keySection)
    {
        this.texture = texture;
        this.keySection = keySection;
    }

    @Override
    public void draw(Batch batch, float alpha)
    {
        batch.draw(texture, Gdx.graphics.getWidth() - texture.getWidth() -1, Gdx.graphics.getHeight() - texture.getHeight() -1, texture.getWidth(), texture.getHeight());
    }
}
