package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by georg on 28.10.15.
 */
public class InputSystem extends EntitySystem implements InputProcessor {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime = 0;

    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<NetworkComponent> nm
    		= ComponentMapper.getFor(NetworkComponent.class);
    private ComponentMapper<NetworkTransmissionComponent> ntm
			= ComponentMapper.getFor(NetworkTransmissionComponent.class);


    private float ax, ay;
    private Vector2 directionVector;
    private Vector2 normDirectionVector;

    private Viewport viewport;

    /* ........................................................................... CONSTRUCTOR .. */
    public InputSystem(Viewport viewport) {
        this.directionVector = new Vector2();
        this.normDirectionVector = new Vector2();
        this.viewport = viewport;
        Gdx.input.setInputProcessor(this);
    }
    
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class,
                InputComponent.class,
                WeaponComponent.class,
                NetworkComponent.class).get());
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            InputComponent input = im.get(entity);

            if(input.moving) move(input.touchPos, body, input);
                /* Keyboard Input */
//                switch(input.direction) {
//                    case NORTH: body.dynamicBody.setLinearVelocity(0,5);break;
//                    case SOUTH: body.dynamicBody.setLinearVelocity(0,-5);break;
//                    case EAST: body.dynamicBody.setLinearVelocity(5,0);break;
//                    case WEST: body.dynamicBody.setLinearVelocity(-5,0);break;
//                    default: body.dynamicBody.setLinearVelocity(0,0);break;
//                }
            else body.dynamicBody.setLinearVelocity(0,0);
        }
    }

    /**
     * Change characters velocities in x and y direction according to the touch position
     * @param touchPos
     */
    public void move(Vector3 touchPos, DynamicBodyComponent body, InputComponent input) {

        // calculate characters main moving direction for sprite choosing
        if(Math.abs(touchPos.x - body.dynamicBody.getPosition().x)
                > Math.abs(touchPos.y - body.dynamicBody.getPosition().y)) {
            if(touchPos.x > body.dynamicBody.getPosition().x) input.direction = Direction.EAST;
            else input.direction = Direction.WEST;
        } else {
            if(touchPos.y > body.dynamicBody.getPosition().y) input.direction = Direction.NORTH;
            else input.direction = Direction.SOUTH;
        }

        // split up velocity vector in x and y component
        ax = (-1)*(body.dynamicBody.getPosition().x-touchPos.x);
        ay = (-1)*(body.dynamicBody.getPosition().y-touchPos.y);

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
                default:break;
            }
            if(keycode != Input.Keys.A) input.moving = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            NetworkTransmissionComponent ntc = ntm.get(entity);
            WeaponComponent weapon = wm.get(entity);
            if (keycode == Input.Keys.A) {
            	input.weaponDrawn = false;
            	weapon.weapon.justUsed = false;
            	ntc.stopAttacking = true;
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
            viewport.unproject(input.touchPos);
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
    /* ..................................................................... GETTERS & SETTERS .. */
}
