package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.VelocityComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;

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
    private Family characterFamily;
    private Family monsterFamily;
    private World world;
    private MediaManager mediaManager;
    private Viewport viewport;
    private RayHandler rayHandler;
    private Maze maze;
    private SpriteBatch batch;

    private CharacterEntity localCharacter;

    /* ........................................................................... CONSTRUCTOR .. */
    public EntityComponentSystem(
            KPMIPrototype game, World world, Viewport viewport,
            RayHandler rayHandler, Maze maze) {
        this.mediaManager = game.mediaManager;
        this.world = world;
        this.viewport = viewport;
        this.rayHandler = rayHandler;
        this.maze = maze;
        this.batch = game.batch;

        this.engine = new Engine(); // Create Engine
        this.characterFamily = Family.all(
                DynamicBodyComponent.class,
                InputComponent.class,
                CharacterSpriteComponent.class
        ).get();
        this.monsterFamily = Family.all(
                SpriteComponent.class,
                DynamicBodyComponent.class
        ).get();

        setUpEntities();
        setUpLocalCharacterEntity();
        setUpEntitySystems();
    }

    /* ............................................................................... METHODS .. */
    private void setUpEntities() {
        // Monsters
        Entity monsterEntity = new Entity();
        monsterEntity.add(new DynamicBodyComponent(world, new Vector2(10.0f, 10.0f)));
        monsterEntity.add(new VelocityComponent());
        monsterEntity.add(new SpriteComponent());
        TextureAtlas atlas = mediaManager.getTextureAtlas("textures/spritesheets/knight_m.pack");
        monsterEntity.getComponent(SpriteComponent.class).sprite.setRegion(atlas
                .findRegion("n", 1));
        this.engine.addEntity(monsterEntity);

    }

    private void setUpEntitySystems() {
        // Movement System
        MovementSystem movementSystem = new MovementSystem();
        movementSystem.addedToEngine(engine);

        // Sprite System
        SpriteSystem spriteSystem = new SpriteSystem(batch, maze);
        spriteSystem.addedToEngine(engine);

        // Weapon System
        WeaponSystem weaponSystem = new WeaponSystem();
        weaponSystem.addedToEngine(engine);

        // Input System
        InputSystem inputSystem = new InputSystem();
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

        // Adding them to the Engine
        this.engine.addSystem(movementSystem);
        this.engine.addSystem(spriteSystem);
        this.engine.addSystem(weaponSystem);
        this.engine.addSystem(characterSpriteSystem);
        this.engine.addSystem(inputSystem);
        this.engine.addSystem(positionSynchroSystem);
        this.engine.addSystem(networkSystem);
    }

    /**
     * Sets up the entity for the local players character, adding components and setting up
     * according entity system.
     */
    private void setUpLocalCharacterEntity() {
        // Create Entity
        this.localCharacter = new CharacterEntity();

        // Add Components
        localCharacter.add(new DynamicBodyComponent(world, new Vector2(32*2.5f, 32*.6f)));
        localCharacter.add(new CharacterSpriteComponent(
                mediaManager.getTextureAtlas("textures/spritesheets/knight_m.pack")
        ));
        localCharacter.add(new InputComponent(viewport));
        localCharacter.add(new WeaponComponent(mediaManager.getTextureAtlasType("weapons").findRegion("sword")
        ));
        localCharacter.add(new LightComponent(rayHandler));
        localCharacter.add(new NetworkTransmissionComponent());

        this.engine.addEntity(localCharacter);
    }

    public void update(float delta) {
        engine.update(delta);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
    public CharacterEntity getLocalCharacterEntity() {
        return localCharacter;
    }
}
