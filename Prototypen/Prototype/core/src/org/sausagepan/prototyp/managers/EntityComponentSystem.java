package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
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
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.entities.ItemEntity;
import org.sausagepan.prototyp.model.entities.MapCharacterObject;
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
    private Engine engine;
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
    private HashMap<Integer,ItemEntity> items;
    private KPMIPrototype game;

    private EntityFactory entityFactory;

    private int localCharacterId;
    private CharacterEntity localCharacter;

    private CharacterClass characterClass;
    private int TeamId;

    private float[][] startPositions;

    /* ........................................................................... CONSTRUCTOR .. */
    public EntityComponentSystem(
            KPMIPrototype game, World world, Viewport viewport, RayHandler rayHandler, Maze maze,
            OrthographicCamera camera, CharacterClass characterClass, int TeamId) {

    	this.game = game;
        this.mediaManager = game.mediaManager;
        this.itemFactory = new ItemFactory(mediaManager);
        this.world = world;
        this.camera = camera;
        this.viewport = viewport;
        this.rayHandler = rayHandler;
        this.maze = maze;
        this.startPositions = maze.getStartPositions();
        this.shpRend = new ShapeRenderer();
        this.characterClass = characterClass;
        this.TeamId = TeamId;

        this.engine = new Engine(); // Create Engine
        this.characters = new HashMap<Integer,CharacterEntity>();
        this.monsters = new HashMap<Integer,MonsterEntity>();
        this.items = new HashMap<Integer,ItemEntity>();
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
        engine.addEntityListener(Family.all(WeaponComponent.class,NetworkTransmissionComponent.class).get(), weaponSystem);

        // Input System
        InputSystem inputSystem = new InputSystem(viewport);
        inputSystem.addedToEngine(engine);

        // Character Sprite System
        CharacterSpriteSystem characterSpriteSystem = new CharacterSpriteSystem(this);
        characterSpriteSystem.addedToEngine(engine);
        engine.addEntityListener(Family.all(
                CharacterSpriteComponent.class,
                DynamicBodyComponent.class).get(), characterSpriteSystem);
        
        // Position Synchro System
        PositionSynchroSystem positionSynchroSystem = new PositionSynchroSystem();
        positionSynchroSystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.positionSynchroFamily, positionSynchroSystem);

        // Network System
        NetworkSystem networkSystem = new NetworkSystem(this);
        networkSystem.addedToEngine(engine);

        // Debugging System
        VisualDebuggingSystem visualDebuggingSystem
                = new VisualDebuggingSystem(shpRend, camera, maze);
        visualDebuggingSystem.addedToEngine(engine);
        engine.addEntityListener(Family.all(HealthComponent.class,DynamicBodyComponent.class,InjurableAreaComponent.class).get(), visualDebuggingSystem);

        // Inventory System
        InventorySystem inventorySystem = new InventorySystem(maze, getLocalCharacterEntity());
        inventorySystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.characterFamily, inventorySystem);

        // Bullet System
        BulletSystem bulletSystem = new BulletSystem(maze);
        bulletSystem.addedToEngine(engine);
        engine.addEntityListener(Family.all(WeaponComponent.class).get(), bulletSystem);

        // Ingame UI System
        InGameUISystem inGameUISystem
                = new InGameUISystem(mediaManager, characterClass, game);
        inGameUISystem.addedToEngine(engine);

        // Adding them to the Engine
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
        // Get Objects from FullGameStateResponse Monster HashMap and add monster entities there
        for(HashMap.Entry<Integer,MapMonsterObject> mapObject : mapMonsterObjects.entrySet()) {
            // Using factory method for creating monsters
        	MonsterEntity monster = entityFactory.createMonster(mapObject.getValue(), mapObject.getKey());
        	monsters.put(mapObject.getKey(), monster);
            this.engine.addEntity(monster);
        }
    }

    public void setUpItems(HashMap<Integer,MapItem> mapItems) {
        // Get Objects from FullGameStateResponse Item HashMap and add item entities there
        for(HashMap.Entry<Integer,MapItem> mapItem : mapItems.entrySet()) {
            // Using factory method for creating monsters
        	ItemEntity item = entityFactory.createItem(mapItem.getValue(), mapItem.getKey());
        	items.put(mapItem.getKey(), item);
            this.engine.addEntity(item);
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
        this.localCharacter = addNewCharacter(localCharacterId, TeamId, characterClass);

        // Add NetworkComponent
        localCharacter.add(new NetworkComponent());
        
        // opens passages for game master
        if(characterClass == CharacterClass.DRAGON) maze.openSecretPassages();
    }

    /**
     * Adds players characters to the world
     * @param newCharacterId
     * @param newCharacterTeamId
     * @param clientClass
     * @return
     */
	public CharacterEntity addNewCharacter(int newCharacterId, int newCharacterTeamId, CharacterClass clientClass) {		
		System.out.println(newCharacterId + ", " + newCharacterTeamId + ", " + clientClass);
		// Create Entity
        CharacterEntity newCharacter = entityFactory.createCharacter(clientClass);

        // Add Components
        newCharacter.add(new NetworkTransmissionComponent());
        newCharacter.add(new IdComponent(newCharacterId));
        newCharacter.add(new TeamComponent(newCharacterTeamId));

        //Set Spawn locations: Game master
        if (newCharacterTeamId == 0) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(startPositions[0][0] / 32f, startPositions[0][1] / 32f), clientClass));
        }
        if (newCharacterTeamId == 1) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(startPositions[1][0] / 32f, startPositions[1][1] / 32f), clientClass));
        }
        if (newCharacterTeamId == 2) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(startPositions[3][0] / 32f, startPositions[3][1] / 32f), clientClass));
        }
        
        characters.put(newCharacterId, newCharacter);
        this.engine.addEntity(newCharacter);
        return newCharacter;
	}
	
	public void addNewCharacter(Integer heroId, MapCharacterObject character) {
		CharacterEntity newCharacter = addNewCharacter(heroId, character.teamId, character.characterClass);
		newCharacter.getComponent(HealthComponent.class).HP = character.health;
		newCharacter.getComponent(DynamicBodyComponent.class).dynamicBody.setTransform(character.position, 0f);
		newCharacter.getComponent(InventoryComponent.class).ownKeys = character.ownKeys;
	}

	public void deleteCharacter(int id) {
		CharacterEntity character = characters.get(id);
		if(character != null) {
			world.destroyBody(character.getComponent(DynamicBodyComponent.class).dynamicBody);
			character.getComponent(LightComponent.class).spriteLight.remove();
			engine.removeEntity(character);
			this.characters.remove(id);
		}
	}
	
	public void deleteMonster(int id, boolean finalDeletion) {
		MonsterEntity monster = monsters.get(id);
		if(monster != null) {
			if(!finalDeletion) {
				// delete body only
				world.destroyBody(monster.getComponent(DynamicBodyComponent.class).dynamicBody);	
			}
			else {
				// completely delete entity
				engine.removeEntity(monster);
				this.monsters.remove(id);
			}
		}
	}

	public void deleteItem(int id) {
		ItemEntity item = items.get(id);
		if(item != null) {
			engine.removeEntity(item);
			this.items.remove(id);
		}
	}
	
    public void createItem(int id, MapItem mapItem) {
    	ItemEntity item = entityFactory.createItem(mapItem, id);
    	items.put(id, item);
        this.engine.addEntity(item);
    }
    
	public CharacterEntity getCharacter(int playerId) {
		return characters.get(playerId);
	}

	public MonsterEntity getMonster(Integer key) {
		return monsters.get(key);
	}
	
	public ItemEntity getItem(Integer key) {
		return items.get(key);
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

    public Maze getMaze() {
        return maze;
    }
}
