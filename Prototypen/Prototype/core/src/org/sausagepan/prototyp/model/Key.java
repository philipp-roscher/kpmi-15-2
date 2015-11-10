package org.sausagepan.prototyp.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import org.sausagepan.prototyp.User_Interface.Actors.KeyActor;
import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.graphics.EntitySprite;

/**
 * Created by Bettina on 02.11.2015.
 */
public class Key {

    /*..................................................................................Attributes*/
    private KeySection keySection;
    private KeyActor keyActor;
    private EntitySprite sprite;
    private Rectangle collider;

    /*.................................................................................Constructor*/
    public Key(KeySection keySection, TextureAtlas.AtlasRegion region, KeyActor keyActor)
    {
        this.keySection = keySection;
        this.keyActor = keyActor;
        this.sprite = new EntitySprite(region);
        this.sprite.setSize(this.sprite.getWidth() * 0.02f, this.sprite.getHeight() * 0.02f);
        this.sprite.visible = false;
        this.collider = new Rectangle(this.sprite.getX(), this.sprite.getY(), this.sprite.getWidth() *2, this.sprite.getHeight() *2);
    }

    /*...........................................................................Getters & Setters*/
    public KeySection getKeySection() { return this.keySection; }

    public EntitySprite getSprite() { return this.sprite; }

    public Rectangle getCollider() { return this.collider; }

    public KeyActor getKeyActor() { return this.keyActor;}



}
