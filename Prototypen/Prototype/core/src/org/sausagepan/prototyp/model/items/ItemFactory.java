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
                mediaManager.getTextureAtlasType("weapons").findRegion("arrow", 1),
                "Bow"
        );
    }

    public Bow createFireBreather() {
        return new Bow(
                mediaManager.getTextureAtlasType("weapons").findRegion("fire_breath"),
                5, Damagetype.MAGICAL,
                mediaManager.getTextureAtlasType("weapons").findRegion("fireball"),
                "FireBreather"
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
                5, Damagetype.PHYSICAL,
                "SmallSword");
    }

    /**
     * Creates a mini {@link Sword} item of {@link org.sausagepan.prototyp.enums.Damagetype}
     * Physical
     * @return  the sword item
     */
    public Sword createMiniSword() {
        return new Sword(
                mediaManager.getTextureAtlasType("weapons").findRegion("sword", 2),
                1, Damagetype.PHYSICAL,
                "MiniSword");
    }

    /**
     * Creates weapon for {@link org.sausagepan.prototyp.enums.CharacterClass} FIGHTER
     * @param itemType allowed values: GLOVE_RED, GLOVE_SPIKE
     * @return
     */
    public Sword createBoxerGlove(ItemType itemType) {
        Sword glove;
        switch(itemType) {
            case GLOVE_SPIKE: glove = new Sword(mediaManager.getTextureAtlasType("weapons")
                    .findRegion("boxer_glove_spike"), 10, Damagetype.PHYSICAL, "BoxerGloveSpike");break;
            default: glove = new Sword(mediaManager.getTextureAtlasType("weapons")
            .findRegion("boxer_glove"), 10, Damagetype.PHYSICAL, "BoxerGlove");break;
        }
        return glove;
    }

    /**
     * Creates weapon for everyone
     * @return
     */
    public Sword createBareHand() {
        Sword hand;
        hand = new Sword(mediaManager.getTextureAtlasType("weapons")
                    .findRegion("bare_hand"), 3, Damagetype.PHYSICAL,
                    "BareHand");
        return hand;
    }

    public Sword createBigSword() {
        return new Sword(
                mediaManager.getTextureAtlasType("weapons").findRegion("sword", 1),
                10, Damagetype.PHYSICAL,
                "BigSword");
    }

    /**
     * Creates a key fragment {@link Item}
     * @return
     */
    public KeyFragmentItem createKeyFragment(int nr) {
        return new KeyFragmentItem(
                mediaManager.getTextureAtlasType("IngameUI").findRegion("key", nr), nr);
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
