 package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.MonsterSpawnComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.network.Network.AttackRequest;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by georg on 28.10.15.
 */
public class InputSystem extends EntitySystem implements InputProcessor {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;

    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<NetworkTransmissionComponent> ntm
			= ComponentMapper.getFor(NetworkTransmissionComponent.class);
    private ComponentMapper<IsDeadComponent> idm
			= ComponentMapper.getFor(IsDeadComponent.class);
    private ComponentMapper<MonsterSpawnComponent> mm
            = ComponentMapper.getFor(MonsterSpawnComponent.class);

    private Vector2 directionVector;
    private Vector2 normDirectionVector;

    private Stage stage;
    private final ImageButton fightButton;
    private final ImageButton spawnButton;

    private InputMultiplexer inputMultiplexer;

    /* ........................................................................... CONSTRUCTOR .. */
    public InputSystem(MediaManager media) {
        this.directionVector = new Vector2();
        this.normDirectionVector = new Vector2();

        // Scene2D
        FitViewport fit = new FitViewport(800,480);

        this.stage = new Stage(fit);

        // Buttons .................................................................................
        ImageButton.ImageButtonStyle fightButtonStyle = new ImageButton.ImageButtonStyle();
        Skin skin = new Skin(media.getTextureAtlasType("IngameUI"));
        fightButtonStyle.up = skin.getDrawable("3dswordbuttonup");
        fightButtonStyle.down = skin.getDrawable("3dswordbuttondown");
        fightButtonStyle.over = skin.getDrawable("3dswordbuttonup");
        fightButtonStyle.pressedOffsetY = -1;
        fightButton = new ImageButton(fightButtonStyle);
        fightButton.setWidth(64f);
        fightButton.setHeight(64f);
        fightButton.setPosition(36, 36);

        fightButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                InputSystem.this.keyDown(Input.Keys.A);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                InputSystem.this.keyUp(Input.Keys.A);
            }
        });

        final ImageButton.ImageButtonStyle spawnButtonStyle = new ImageButton.ImageButtonStyle();
        spawnButtonStyle.up = skin.getDrawable("3dmonsterbuttonup");
        spawnButtonStyle.down = skin.getDrawable("3dmonsterbuttondown");
        spawnButtonStyle.over = skin.getDrawable("3dmonsterbuttonup");
        spawnButtonStyle.pressedOffsetY = -1;
        spawnButton = new ImageButton(spawnButtonStyle);
        spawnButton.setWidth(64f);
        spawnButton.setHeight(64f);
        spawnButton.setPosition(136, 36);

        spawnButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                InputSystem.this.keyDown(Input.Keys.S);
                //cooldown for monster spawn
                spawnButton.addAction(Actions.sequence(Actions.visible(false), Actions.delay(GlobalSettings.MONSTER_SPAWN_COOLDOWN), Actions.visible(true)));
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                InputSystem.this.keyUp(Input.Keys.S);
            }
        });

        stage.addActor(fightButton);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(this);

        Gdx.input.setInputProcessor(inputMultiplexer);
    }
    
    /* ............................................................................... METHODS .. */
    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class,
                InputComponent.class,
                WeaponComponent.class,
                NetworkComponent.class).get() );
    }

    public void update(float deltaTime) {
        stage.act();
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            InputComponent input = im.get(entity);
            IsDeadComponent isDead = idm.get(entity);

            if(input.moving && isDead == null) move(input.touchPos, body, input);
                /* Keyboard Input */
//                switch(input.direction) {
//                    case NORTH: body.dynamicBody.setLinearVelocity(0,5);break;
//                    case SOUTH: body.dynamicBody.setLinearVelocity(0,-5);break;
//                    case EAST: body.dynamicBody.setLinearVelocity(5,0);break;
//                    case WEST: body.dynamicBody.setLinearVelocity(-5,0);break;
//                    default: body.dynamicBody.setLinearVelocity(0,0);break;
//                }
            else body.dynamicBody.setLinearVelocity(0,0);

            //so only GM sees this button
            if (entity.getComponent(TeamComponent.class).TeamId == 0) {
                stage.addActor(spawnButton);
            }
        }
    }

    /**
     * Change characters velocities in x and y direction according to the touch position
     * @param touchPos
     */
    public void move(Vector3 touchPos, DynamicBodyComponent body, InputComponent input) {

        // calculate characters main moving direction for sprite choosing
        if(Math.abs(touchPos.x - 400) > Math.abs(touchPos.y - 240)) {
            if(touchPos.x > 400) input.direction = Direction.EAST;
            else input.direction = Direction.WEST;
        } else {
            if(touchPos.y > 240) input.direction = Direction.NORTH;
            else input.direction = Direction.SOUTH;
        }

        // split up velocity vector in x and y component
        float ax = (-.1f)*(400-touchPos.x);
        float ay = (-.1f)*(240-touchPos.y);

        directionVector.x = ax;
        directionVector.y = ay;

        // normalize velocity vector
        normDirectionVector.x = (ax / Vector3.len(ax, ay, 0) * 5);
        normDirectionVector.y = (ay / Vector3.len(ax, ay, 0) * 5);

        // limit maximum velocity
        if (directionVector.len() > 4) {
            directionVector.x = normDirectionVector.x;
            directionVector.y = normDirectionVector.y;
        }

        // set velocity to zero, if below the given value
        if(directionVector.len() < 1) {
            directionVector.x = 0;
            directionVector.y = 0;
            input.moving = false;
        } else input.moving = true;
        body.direction = normDirectionVector;
        body.dynamicBody.setLinearVelocity(directionVector);
    }

    /* ....................................................................... INPUT PROCESSOR .. */
    @Override
    public boolean keyDown(int keycode) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            WeaponComponent weapon = wm.get(entity);
            DynamicBodyComponent body = pm.get(entity);
            NetworkTransmissionComponent ntc = ntm.get(entity);

            switch(keycode) {
                case Input.Keys.UP:     input.direction = Direction.NORTH;break;
                case Input.Keys.LEFT:   input.direction = Direction.WEST;break;
                case Input.Keys.RIGHT:  input.direction = Direction.EAST;break;
                case Input.Keys.DOWN:   input.direction = Direction.SOUTH;break;
                case Input.Keys.A:
                    input.weaponDrawn = true;
                    //System.out.println("Attacking!");
                    weapon.weapon.justUsed = true;
                    break;
                case Input.Keys.S:
                    //Spawn Monsters : sent current position of MG and set spawn to true
                    if (entity.getComponent(TeamComponent.class).TeamId == 0) {
                        MonsterSpawnComponent mon = mm.get(entity);
                        mon.setMasterPosition(new Vector2(
                                body.dynamicBody.getPosition().x,
                                body.dynamicBody.getPosition().y
                        ));
                        mon.monsterSpawn = true;
                        ntc.networkMessagesToProcess.add(mon);
                    }
                    break;

                default:break;
            }
            if(keycode != Input.Keys.A && keycode != Input.Keys.S) input.moving = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            NetworkTransmissionComponent ntc = ntm.get(entity);
            NetworkComponent network = CompMappers.network.get(entity);
            WeaponComponent weapon = wm.get(entity);
            if (keycode == Input.Keys.A) {
            	input.weaponDrawn = false;
            	weapon.weapon.justUsed = false;
            	ntc.networkMessagesToProcess.add(new AttackRequest(network.id, true));
            }
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            input.touchPos.x = screenX;
            input.touchPos.y = screenY;
            stage.getViewport().unproject(input.touchPos);
        }
        return touchDragged(screenX,screenY,pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            input.moving = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            input.touchPos.x = screenX;
            input.touchPos.y = screenY;
            stage.getViewport().unproject(input.touchPos);
            input.moving = true;
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void draw() {
        this.stage.getViewport().apply();
        this.stage.draw();
    }
    /* ..................................................................... GETTERS & SETTERS .. */


    public Stage getStage() {
        return stage;
    }

    public InputMultiplexer getInputMultiplexer() {
        return inputMultiplexer;
    }
}
