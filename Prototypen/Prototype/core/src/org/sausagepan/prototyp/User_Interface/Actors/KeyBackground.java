package org.sausagepan.prototyp.User_Interface.Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Bettina on 02.11.2015.
 */
public class KeyBackground extends Actor {

    Texture texture = new Texture(Gdx.files.internal("textures/User Interface/backgroundKey.png"));

    @Override
    public void draw(Batch batch, float alpha)
    {
      batch.draw(texture, Gdx.graphics.getWidth() - texture.getWidth(), Gdx.graphics.getHeight() - texture.getHeight(), texture.getWidth(), texture.getHeight());
    }
}
