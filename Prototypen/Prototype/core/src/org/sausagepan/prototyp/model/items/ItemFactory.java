package org.sausagepan.prototyp.model.items;

import org.sausagepan.prototyp.User_Interface.Actors.KeyActor;
import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.Key;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import javax.print.attribute.standard.Media;

/**
 * Created by georg on 03.11.15.
 */
public class ItemFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private MediaManager mediaManager;
    /* ........................................................................... CONSTRUCTOR .. */

    public ItemFactory(MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }
    /* ............................................................................... METHODS .. */

    /**
     * Creates a standard {@link Bow} item of {@link org.sausagepan.prototyp.enums.Damagetype}
     * Physical
     * @return  the bow item
     */
    public Bow createBow() {
        return new Bow(
                mediaManager.getTextureAtlasType("weapons").findRegion("bow", 1),
                1, Damagetype.PHYSICAL,
                mediaManager.getTextureAtlasType("weapons").findRegion("arrow", 1)
        );
    }

    public Bow createFireBreather() {
        return new Bow(
                mediaManager.getTextureAtlasType("weapons").findRegion("bow", 1),
                5, Damagetype.MAGICAL,
                mediaManager.getTextureAtlasType("weapons").findRegion("fireball")
        );
    }

    /**
     * Creates a standard {@link Sword} item of {@link org.sausagepan.prototyp.enums.Damagetype}
     * Physical
     * @return  the sword item
     */
    public Sword createSmallSword() {
        return new Sword(
                mediaManager.getTextureAtlasType("weapons").findRegion("sword", 2),
                5, Damagetype.PHYSICAL);
    }

    public Sword createBigSword() {
        return new Sword(
                mediaManager.getTextureAtlasType("weapons").findRegion("sword", 1),
                10, Damagetype.PHYSICAL);
    }
    
    /**
     * Creates a key from the given KeySection
     * @return the key
     */
    public Key createKey(KeySection keySection) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("textures/spritesheets/KeySections/keyAtlas.pack"));
        Key key;
        switch(keySection) {
	        case PartOne: key = new Key(KeySection.PartOne, atlas.findRegion("PartOne"), new KeyActor(new Texture(Gdx.files.internal("textures/User Interface/KeyPartOne.png")), KeySection.PartOne)); break;
	        case PartTwo: key = new Key(KeySection.PartTwo, atlas.findRegion("PartTwo"), new KeyActor(new Texture(Gdx.files.internal("textures/User Interface/KeyPartTwo.png")), KeySection.PartTwo)); break;
	        case PartThree: key = new Key(KeySection.PartThree, atlas.findRegion("PartThree"), new KeyActor(new Texture(Gdx.files.internal("textures/User Interface/KeyPartThree.png")), KeySection.PartThree)); break;
	        default: return null;
        }
        return key;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
