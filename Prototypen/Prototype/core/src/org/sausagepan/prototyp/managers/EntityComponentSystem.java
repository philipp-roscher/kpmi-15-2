package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.enums.MazeObjectType;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.entities.MapMonsterObject;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.items.ItemFactory;
import org.sausagepan.prototyp.model.items.MapItem;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;

import java.util.HashMap;

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
    private HashMap<Integer,MonsterEntity> monsters;
    private HashMap<Integer,Entity> items;

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
        this.monsters = new HashMap<Integer,MonsterEntity>();
        this.items = new HashMap<Integer,Entity>();
        this.localCharacterId = game.clientId;

        this.entityFactory = new EntityFactory(mediaManager, world, rayHandler);
        
        setUpLocalCharacterEntity();
        setUpMazeLights();

        // At least - not before adding entities
        setUpEntitySystems();
    }

    /* ............................................................................... METHODS .. */
    private void setUpEntitySystems() {
        // Sprite System
        SpriteSystem spriteSystem = new SpriteSystem(maze);
        spriteSystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.spriteFamily, spriteSystem);

        // Weapon System
        WeaponSystem weaponSystem = new WeaponSystem();
        weaponSystem.addedToEngine(engine);
        engine.subscribe(weaponSystem);

        // Input System
        InputSystem inputSystem = new InputSystem(viewport);
        inputSystem.addedToEngine(engine);
        engine.subscribe(inputSystem);

        // Character Sprite System
        CharacterSpriteSystem characterSpriteSystem = new CharacterSpriteSystem();
        characterSpriteSystem.addedToEngine(engine);
        engine.subscribe(characterSpriteSystem);

        // Position Synchro System
        PositionSynchroSystem positionSynchroSystem = new PositionSynchroSystem();
        positionSynchroSystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.positionSynchroFamily, positionSynchroSystem);

        // Network System
        NetworkSystem networkSystem = new NetworkSystem(this);
        networkSystem.addedToEngine(engine);
        engine.subscribe(networkSystem);

        // Debugging System
        VisualDebuggingSystem visualDebuggingSystem
                = new VisualDebuggingSystem(shpRend, camera, maze);
        visualDebuggingSystem.addedToEngine(engine);
        engine.subscribe(visualDebuggingSystem);

        //TODO: port this
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

        //TODO: port this
        // Item System
        ItemSystem itemSystem = new ItemSystem();
        itemSystem.addedToEngine(engine);
        engine.subscribe(itemSystem);

        // Light System
        LightSystem lightSystem = new LightSystem(rayHandler);
        lightSystem.addedToEngine(engine);
        engine.subscribe(lightSystem);

        // Adding them to the Engine
        //this.engine.addSystem(movementSystem);
        this.engine.addSystem(spriteSystem);
        this.engine.addSystem(weaponSystem);
        this.engine.addSystem(characterSpriteSystem);
        this.engine.addSystem(inputSystem);
        this.engine.addSystem(positionSynchroSystem);
        this.engine.addSystem(networkSystem);
        this.engine.addSystem(visualDebuggingSystem);
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

    public void setUpMonsters(HashMap<Integer,MapMonsterObject> mapMonsterObjects) {
        // Get Objects from Maps Monster Layer and add monster entities there
        for(HashMap.Entry<Integer,MapMonsterObject> mapObject : mapMonsterObjects.entrySet()) {
            // Using factory method for creating monsters
        	MonsterEntity monster = entityFactory.createMonster(mapObject.getValue());
        	monsters.put(mapObject.getKey(), monster);
            this.engine.addEntity(monster);
            maze.addCharacterSpriteComponent(monster.getComponent(CharacterSpriteComponent.class));
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
    }

	public CharacterEntity addNewCharacter(NewHeroResponse request) {
		return addNewCharacter(request.playerId, request.teamId, request.clientClass);
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

        // opens passages for game master
        if(characterClass == CharacterClass.DRAGON) maze.openSecretPassages();
        
        characters.put(localCharacterId, localCharacter);
        this.engine.addEntity(localCharacter);
    }

    /**
     * Adds other (network-) players characters to the world
     * @param newCharacterId
     * @param clientClass
     * @return
     */
	public CharacterEntity addNewCharacter(int newCharacterId, int newCharacterTeamId, CharacterClass clientClass) {		
		System.out.println(newCharacterId + ", " + newCharacterTeamId + ", " + clientClass);
		// Create Entity
        CharacterEntity newCharacter = setUpCharacterEntity(clientClass);

        // Add Components
        newCharacter.add(new NetworkTransmissionComponent());
        newCharacter.add(new IdComponent(newCharacterId));
        newCharacter.add(new TeamComponent(newCharacterTeamId));

        //Set Spawn locations: Game master
        if (newCharacterTeamId == 0) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.5f), clientClass));
        }
        if (newCharacterTeamId == 1) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*.5f, 32*3.5f), clientClass));
        }

        if (newCharacterTeamId == 2) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*6.5f, 32*3.5f), clientClass));
        }
        
        characters.put(newCharacterId, newCharacter);
        this.engine.addEntity(newCharacter);
        maze.addCharacterSpriteComponent(newCharacter.getComponent(CharacterSpriteComponent.class));
        maze.addWeaponComponent(newCharacter.getComponent(WeaponComponent.class));
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

        return characterEntity;
    }

	public void deleteCharacter(int id) {
		if(characters.get(id) != null) {
//			System.out.println("Character wird gelöscht: " +id);
			maze.removeCharacterSpriteComponent(characters.get(id).getComponent(CharacterSpriteComponent.class));
            maze.removeWeaponComponent(characters.get(id).getComponent(WeaponComponent.class));
			engine.removeEntity(this.characters.get(id));
			this.characters.remove(id);
		}
	}

	public CharacterEntity getCharacter(int playerId) {
		return characters.get(playerId);
	}

	public MonsterEntity getMonster(Integer key) {
		return monsters.get(key);
	}
	
	public void setupNetworkSystem() {
		engine.getSystem(NetworkSystem.class).setupSystem();
	}
	
    /* ..................................................................... GETTERS & SETTERS .. */
    public CharacterEntity getLocalCharacterEntity() {
        return localCharacter;
    }
    
    public ItemFactory getItemFactory() {
    	return itemFactory;
    }

    public InputProcessor getInputProcessor() {
        InputSystem inputSystem = this.engine.getSystem(InputSystem.class);
        return inputSystem;
    }
}
