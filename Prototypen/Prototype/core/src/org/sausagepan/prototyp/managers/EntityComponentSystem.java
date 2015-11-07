package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.ashley.core.EntitySystem;

import java.util.HashMap;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.KeyViewerComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.ItemFactory;
import org.sausagepan.prototyp.network.HeroInformation;
import org.sausagepan.prototyp.network.NetworkPosition;
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

    private String clientClass;
    private int TeamId;

    /* ........................................................................... CONSTRUCTOR .. */
    public EntityComponentSystem(
            KPMIPrototype game, World world, Viewport viewport,
            RayHandler rayHandler, Maze maze, OrthographicCamera camera, String clientClass, int TeamId) {
        this.mediaManager = game.mediaManager;
        this.itemFactory = new ItemFactory(mediaManager);
        this.world = world;
        this.camera = camera;
        this.viewport = viewport;
        this.rayHandler = rayHandler;
        this.maze = maze;
        this.shpRend = new ShapeRenderer();
        this.clientClass = clientClass;
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
        setUpEntitySystems();
        setUpInventory();
    }

    /* ............................................................................... METHODS .. */
    private void setUpEntities() {
        // TODO
    }

    private void setUpMonsters() {
        // Get Objects from Maps Monster Layer and add monster entities there
        for(Vector2 pos : maze.getMonsterPositions()) {
            MonsterEntity monster = new MonsterEntity();
            monster.add(new DynamicBodyComponent(world, new Vector2(pos.x, pos.y), "monster"));
            monster.add(new HealthComponent(20));
            monster.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/monsters/zombie_01.pack"), "monster"
            ));
            monster.add(new InjurableAreaComponent(pos.x, pos.y, .8f, 1f));

            this.engine.addEntity(monster);
            System.out.println("Added monster at (" + pos.x + "|" + pos.y + ")");
        }
        // TODO
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
        VisualDebuggingSystem visualDebuggingSystem = new VisualDebuggingSystem(shpRend, camera);
        visualDebuggingSystem.addedToEngine(engine);

        // Battle System
        BattleSystem battleSystem = new BattleSystem(engine);
        battleSystem.addedToEngine(engine);

        //Inventory System
        InventorySystem inventorySystem = new InventorySystem();
        inventorySystem.addedToEngine(engine);

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
    }

    /**
     * Sets up the entity for the local players character, adding components and setting up
     * according entity system.
     */
    private void setUpLocalCharacterEntity() {
        // Create Entity
        this.localCharacter = new CharacterEntity();

        // Add Components
        //TODO: add further if-circle(s) to choose character class (Sara)
        localCharacter.add(new InputComponent());
        localCharacter.add(new LightComponent(rayHandler));
        localCharacter.add(new NetworkTransmissionComponent());
        localCharacter.add(new TeamComponent(TeamId));
        localCharacter.add(new NetworkComponent());

        if (clientClass.equals("knight_m")) {
            localCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), clientClass));
            localCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/knight_m.pack"), clientClass
            ));
            localCharacter.add(new WeaponComponent(itemFactory.createSmallSword()));
            localCharacter.add(new HealthComponent(100));
            localCharacter.add(new MagicComponent(80));
            localCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
            localCharacter.add(new InventoryComponent());
            localCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        if (clientClass.equals("fighter_m")) {
            localCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), clientClass));
            localCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/fighter_m.pack"), clientClass
            ));
            localCharacter.add(new WeaponComponent(itemFactory.createSmallSword()));
            localCharacter.add(new HealthComponent(100));
            localCharacter.add(new MagicComponent(80));
            localCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
            localCharacter.add(new InventoryComponent());
            localCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        if (clientClass.equals("archer_f")) {
            localCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), clientClass));
            localCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/archer_f.pack"), clientClass
            ));
            localCharacter.add(new WeaponComponent(itemFactory.createBow()));
            localCharacter.add(new HealthComponent(100));
            localCharacter.add(new MagicComponent(80));
            localCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
            localCharacter.add(new InventoryComponent());
            localCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        if (clientClass.equals("shaman_m")) {
            localCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), clientClass));
            localCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/shaman_m.pack"), clientClass
            ));
            localCharacter.add(new WeaponComponent(itemFactory.createFireBreather())); //TODO: weapon?
            localCharacter.add(new HealthComponent(100));
            localCharacter.add(new MagicComponent(80));
            localCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
            localCharacter.add(new InventoryComponent());
            localCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        if (clientClass.equals("dragon_red")) {
            localCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), clientClass));
            localCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/dragon_red.pack"), clientClass
            ));
            localCharacter.add(new WeaponComponent(itemFactory.createFireBreather()));
            localCharacter.add(new HealthComponent(100));
            localCharacter.add(new MagicComponent(80));
            localCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f * 2, 1f * 2));   //has to be *2 here and added in CharacterSpriteComponent and DynamicBodyComponent
            localCharacter.add(new InventoryComponent());
            localCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        characters.put(localCharacterId, localCharacter);
        this.engine.addEntity(localCharacter);
    }

    public void setUpMazeLights() {
        // Get Objects from Maps Light Layer and add light entities there
        for(Vector2 pos : maze.getLightPositions()) {
            Entity torch = new Entity().add(
                    new LightComponent(rayHandler,pos.x, pos.y ,new Color(1,.8f,.5f, 1),20,2));
            engine.addEntity(torch);
        }
        LightSystem lightSystem = new LightSystem(rayHandler);
        lightSystem.addedToEngine(engine);
        engine.addSystem(lightSystem);
    }

    //InventorySystem functions
    public void setUpInventory()
    {
        InventorySystem is = engine.getSystem(InventorySystem.class);
        //Zuerst wird die Waffe ins Inventar hinzugefuegt
        is.setWeaponInInventory();
        //Dann werden die Schluesseltraeger ausgewaehlt und ein Schluesselteil zugewiesen
        is.setUpKeyBags();
    }

    public void update(float delta) {

        engine.update(delta);
        engine.getSystem(InventorySystem.class).drawKeys();
    }

	public CharacterEntity addNewCharacter(NewHeroResponse request) {
		return addNewCharacter(request.playerId, request.hero);
	}
		
	public CharacterEntity addNewCharacter(int newCharacterId, HeroInformation newHero) {		
		// Create Entity
        CharacterEntity newCharacter = new CharacterEntity();

        // Add Components
        //TODO: add further if-circle(s) to choose character class (Sara)
        newCharacter.add(new LightComponent(rayHandler));
        newCharacter.add(new NetworkTransmissionComponent());
        newCharacter.add(new TeamComponent(TeamId));

        if (newHero.clientClass.equals("knight_m")) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), newHero.clientClass));
            newCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/knight_m.pack"), newHero.clientClass
            ));
            newCharacter.add(new WeaponComponent(itemFactory.createSmallSword()));
            newCharacter.add(new HealthComponent(100));
            newCharacter.add(new MagicComponent(80));
            newCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
            newCharacter.add(new InventoryComponent());
            newCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        if (newHero.clientClass.equals("fighter_m")) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), newHero.clientClass));
            newCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/fighter_m.pack"), newHero.clientClass
            ));
            newCharacter.add(new WeaponComponent(itemFactory.createSmallSword())); //TODO: weapon
            newCharacter.add(new HealthComponent(100));
            newCharacter.add(new MagicComponent(80));
            newCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
            newCharacter.add(new InventoryComponent());
            newCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        if (newHero.clientClass.equals("archer_f")) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), newHero.clientClass));
            newCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/archer_f.pack"), newHero.clientClass
            ));
            newCharacter.add(new WeaponComponent(itemFactory.createBow()));
            newCharacter.add(new HealthComponent(100));
            newCharacter.add(new MagicComponent(80));
            newCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
            newCharacter.add(new InventoryComponent());
            newCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        if (newHero.clientClass.equals("shaman_m")) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), newHero.clientClass));
            newCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/shaman_m.pack"), newHero.clientClass
            ));
            newCharacter.add(new WeaponComponent(itemFactory.createFireBreather())); //TODO: weapon?
            newCharacter.add(new HealthComponent(100));
            newCharacter.add(new MagicComponent(80));
            newCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f, 1f));
            newCharacter.add(new InventoryComponent());
            newCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        if (newHero.clientClass.equals("dragon_red")) {
            newCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f), newHero.clientClass));
            newCharacter.add(new CharacterSpriteComponent(
                    mediaManager.getTextureAtlas("textures/spritesheets/characters/dragon_red.pack"), newHero.clientClass
            ));
            newCharacter.add(new WeaponComponent(itemFactory.createFireBreather()));
            newCharacter.add(new HealthComponent(100));
            newCharacter.add(new MagicComponent(80));
            newCharacter.add(new InjurableAreaComponent(32 * 2.5f, 32 * .6f, .8f * 2, 1f * 2));     //has to be *2 here and added in CharacterSpriteComponent and DynamicBodyComponent
            newCharacter.add(new InventoryComponent());
            newCharacter.add(new KeyViewerComponent(maze.getTiledMapRenderer().getBatch()));
        }

        characters.put(newCharacterId, newCharacter);
        this.engine.addEntity(newCharacter);
        System.out.println("created new hero: " + newHero.clientClass + "(" + newCharacterId + ")");
        return newCharacter;
	}
	
	public void updatePosition(int id, NetworkTransmissionComponent position) {
		if(characters.get(id) != null) {
			this.characters.get(id).getComponent(DynamicBodyComponent.class).dynamicBody.setTransform(position.position, 0f);
			this.characters.get(id).getComponent(DynamicBodyComponent.class).dynamicBody.setLinearVelocity(position.linearVelocity);
		}
	}

	public void deleteCharacter(int playerId) {
		//TODO Spieler l�schen (Philipp)
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
