package org.sausagepan.prototyp.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.graphics.EntitySprite;

/**
 * Created by Bettina on 02.11.2015.
 */
public class Key {

    /*..................................................................................Attributes*/
    private KeySection keySection;
    private EntitySprite sprite;
    private Rectangle collider;

    /*.................................................................................Constructor*/
    public Key(KeySection keySection, TextureAtlas.AtlasRegion region)
    {
        this.keySection = keySection;
        this.sprite = new EntitySprite(region);
        this.sprite.setSize(this.sprite.getWidth()*2, this.sprite.getHeight()*2);
        this.sprite.visible = false;
        this.collider = new Rectangle(0, 0, this.sprite.getWidth(), this.sprite.getHeight());
    }

    /*...........................................................................Getters & Setters*/
    public KeySection getKeySection() { return this.keySection; }

    public EntitySprite getSprite() { return this.sprite; }

    public Rectangle getCollider() { return this.collider; }


}
