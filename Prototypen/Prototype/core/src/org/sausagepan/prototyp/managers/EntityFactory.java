package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.enums.MazeObjectType;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.CharacterClassComponent;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.MonsterSpawnComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.SensorComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.ItemEntity;
import org.sausagepan.prototyp.model.entities.MapMonsterObject;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;
import org.sausagepan.prototyp.model.items.Item;
import org.sausagepan.prototyp.model.items.ItemFactory;
import org.sausagepan.prototyp.model.items.MapItem;

import box2dLight.RayHandler;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

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

    public EntityFactory(MediaManager media, World world) {
        this.media = media;
        this.world = world;
        this.itemFactory = new ItemFactory(media);
	}

	/**
     * Creates a {@link MonsterEntity} for the game world
     * @return
     */
    public MonsterEntity createMonster(MapMonsterObject mapMonsterObject, int id) {
        MonsterEntity monster = new MonsterEntity();
        monster.add(new DynamicBodyComponent(
                world, mapMonsterObject.position, CharacterClass.MONSTER, monster));
        monster.add(new InjurableAreaComponent(
                mapMonsterObject.position.x, mapMonsterObject.position.y, .8f, 1f));
        //same Team as GM -> no friendly fire
        monster.add(new IdComponent(id));
        monster.add(new TeamComponent(0));

        monster.add(new InputComponent());
        monster.add(new SensorComponent(world, monster));
        monster.add(new WeaponComponent(itemFactory.createMiniSword())); //TODO: change weapon

        int health;
        TextureAtlas tex;

        switch(mapMonsterObject.characterClass) {
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

        // if health value was already set by the server, use that value instead
        if(mapMonsterObject.health != -1)
        	health = mapMonsterObject.health;
        
        monster.add(new HealthComponent(health));
        monster.add(new CharacterSpriteComponent(tex, CharacterClass.MONSTER));
        monster.add(new CharacterClassComponent(mapMonsterObject.characterClass));

        if(GlobalSettings.DEBUGGING_ACTIVE)
            System.out.println(
                    "Monster (ID " + id + "): " + mapMonsterObject.characterClass + " " + mapMonsterObject.position);
        
        return monster;
    }
    
    /**
     * Creates a {@link ItemEntity} for the game world
     * @param id
     * @return
     */
    public ItemEntity createItem(MapItem mapItem, int id) {
        ItemEntity itemEntity = new ItemEntity();
        Item item = itemFactory.createMapItem(mapItem.type, mapItem.value);
        itemEntity.add(new ItemComponent(item, mapItem.type, mapItem.value));
        itemEntity.add(new InjurableAreaComponent(mapItem.position.x, mapItem.position.y, 1f, 1f));
        itemEntity.add(new IdComponent(id));
        SpriteComponent sprite = new SpriteComponent();
        sprite.sprite = new Sprite(item.itemImg);
        sprite.sprite.setPosition(mapItem.position.x-.5f, mapItem.position.y-.5f);
        sprite.sprite.setSize(1f, 1f);
        sprite.sprite.setOriginCenter();
        itemEntity.add(sprite);
        if(GlobalSettings.DEBUGGING_ACTIVE)
            System.out.println(
                    "Item (ID " + id + "): " + mapItem.type + " " + mapItem.value + " " + mapItem.position);

        return itemEntity;
    }

    public Entity createLight(float x, float y, MazeObjectType type) {

        Entity light = new Entity();
        Color color;

        switch(type) {
            case LIGHT_SECRET: color = new Color(0,1,0,1); break;
            default: color = new Color(1,.8f,.5f, 1); break;
        }

        if(rayHandler != null)
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
        characterEntity.add(new CharacterClassComponent(characterClass));

        // Add components which are equal for all classes
        characterEntity.add(new HealthComponent(100));
        characterEntity.add(new MagicComponent(80));
        InventoryComponent ic = new InventoryComponent();
        ic.items.add(itemFactory.createMapItem(ItemType.POTION_HP, 10));
        ic.items.add(itemFactory.createMapItem(ItemType.POTION_HP, 10));
        ic.items.add(itemFactory.createMapItem(ItemType.POTION_MP, 10));
        characterEntity.add(ic);

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
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather())); //TODO: weapon
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case WITCH_F:
                characterEntity.add(new CharacterSpriteComponent(media.getTextureAtlas(
                        "textures/spritesheets/characters/witch_f.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather())); //TODO: weapon
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case NINJA_F:
                characterEntity.add(new CharacterSpriteComponent(media.getTextureAtlas(
                        "textures/spritesheets/characters/ninja_f.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createBow())); //TODO: weapon
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case DRAGON:
                characterEntity.add(new CharacterSpriteComponent(media.getTextureAtlas(
                        "textures/spritesheets/characters/dragon_red.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f * 2, 1f * 2));
                //has to be *2 here and added in CharacterSpriteComponent and DynamicBodyComponent
                characterEntity.add(new MonsterSpawnComponent());
                break;
            default: break;
        }


        return characterEntity;
    }
    /* ..................................................................... GETTERS & SETTERS .. */

	public ServerCharacterEntity createServerCharacter(CharacterClass characterClass) {
        ServerCharacterEntity characterEntity = new ServerCharacterEntity();

        // Add Components
        characterEntity.add(new InputComponent());

        // Add components which are equal for all classes
        characterEntity.add(new HealthComponent(100));
        characterEntity.add(new MagicComponent(80));
        characterEntity.add(new InventoryComponent());

        // Add class specific components
        switch(characterClass) {
            case KNIGHT_M:
                characterEntity.add(new WeaponComponent(itemFactory.createSmallSword()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case FIGHTER_M:
                characterEntity.add(new WeaponComponent(itemFactory.createBoxerGlove(ItemType.GLOVE_RED)));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case ARCHER_F:
                characterEntity.add(new WeaponComponent(itemFactory.createBow()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case SHAMAN_M:
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather())); //TODO: weapon?
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case WITCH_F:
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather())); //TODO: weapon
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case NINJA_F:
                characterEntity.add(new WeaponComponent(itemFactory.createBow())); //TODO: weapon
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case DRAGON:
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f * 2, 1f * 2));
                //has to be *2 here and added in DynamicBodyComponent
                break;
            default: break;
        }

        return characterEntity;
	}

}
