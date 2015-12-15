package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Server;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.ServerSettings;
import org.sausagepan.prototyp.model.components.CharacterClassComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.ServerNetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.entities.ItemEntity;
import org.sausagepan.prototyp.model.entities.MapCharacterObject;
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

import java.util.HashMap;

/**
 * Manages all {@link com.badlogic.ashley.core.Entity}s, {@link com.badlogic.ashley.core.Component}s
 * and {@link com.badlogic.ashley.core.EntitySystem}s and the
 * {@link com.badlogic.ashley.core.Engine} on the server.
 * Created by philipp on 01.12.15.
 */
public class ServerEntityComponentSystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Engine engine;
    private World world;
    private ItemFactory itemFactory;
    private Maze maze;
    private HashMap<Integer,ServerCharacterEntity> characters;
    private HashMap<Integer,MonsterEntity> monsters;
    private HashMap<Integer,ItemEntity> items;
    private GameServer gameServer;
    private Server server;
    private float tickrate = ServerSettings.TICKRATE;
    private ServerNetworkTransmissionComponent sntc;
    private float[][] startPositions;
    private int maxItemId;
    
    private EntityFactory entityFactory;

    /* ........................................................................... CONSTRUCTOR .. */
    public ServerEntityComponentSystem(MapInformation mapInformation, Server server, GameServer gameServer) {
    	Box2D.init();
        MediaManager mediaManager = new MediaManager();
        this.itemFactory = new ItemFactory(mediaManager);
        this.world = new World(new Vector2(0,0), true);
        this.maze = new Maze(mapInformation, world, gameServer.gameReady);
        this.startPositions = maze.getStartPositions();
        maze.openSecretPassages();
        this.maxItemId = 1;

        this.engine = new Engine(); // Create Engine
        this.characters = new HashMap<Integer,ServerCharacterEntity>();
        this.monsters = new HashMap<Integer,MonsterEntity>();
        this.items = new HashMap<Integer,ItemEntity>();

        this.entityFactory = new EntityFactory(mediaManager, world);
        this.server = server;
        this.gameServer = gameServer;

        Entity networkEntity = new Entity();
        sntc = new ServerNetworkTransmissionComponent();
        networkEntity.add(sntc);
        engine.addEntity(networkEntity);

        setUpMonsters();
        setUpItems();

        // At least - not before adding entities
        setUpEntitySystems();
    }

    /* ............................................................................... METHODS .. */
    @SuppressWarnings("unchecked")
    private void setUpEntitySystems() {
        // Movement System
        MovementSystem movementSystem = new MovementSystem(world);
        movementSystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.monsterFamily, movementSystem);

        // Weapon System
        WeaponSystem weaponSystem = new WeaponSystem();
        weaponSystem.addedToEngine(engine);

        // Position Synchro System
        PositionSynchroSystem positionSynchroSystem = new PositionSynchroSystem();
        positionSynchroSystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.positionSynchroFamily, positionSynchroSystem);

        // Battle System
        BattleSystem battleSystem = new BattleSystem(this);
        battleSystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.attackerFamily, battleSystem);
        engine.addEntityListener(EntityFamilies.victimFamily, battleSystem);

        // Bullet System
        BulletSystem bulletSystem = new BulletSystem(maze);
        bulletSystem.addedToEngine(engine);
        engine.addEntityListener(Family.all(WeaponComponent.class).get(), bulletSystem);

        // Item System
        ItemSystem itemSystem = new ItemSystem(this);
        itemSystem.addedToEngine(engine);
        engine.addEntityListener(EntityFamilies.serverCharacterFamily, itemSystem);
        engine.addEntityListener(EntityFamilies.itemFamily, itemSystem);

        // Network System
        ServerNetworkSystem networkSystem = new ServerNetworkSystem(this, server, gameServer);
        networkSystem.addedToEngine(engine);
        
        // Adding them to the Engine
        this.engine.addSystem(movementSystem);
        this.engine.addSystem(networkSystem);
        this.engine.addSystem(battleSystem);
        this.engine.addSystem(weaponSystem);
        this.engine.addSystem(positionSynchroSystem);
        this.engine.addSystem(bulletSystem);
        this.engine.addSystem(itemSystem);
    }

    private void setUpMonsters() {
    	int i = 1;
        // Get Objects from Maps Monster Layer and add monster entities there
        for(MapMonsterObject mapObject : maze.getMapMonsterObjects()) {
            // Using factory method for creating monsters
        	MonsterEntity monster = entityFactory.createMonster(mapObject, i);
        	monsters.put(i, monster);
            this.engine.addEntity(monster);
            i++;
        }
        // TODO
    }

    private void setUpItems() {
        // Get Objects from Maps Monster Layer and add monster entities there
        for(MapItem mi : maze.getMapItems()) {
        	ItemEntity item = entityFactory.createItem(mi, maxItemId);
        	items.put(maxItemId++, item);
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
        newCharacter.add(new DynamicBodyComponent(world, new Vector2(startPositions[newCharacterTeamId][0] / 32f, startPositions[newCharacterTeamId][1] / 32f), clientClass, newCharacter));
        
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
        return entityFactory.createServerCharacter(characterClass);
    }

    public void deleteCharacter(int id) {
		ServerCharacterEntity character = characters.get(id);
		if(character != null) {
			world.destroyBody(character.getComponent(DynamicBodyComponent.class).dynamicBody);
			engine.removeEntity(character);
			this.characters.remove(id);
            gameServer.deleteCharacter(id);
		}
	}

    public void deleteMonster(int id) {
		MonsterEntity monster = monsters.get(id);
		if(monster != null) {
			world.destroyBody(monster.getComponent(DynamicBodyComponent.class).dynamicBody);
			engine.removeEntity(monster);
			this.monsters.remove(id);
		}
	}

    public void deleteItem(int id) {
		ItemEntity item = items.get(id);
		if(item != null) {
			engine.removeEntity(item);
			this.items.remove(id);
		}
	}
    
    public void createMonster(MapMonsterObject mmo, int id) {
    	MonsterEntity monster = entityFactory.createMonster(mmo, id);
    	monsters.put(id, monster);
        this.engine.addEntity(monster);
    }
    
    public int createItem(MapItem mapItem) {
    	ItemEntity item = entityFactory.createItem(mapItem, maxItemId);
    	items.put(maxItemId, item);
        this.engine.addEntity(item);
        return maxItemId++;
    }

	public ServerCharacterEntity getCharacter(int playerId) {
		return characters.get(playerId);
	}
	
    /* ..................................................................... GETTERS & SETTERS .. */    
    public ItemFactory getItemFactory() {
    	return itemFactory;
    }
    
    public ServerNetworkTransmissionComponent getSNTC() {
    	return sntc;
    }

	public GameStateResponse getGameState() {
		HashMap<Integer,NetworkPosition> characters = new HashMap<Integer,NetworkPosition>();
		for(HashMap.Entry<Integer,ServerCharacterEntity> character : this.characters.entrySet()) {
			NetworkPosition np = new NetworkPosition();
		    np.moving     = character.getValue().getComponent(InputComponent.class).moving;
		    np.direction  = character.getValue().getComponent(InputComponent.class).direction;
		    np.velocity   = character.getValue().getComponent(DynamicBodyComponent.class).dynamicBody.getLinearVelocity();
		    np.position   = character.getValue().getComponent(DynamicBodyComponent.class).dynamicBody.getPosition();
            np.bodyDirection    = character.getValue().getComponent(DynamicBodyComponent.class).direction;
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

	// produces a FullGameStateResponse containing all the needed information to recreate the game state in the clients
	public FullGameStateResponse generateFullGameStateResponse() {
		HashMap<Integer,MapCharacterObject> characters = new HashMap<Integer,MapCharacterObject>();
		HashMap<Integer,MapMonsterObject> monsters = new HashMap<Integer,MapMonsterObject>();
		HashMap<Integer,MapItem> items = new HashMap<Integer,MapItem>();
		
		for(HashMap.Entry<Integer,ServerCharacterEntity> c : this.characters.entrySet()) {
			characters.put(c.getKey(), c.getValue().createClientInformation());
		}
		
		for(HashMap.Entry<Integer,MonsterEntity> m : this.monsters.entrySet()) {
			monsters.put(m.getKey(), m.getValue().createClientInformation());
		}
		
		for(HashMap.Entry<Integer,ItemEntity> m : this.items.entrySet()) {
			items.put(m.getKey(), m.getValue().createClientInformation());
		}
		
		return new FullGameStateResponse(characters, monsters, items);
	}	
}
