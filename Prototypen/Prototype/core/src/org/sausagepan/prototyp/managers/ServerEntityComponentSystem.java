package org.sausagepan.prototyp.managers;

import java.util.HashMap;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.ServerSettings;
import org.sausagepan.prototyp.model.components.CharacterClassComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.entities.MapMonsterObject;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;
import org.sausagepan.prototyp.model.items.ItemFactory;
import org.sausagepan.prototyp.model.items.MapItem;
import org.sausagepan.prototyp.network.GameServer;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.MapInformation;
import org.sausagepan.prototyp.network.Network.NetworkPosition;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Server;

/**
 * Manages all {@link com.badlogic.ashley.core.Entity}s, {@link com.badlogic.ashley.core.Component}s
 * and {@link com.badlogic.ashley.core.EntitySystem}s and the
 * {@link com.badlogic.ashley.core.Engine} as well.
 * Created by georg on 29.10.15.
 */
public class ServerEntityComponentSystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ObservableEngine engine;
    private World world;
    private ItemFactory itemFactory;
    private MediaManager mediaManager;
    private Maze maze;
    private HashMap<Integer,ServerCharacterEntity> characters;
    private HashMap<Integer,MonsterEntity> monsters;
    private HashMap<Integer,Entity> items;
    private GameServer gameServer;
    private Server server;
    private float tickrate = ServerSettings.TICKRATE;
    
    private EntityFactory entityFactory;

    /* ........................................................................... CONSTRUCTOR .. */
    public ServerEntityComponentSystem(MapInformation mapInformation, Server server, GameServer gameServer) {
    	Box2D.init();
        this.mediaManager = new MediaManager();
        this.itemFactory = new ItemFactory(mediaManager);
        this.world = new World(new Vector2(0,0), true);
        this.maze = new Maze(mapInformation, world);

        this.engine = new ObservableEngine(); // Create Engine
        this.characters = new HashMap<Integer,ServerCharacterEntity>();
        this.monsters = new HashMap<Integer,MonsterEntity>();
        this.items = new HashMap<Integer,Entity>();

        this.entityFactory = new EntityFactory(mediaManager, world);
        this.server = server;
        this.gameServer = gameServer;

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

        // Weapon System
        WeaponSystem weaponSystem = new WeaponSystem();
        weaponSystem.addedToEngine(engine);
        engine.subscribe(weaponSystem);

        // Position Synchro System
        PositionSynchroSystem positionSynchroSystem = new PositionSynchroSystem();
        positionSynchroSystem.addedToEngine(engine);
        engine.subscribe(positionSynchroSystem);

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

        // Item System
        ItemSystem itemSystem = new ItemSystem();
        itemSystem.addedToEngine(engine);
        engine.subscribe(itemSystem);

        // Network System
        ServerNetworkSystem networkSystem = new ServerNetworkSystem(this, server, gameServer);
        
        // Adding them to the Engine
        this.engine.addSystem(movementSystem);
        this.engine.addSystem(weaponSystem);
        this.engine.addSystem(positionSynchroSystem);
        this.engine.addSystem(battleSystem);
        this.engine.addSystem(inventorySystem);
        this.engine.addSystem(bulletSystem);
        this.engine.addSystem(itemSystem);
        this.engine.addSystem(networkSystem);
    }

    private void setUpMonsters() {
    	int i = 1;
        // Get Objects from Maps Monster Layer and add monster entities there
        for(MapMonsterObject mapObject : maze.getMapMonsterObjects()) {
            // Using factory method for creating monsters
        	MonsterEntity monster = entityFactory.createMonster(mapObject);
        	monsters.put(i++, monster);
            this.engine.addEntity(monster);
        }
        // TODO
    }

    private void setUpItems() {
    	int i = 1;
        // Get Objects from Maps Monster Layer and add monster entities there
        for(MapItem mi : maze.getMapItems()) {
        	Entity item = entityFactory.createItem(mi);
        	items.put(i++, entityFactory.createItem(mi));
            this.engine.addEntity(item);
        }
    }

    public void update(float delta) {
        engine.update(delta);
        world.step(1 / tickrate, 6, 2);    // time step at which world is updated
    }

	public ServerCharacterEntity addNewCharacter(NewHeroResponse request) {
		return addNewCharacter(request.playerId, request.teamId, request.clientClass);
	}

    /**
     * Adds other (network-) players characters to the world
     * @param newCharacterId
     * @param clientClass
     * @return
     */
	public ServerCharacterEntity addNewCharacter(int newCharacterId, int newCharacterTeamId, CharacterClass clientClass) {		
		System.out.println(newCharacterId + ", " + newCharacterTeamId + ", " + clientClass);
		// Create Entity
        ServerCharacterEntity newCharacter = setUpCharacterEntity(clientClass);

        // Add Components
        newCharacter.add(new IdComponent(newCharacterId));
        newCharacter.add(new TeamComponent(newCharacterTeamId));
        newCharacter.add(new CharacterClassComponent(clientClass));
        
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
        return newCharacter;
	}

    /**
     * Creates a generic {@link CharacterEntity} without {@link NetworkComponent} or
     * {@link NetworkTransmissionComponent}
     * @return
     */
    private ServerCharacterEntity setUpCharacterEntity(CharacterClass characterClass) {
        // Create Entity
        ServerCharacterEntity characterEntity = entityFactory.createServerCharacter(characterClass);

        return characterEntity;
    }

	public void deleteCharacter(int id) {
		if(characters.get(id) != null) {
//			System.out.println("Character wird gelöscht: " +id);
			engine.removeEntity(this.characters.get(id));
			this.characters.remove(id);
		}
	}

	public ServerCharacterEntity getCharacter(int playerId) {
		return characters.get(playerId);
	}
	
    /* ..................................................................... GETTERS & SETTERS .. */    
    public ItemFactory getItemFactory() {
    	return itemFactory;
    }

	public GameStateResponse getGameState() {
		HashMap<Integer,NetworkPosition> characters = new HashMap<Integer,NetworkPosition>();
		for(HashMap.Entry<Integer,ServerCharacterEntity> character : this.characters.entrySet()) {
			NetworkPosition np = new NetworkPosition();
		    np.moving     = character.getValue().getComponent(InputComponent.class).moving;
		    np.direction  = character.getValue().getComponent(InputComponent.class).direction;
		    np.velocity   = character.getValue().getComponent(DynamicBodyComponent.class).dynamicBody.getLinearVelocity();
		    np.position   = character.getValue().getComponent(DynamicBodyComponent.class).dynamicBody.getPosition();
			characters.put(
					character.getKey(),
					np
				);
		}
		
		HashMap<Integer,NetworkPosition> monsters = new HashMap<Integer,NetworkPosition>();
		for(HashMap.Entry<Integer,MonsterEntity> monster : this.monsters.entrySet()) {
			NetworkPosition np = new NetworkPosition();
		    np.velocity   = monster.getValue().getComponent(DynamicBodyComponent.class).dynamicBody.getLinearVelocity();
		    np.position   = monster.getValue().getComponent(DynamicBodyComponent.class).dynamicBody.getPosition();
			monsters.put(
					monster.getKey(),
					np
				);
		}
		
		GameStateResponse result = new GameStateResponse();
		result.characters = characters;
		result.monsters = monsters;
		
		return result;
	}

	public void updatePosition(int playerId, NetworkPosition position) {
		ServerCharacterEntity character = characters.get(playerId);
	    character.getComponent(DynamicBodyComponent.class)
        											.dynamicBody
        											.setTransform(position.position, 0f);
	    character.getComponent(DynamicBodyComponent.class)
        											.dynamicBody
        											.setLinearVelocity(position.velocity);
	    if(position.direction != null)
	    	character.getComponent(InputComponent.class).direction = position.direction;
	}

	public FullGameStateResponse generateFullGameStateResponse() {
		HashMap<Integer,CharacterClass> heroes = new HashMap<Integer,CharacterClass>();
		HashMap<Integer,MapMonsterObject> monsters = new HashMap<Integer,MapMonsterObject>(); 
		HashMap<Integer,Integer> teamAssignments = new HashMap<Integer,Integer>();
		
		for(HashMap.Entry<Integer,ServerCharacterEntity> c : characters.entrySet()) {
			heroes.put(c.getKey(), c.getValue().getComponent(CharacterClassComponent.class).characterClass);
			teamAssignments.put(c.getKey(), c.getValue().getComponent(TeamComponent.class).TeamId);
		}
		
		for(HashMap.Entry<Integer,MonsterEntity> m : this.monsters.entrySet()) {
			monsters.put(m.getKey(), m.getValue().createClientInformation());
		}
		
		return new FullGameStateResponse(heroes, monsters, teamAssignments);
	}	
}
