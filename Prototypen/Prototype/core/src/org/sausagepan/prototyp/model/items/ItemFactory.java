package org.sausagepan.prototyp.model.items;

import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.managers.MediaManager;
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
     * Creates a key fragment {@link Item}
     * @return
     */
    public KeyFragmentItem createKeyFragment(int nr) {
        KeyFragmentItem keyFrag = new KeyFragmentItem(
                mediaManager.getTextureAtlasType("IngameUI").findRegion("key", nr), nr);
        return keyFrag;
    }

    /**
     * Creates items for the map, use it for item creation from tiled maps
     * @param itemType
     * @param value
     * @return
     */
    public Item createMapItem(ItemType itemType, int value) {
        Item item = null;
        switch(itemType) {
            case POTION_HP: item = new PotionHP(
                    mediaManager.getTextureAtlasType("items").findRegion("potion_red"), value);
                break;
            case POTION_MP: item = new PotionMP(
                    mediaManager.getTextureAtlasType("items").findRegion("potion_green"), value);
                break;
            case KEY: item = createKeyFragment(value);
                break;
            default: break;
        }

        return item;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
