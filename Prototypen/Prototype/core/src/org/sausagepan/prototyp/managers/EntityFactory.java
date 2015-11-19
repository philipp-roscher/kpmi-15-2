package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.enums.MazeObjectType;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.SensorBodyComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.items.Item;
import org.sausagepan.prototyp.model.items.ItemFactory;
import org.sausagepan.prototyp.model.items.MapItem;

import box2dLight.RayHandler;

/**
 * This class creates and returns {@link Entity}s for use in the game world
 * - monsters
 * - characters
 * - lights
 * - items
 * Created by georg on 19.11.15.
 */
public class EntityFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private MediaManager media;
    private World world;
    private ItemFactory itemFactory;
    private RayHandler rayHandler;
    /* ........................................................................... CONSTRUCTOR .. */

    public EntityFactory(MediaManager media, World world, RayHandler rayHandler) {
        this.media = media;
        this.world = world;
        this.itemFactory = new ItemFactory(media);
        this.rayHandler = rayHandler;
    }
    /* ............................................................................... METHODS .. */

    /**
     * Creates a {@link MonsterEntity} for the game world
     * @return
     */
    public MonsterEntity createMonster(float x, float y, CharacterClass characterClass) {
        MonsterEntity monster = new MonsterEntity();
        monster.add(new DynamicBodyComponent(world, new Vector2(x,y), CharacterClass.MONSTER));
        monster.add(new InjurableAreaComponent(x, y, .8f, 1f));
        //same Team as GM -> no friendly fire
        monster.add(new TeamComponent(0));
        monster.add(new SensorBodyComponent(world, new Vector2(x, y)));

        int health;
        TextureAtlas tex;

        switch(characterClass) {
            case MONSTER_ZOMBIE:
                health = 20;
                tex = media.getTextureAtlas("textures/spritesheets/monsters/zombie_01.pack");
                break;
            case MONSTER_SKELETON:
                health = 15;
                tex = media.getTextureAtlas("textures/spritesheets/monsters/skeleton.pack");
                break;
            default:
                health = 20;
                tex = media.getTextureAtlas("textures/spritesheets/monsters/zombie_01.pack");
                break;
        }

        monster.add(new HealthComponent(health));
        monster.add(new CharacterSpriteComponent(tex, CharacterClass.MONSTER));

        return  monster;
    }

    /**
     * Creates a {@link Entity} for the game world
     * @return
     */
    public Entity createItem(MapItem mapItem) {
        Entity itemEntity = new Entity();
        Item item = itemFactory.createMapItem(mapItem.type, mapItem.value);
        itemEntity.add(new ItemComponent(item));
        itemEntity.add(new InjurableAreaComponent(mapItem.position.x, mapItem.position.y, 1f, 1f));
        SpriteComponent sprite = new SpriteComponent();
        sprite.sprite = new Sprite(item.itemImg);
        sprite.sprite.setPosition(mapItem.position.x, mapItem.position.y);
        sprite.sprite.setSize(1f, 1f);
        sprite.sprite.setOriginCenter();
        itemEntity.add(sprite);
        if(GlobalSettings.DEBUGGING_ACTIVE)
            System.out.println(
                    "Item: " + mapItem.type + " " + mapItem.value + " " + mapItem.position);

        return itemEntity;
    }

    public Entity createLight(float x, float y, MazeObjectType type) {

        Entity light = new Entity();
        Color color;

        switch(type) {
            case LIGHT_SECRET: color = new Color(0,1,0,1); break;
            default: color = new Color(1,.8f,.5f, 1); break;
        }

        light.add(new LightComponent(rayHandler, x, y, color, 20, 2));

        if(GlobalSettings.DEBUGGING_ACTIVE)
            System.out.println("Light: " + x + "|" + y);

        return light;
    }

    /**
     * Creates a generic {@link CharacterEntity} without {@link NetworkComponent} or
     * {@link NetworkTransmissionComponent}
     * @return
     */
    public CharacterEntity createCharacter(CharacterClass characterClass) {
        CharacterEntity characterEntity = new CharacterEntity();

        // Add Components
        characterEntity.add(new InputComponent());
        characterEntity.add(new LightComponent(rayHandler));

        // Add components which are equal for all classes
        characterEntity.add(new HealthComponent(100));
        characterEntity.add(new MagicComponent(80));
        characterEntity.add(new InventoryComponent());

        // Add class specific components
        switch(characterClass) {
            case KNIGHT_M:
                characterEntity.add(new CharacterSpriteComponent(media.getTextureAtlas(
                        "textures/spritesheets/characters/knight_m.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createSmallSword()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case FIGHTER_M:
                characterEntity.add(new CharacterSpriteComponent(media.getTextureAtlas(
                        "textures/spritesheets/characters/fighter_m.pack"), characterClass));
                characterEntity.add(
                        new WeaponComponent(itemFactory.createBoxerGlove(ItemType.GLOVE_RED)));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case ARCHER_F:
                characterEntity.add(new CharacterSpriteComponent(media.getTextureAtlas(
                        "textures/spritesheets/characters/archer_f.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createBow()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case SHAMAN_M:
                characterEntity.add(new CharacterSpriteComponent(media.getTextureAtlas(
                        "textures/spritesheets/characters/shaman_m.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather())); //TODO: weapon?
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case DRAGON:
                characterEntity.add(new CharacterSpriteComponent(media.getTextureAtlas(
                        "textures/spritesheets/characters/dragon_red.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f * 2, 1f * 2));
                //has to be *2 here and added in CharacterSpriteComponent and DynamicBodyComponent
                break;
            default: break;
        }


        return characterEntity;
    }
    /* ..................................................................... GETTERS & SETTERS .. */

}
