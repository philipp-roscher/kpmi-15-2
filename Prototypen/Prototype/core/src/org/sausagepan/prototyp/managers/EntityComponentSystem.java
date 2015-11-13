package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.Maze;
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
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.SensorBodyComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.Item;
import org.sausagepan.prototyp.model.items.ItemFactory;
import org.sausagepan.prototyp.model.items.MapItem;
import org.sausagepan.prototyp.network.HeroInformation;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.TakeKeyResponse;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.LoseKeyResponse;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;

import box2dLight.RayHandler;

/**
 * Manages all {@link com.badlogic.ashley.core.Entity}s, {@link com.badlogic.ashley.core.Component}s
 * and {@link com.badlogic.ashley.core.EntitySystem}s and the
 * {@link com.badlogic.ashley.core.Engine} as well.
 * Created by georg on 29.10.15.
 */
public class EntityComponentSystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ObservableEngine engine;
    private Family characterFamily;
    private Family monsterFamily;
    private World world;
    private MediaManager mediaManager;
    private ItemFactory itemFactory;
    private OrthographicCamera camera;
    private Viewport viewport;
    private RayHandler rayHandler;
    private Maze maze;
    private ShapeRenderer shpRend;
    private HashMap<Integer,CharacterEntity> characters;

    private int localCharacterId;
    private CharacterEntity localCharacter;

    private CharacterClass characterClass;
    private int TeamId;

    private float [] [] startPos;

    /* ........................................................................... CONSTRUCTOR .. */
    public EntityComponentSystem(
            KPMIPrototype game, World world, Viewport viewport, RayHandler rayHandler, Maze maze,
            OrthographicCamera camera, CharacterClass characterClass, int TeamId) {

        this.mediaManager = game.mediaManager;
        this.itemFactory = new ItemFactory(mediaManager);
        this.world = world;
        this.camera = camera;
        this.viewport = viewport;
        this.rayHandler = rayHandler;
        this.maze = maze;
        this.shpRend = new ShapeRenderer();
        this.characterClass = characterClass;
        this.TeamId = TeamId;

        this.engine = new ObservableEngine(); // Create Engine
        this.characterFamily = Family.all(
                DynamicBodyComponent.class,
                InputComponent.class,
                CharacterSpriteComponent.class
        ).get();
        this.monsterFamily = Family.all(
                SpriteComponent.class,
                DynamicBodyComponent.class
        ).get();
        this.characters = new HashMap<Integer,CharacterEntity>();
        this.localCharacterId = game.clientId;
        
        setUpEntities();
        setUpLocalCharacterEntity();
        setUpMazeLights();
        setUpMonsters();
        setUpItems();

        // At least - not before adding entities
        setUpEntitySystems();
    }

    /* ............................................................................... METHODS .. */
    private void setUpEntities() {
        // TODO
    }

    private void setUpMonsters() {
        // Get Objects from Maps Monster Layer and add monster entities there
        for(Vector2 pos : maze.getMonsterPositions()) {
            MonsterEntity monster = new MonsterEntity();
            monster.add(new DynamicBodyComponent(
                    world, new Vector2(pos.x, pos.y), CharacterClass.MONSTER));
            monster.add(new HealthComponent(20));
            monster.add(new CharacterSpriteComponent(mediaManager.getTextureAtlas(
                    "textures/spritesheets/monsters/zombie_01.pack"), CharacterClass.MONSTER));
            monster.add(new InjurableAreaComponent(pos.x, pos.y, .8f, 1f));
            //same Team as GM -> no friendly fire
            monster.add(new TeamComponent(0));
            monster.add(new SensorBodyComponent(world, new Vector2(pos.x, pos.y)));

            this.engine.addEntity(monster);
        }
        // TODO
    }

    private void setUpItems() {
        // Get Objects from Maps Monster Layer and add monster entities there
        for(MapItem mi : maze.getMapItems()) {
            Entity itemEntity = new Entity();
            Item item = itemFactory.createMapItem(mi.type, mi.value);
            itemEntity.add(new ItemComponent(item));
            itemEntity.add(new InjurableAreaComponent(mi.position.x, mi.position.y, 1f, 1f));
            SpriteComponent sprite = new SpriteComponent();
            sprite.sprite = new Sprite(item.itemImg);
            sprite.sprite.setPosition(mi.position.x, mi.position.y);
            sprite.sprite.setSize(1f, 1f);
            sprite.sprite.setOriginCenter();
            itemEntity.add(sprite);
            System.out.println("Item: " + mi.type + " " + mi.value + " " + mi.position);
            this.engine.addEntity(itemEntity);
        }
    }

    private void setUpEntitySystems() {
        // Movement System
        MovementSystem movementSystem = new MovementSystem();
        movementSystem.addedToEngine(engine);

        // Sprite System
        SpriteSystem spriteSystem = new SpriteSystem(maze);
        spriteSystem.addedToEngine(engine);

        // Weapon System
        WeaponSystem weaponSystem = new WeaponSystem();
        weaponSystem.addedToEngine(engine);

        // Input System
        InputSystem inputSystem = new InputSystem(viewport);
        inputSystem.addedToEngine(engine);

        // Character Sprite System
        CharacterSpriteSystem characterSpriteSystem = new CharacterSpriteSystem();
        characterSpriteSystem.addedToEngine(engine);

        // Position Synchro System
        PositionSynchroSystem positionSynchroSystem = new PositionSynchroSystem();
        positionSynchroSystem.addedToEngine(engine);

        // Network System
        NetworkSystem networkSystem = new NetworkSystem();
        networkSystem.addedToEngine(engine);

        // Debugging System
        VisualDebuggingSystem visualDebuggingSystem
                = new VisualDebuggingSystem(shpRend, camera, maze);
        visualDebuggingSystem.addedToEngine(engine);

        // Battle System
        BattleSystem battleSystem = new BattleSystem(engine);
        battleSystem.addedToEngine(engine);

        //Inventory System
        InventorySystem inventorySystem = new InventorySystem(maze);
        inventorySystem.addedToEngine(engine);

        // Bullet System
        BulletSystem bulletSystem = new BulletSystem(engine, maze);
        bulletSystem.addedToEngine(engine);

        // Ingame UI System
        InGameUISystem inGameUISystem
                = new InGameUISystem(mediaManager, characterClass);
        inGameUISystem.addedToEngine(engine);

        // Item System
        ItemSystem itemSystem = new ItemSystem();
        itemSystem.addedToEngine(engine);

        // Adding them to the Engine
        this.engine.addSystem(movementSystem);
        this.engine.addSystem(spriteSystem);
        this.engine.addSystem(weaponSystem);
        this.engine.addSystem(characterSpriteSystem);
        this.engine.addSystem(inputSystem);
        this.engine.addSystem(positionSynchroSystem);
        this.engine.addSystem(networkSystem);
        this.engine.addSystem(visualDebuggingSystem);
        this.engine.addSystem(battleSystem);
        this.engine.addSystem(inventorySystem);
        this.engine.addSystem(bulletSystem);
        this.engine.addSystem(inGameUISystem);
        this.engine.addSystem(itemSystem);
    }

    public void setUpMazeLights() {
        // Get Objects from Maps Light Layer and add light entities there
        for(Vector2 pos : maze.getLightPositions()) {
            Entity torch = new Entity().add(
                    new LightComponent(rayHandler,pos.x, pos.y ,new Color(1,.8f,.5f, 1),20,2));
            engine.addEntity(torch);
        }
        for(Vector2 pos : maze.getGameMasterSecretPositions()) {
            Entity torch = new Entity().add(
                    new LightComponent(rayHandler,pos.x, pos.y ,new Color(0,1,0,1),20,2));
            engine.addEntity(torch);
        }
        LightSystem lightSystem = new LightSystem(rayHandler);
        lightSystem.addedToEngine(engine);
        engine.addSystem(lightSystem);
    }

    public void update(float delta) {
        engine.update(delta);
    }

    public void draw() {
        this.engine.getSystem(InGameUISystem.class).draw();
    }

	public CharacterEntity addNewCharacter(NewHeroResponse request) {
		return addNewCharacter(request.playerId, request.teamId, request.hero);
	}

    /**
     * Sets up the entity for the local players character, adding components and setting up
     * according entity system.
     */
    private void setUpLocalCharacterEntity() {
        // Create Entity
        this.localCharacter = setUpCharacterEntity(characterClass);

        // Add Components
        localCharacter.add(new NetworkTransmissionComponent());
        localCharacter.add(new TeamComponent(TeamId));
        localCharacter.add(new NetworkComponent());
        localCharacter.add(new IdComponent(localCharacterId));

        //Set Spawn locations: Game master
        if (TeamId == 0) {
            localCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.5f), characterClass));
        }
        if (TeamId == 1) {
            localCharacter.add(new DynamicBodyComponent(world, new Vector2(32*.5f, 32*3.5f), characterClass));
        }

        if (TeamId == 2) {
            localCharacter.add(new DynamicBodyComponent(world, new Vector2(32*6.5f, 32*3.5f), characterClass));
        }

        characters.put(localCharacterId, localCharacter);
        this.engine.addEntity(localCharacter);
    }

    /**
     * Adds other (network-) players characters to the world
     * @param newCharacterId
     * @param teamId 
     * @param newHero
     * @return
     */
	public CharacterEntity addNewCharacter(int newCharacterId, int newCharacterTeamId, HeroInformation newHero) {		
		// Create Entity
        CharacterEntity newCharacter = setUpCharacterEntity(newHero.clientClass);

        // Add Components
        newCharacter.add(new NetworkTransmissionComponent());
        newCharacter.add(new IdComponent(newCharacterId));
        newCharacter.add(new TeamComponent(newCharacterTeamId));

        //Set Spawn locations: Game master
        if (newCharacterId == 0) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.5f), characterClass));
        }
        if (newCharacterId == 1) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*.5f, 32*3.5f), characterClass));
        }

        if (newCharacterId == 2) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*6.5f, 32*3.5f), characterClass));
        }
        
        characters.put(newCharacterId, newCharacter);
        this.engine.addEntity(newCharacter);
        return newCharacter;
	}

    /**
     * Creates a generic {@link CharacterEntity} without {@link NetworkComponent} or
     * {@link NetworkTransmissionComponent}
     * @return
     */
    private CharacterEntity setUpCharacterEntity(CharacterClass characterClass) {
        // Create Entity
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
                characterEntity.add(new CharacterSpriteComponent(mediaManager.getTextureAtlas(
                        "textures/spritesheets/characters/knight_m.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createSmallSword()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case FIGHTER_M:
                characterEntity.add(new CharacterSpriteComponent(mediaManager.getTextureAtlas(
                        "textures/spritesheets/characters/fighter_m.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createSmallSword()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case ARCHER_F:
                characterEntity.add(new CharacterSpriteComponent(mediaManager.getTextureAtlas(
                        "textures/spritesheets/characters/archer_f.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createBow()));
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case SHAMAN_M:
                characterEntity.add(new CharacterSpriteComponent(mediaManager.getTextureAtlas(
                        "textures/spritesheets/characters/shaman_m.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather())); //TODO: weapon?
                characterEntity.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
                break;
            case DRAGON:
                characterEntity.add(new CharacterSpriteComponent(mediaManager.getTextureAtlas(
                        "textures/spritesheets/characters/dragon_red.pack"), characterClass));
                characterEntity.add(new WeaponComponent(itemFactory.createFireBreather()));
                characterEntity.add(new InjurableAreaComponent(32*2.5f, 32*.6f, .8f*2, 1f*2));
                //has to be *2 here and added in CharacterSpriteComponent and DynamicBodyComponent
                maze.openSecretPassages();  // opens passages for game master
                break;
            default: break;
        }


        return characterEntity;
    }
	
	public void updatePosition(int id, NetworkTransmissionComponent position) {
		if(characters.get(id) != null) {
			this.characters.get(id)
                    .getComponent(DynamicBodyComponent.class)
                    .dynamicBody
                    .setTransform(position.position, 0f);
			this.characters.get(id).getComponent(DynamicBodyComponent.class)
                    .dynamicBody.setLinearVelocity(position.linearVelocity);
			if(position.direction != null)
				this.characters.get(id).getComponent(InputComponent.class).direction
                        = position.direction;
		}
	}

	public void deleteCharacter(int id) {
		if(characters.get(id) != null) {
//			System.out.println("Character wird gelöscht: " +id);
			engine.removeEntity(this.characters.get(id));
			this.characters.remove(id);
		}
	}
	
	public void attack(int id) {
		if(characters.get(id) != null) {
//			System.out.println("Character greift an: " + id);
			this.characters.get(id).getComponent(InputComponent.class).weaponDrawn = true;
//			this.characters.get(id).getComponent(WeaponComponent.class).weapon.justUsed = true;
		}
	}

	public void stopAttacking(int id) {
		if(characters.get(id) != null) {
//			System.out.println("Character bricht Angriff ab: "+id);
			this.characters.get(id).getComponent(InputComponent.class).weaponDrawn = false;
//			this.characters.get(id).getComponent(WeaponComponent.class).weapon.justUsed = true;
		}
	}
	
	public void shoot(ShootResponse sr) {
		if(characters.get(sr.playerId) != null) {
//			System.out.println("Character schießt: "+sr.playerId);
			((Bow)this.characters.get(sr.playerId).getComponent(WeaponComponent.class).weapon).shoot(sr.position, sr.direction);
		}
	}

	public void updateHP(HPUpdateResponse result) {
		if(characters.get(result.playerId) != null)
			this.characters.get(result.playerId).getComponent(HealthComponent.class).HP = result.HP;		
	}

	public CharacterEntity getCharacter(int playerId) {
		if(characters.get(playerId) != null)
			return characters.get(playerId);
		
		return null;
	}

	public void loseKey(LoseKeyResponse result) {
		if(characters.get(result.id) != null) {
			characters.get(result.id).getComponent(InventoryComponent.class).dropAllItems();
		}
	}

	public void takeKey(TakeKeyResponse result) {
        if (characters.get(result.id) != null) {
			characters.get(result.id).getComponent(InventoryComponent.class)
                    .pickUpItem(itemFactory.createKeyFragment(result.keySection), 1);
		}
	}
	
    /* ..................................................................... GETTERS & SETTERS .. */
    public CharacterEntity getLocalCharacterEntity() {
        return localCharacter;
    }

    public InputProcessor getInputProcessor() {
        InputSystem inputSystem = this.engine.getSystem(InputSystem.class);
        return inputSystem;
    }
}
