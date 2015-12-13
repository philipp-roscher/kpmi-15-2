package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.enums.MazeObjectType;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.entities.MapMonsterObject;
import org.sausagepan.prototyp.model.items.Bow;
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
    private World world;
    private MediaManager mediaManager;
    private ItemFactory itemFactory;
    private OrthographicCamera camera;
    private Viewport viewport;
    private RayHandler rayHandler;
    private Maze maze;
    private ShapeRenderer shpRend;
    private HashMap<Integer,CharacterEntity> characters;

    private EntityFactory entityFactory;

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
        this.characters = new HashMap<Integer,CharacterEntity>();
        this.localCharacterId = game.clientId;

        this.entityFactory = new EntityFactory(mediaManager, world, rayHandler);
        
        setUpEntities();
        setUpLocalCharacterEntity();
        setUpMazeLights();
        setUpMonsters();
        setUpItems();

        // At least - not before adding entities
        setUpEntitySystems();
    }

    /* ............................................................................... METHODS .. */
    private void setUpEntitySystems() {
        // Movement System
        MovementSystem movementSystem = new MovementSystem(world);
        movementSystem.addedToEngine(engine);
        engine.addEntityListener(Family.all(DynamicBodyComponent.class).get(), movementSystem);

        // Sprite System
        SpriteSystem spriteSystem = new SpriteSystem(maze);
        spriteSystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.spriteFamily, spriteSystem);

        // Weapon System
        WeaponSystem weaponSystem = new WeaponSystem();
        weaponSystem.addedToEngine(engine);
        engine.subscribe(weaponSystem);

        // Input System
        InputSystem inputSystem = new InputSystem(viewport, mediaManager);
        inputSystem.addedToEngine(engine);
        engine.subscribe(inputSystem);

        // Character Sprite System
        CharacterSpriteSystem characterSpriteSystem = new CharacterSpriteSystem();
        characterSpriteSystem.addedToEngine(engine);
        engine.subscribe(characterSpriteSystem);

        // Position Synchro System
        PositionSynchroSystem positionSynchroSystem = new PositionSynchroSystem();
        positionSynchroSystem.addedToEngine(engine);
        engine.subscribe(positionSynchroSystem);

        // Network System
        NetworkSystem networkSystem = new NetworkSystem();
        networkSystem.addedToEngine(engine);
        engine.subscribe(networkSystem);

        // Debugging System
        VisualDebuggingSystem visualDebuggingSystem
                = new VisualDebuggingSystem(shpRend, camera, maze);
        visualDebuggingSystem.addedToEngine(engine);
        engine.subscribe(visualDebuggingSystem);

        // Battle System
        BattleSystem battleSystem = new BattleSystem();
        battleSystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.attackerFamily, battleSystem);
        engine.addEntityListener(EntityFamilies.victimFamily, battleSystem);

        //Inventory System
        InventorySystem inventorySystem = new InventorySystem(maze);
        inventorySystem.addedToEngine(engine);
        engine.subscribe(inventorySystem);

        // Bullet System
        BulletSystem bulletSystem = new BulletSystem(engine, maze);
        bulletSystem.addedToEngine(engine);
        engine.subscribe(bulletSystem);

        // Ingame UI System
        InGameUISystem inGameUISystem
                = new InGameUISystem(mediaManager, characterClass);
        inGameUISystem.addedToEngine(engine);
        engine.subscribe(inGameUISystem);

        // Item System
        ItemSystem itemSystem = new ItemSystem(maze.getTiledMapRenderer());
        itemSystem.addedToEngine(engine);
        engine.subscribe(itemSystem);

        // Light System
        LightSystem lightSystem = new LightSystem(rayHandler);
        lightSystem.addedToEngine(engine);
        engine.subscribe(lightSystem);

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
        this.engine.addSystem(lightSystem);
    }

    public void setUpMazeLights() {
        // Get Objects from Maps Light Layer and add light entities there
        for(Vector2 pos : maze.getLightPositions()) {
            engine.addEntity(entityFactory.createLight(pos.x, pos.y, MazeObjectType.LIGHT_TORCH));
        }
        for(Vector2 pos : maze.getGameMasterSecretPositions()) {
            engine.addEntity(entityFactory.createLight(pos.x, pos.y, MazeObjectType.LIGHT_SECRET));
        }
    }

    private void setUpEntities() {
        // TODO
    }

    private void setUpMonsters() {
        // Get Objects from Maps Monster Layer and add monster entities there
        for(MapMonsterObject mapObject : maze.getMapMonsterObjects()) {
            // Using factory method for creating monsters
            this.engine.addEntity(entityFactory.createMonster(mapObject));
        }
        // TODO
    }

    private void setUpItems() {
        // Get Objects from Maps Monster Layer and add monster entities there
        for(MapItem mi : maze.getMapItems()) {
            this.engine.addEntity(entityFactory.createItem(mi));
        }
    }

    public void update(float delta) {
        engine.update(delta);
    }

    public void draw() {
        this.engine.getSystem(InGameUISystem.class).draw();
        this.engine.getSystem(InputSystem.class).draw();
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
        CharacterEntity characterEntity = entityFactory.createCharacter(characterClass);

        // opens passages for game master
        if(characterClass == CharacterClass.DRAGON) maze.openSecretPassages();

        return characterEntity;
    }

    /* TODO move stuff that doesn't belong here to the respective systems, like
       {@link NetworkSystem} */
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

    public void setInputProcessor() {
        InputSystem inputSystem = this.engine.getSystem(InputSystem.class);
        inputSystem.setInputProcessor();
    }
}
