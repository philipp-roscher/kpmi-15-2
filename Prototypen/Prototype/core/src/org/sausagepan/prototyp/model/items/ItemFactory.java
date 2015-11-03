package org.sausagepan.prototyp.model.items;

import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.managers.MediaManager;

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
    /* ..................................................................... GETTERS & SETTERS .. */
}
