package org.sausagepan.prototyp.model.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.Bullet;
import org.sausagepan.prototyp.model.Status;
import org.sausagepan.prototyp.model.Weapon;
import org.sausagepan.prototyp.model.components.PlayerPhysicsComponent;
import org.sausagepan.prototyp.network.NetworkPosition;

import box2dLight.PointLight;

/**
 * Server characters must not have an
 * {@link org.sausagepan.prototyp.model.components.InputComponent} as this would cause the
 * {@link org.sausagepan.prototyp.managers.InputSystem} to process them
 */
public class ServerCharacterEntity extends CharacterEntity {
	private int ID;

    public ServerCharacterEntity(int ID) {
        super();
        this.ID = ID;
    }
}
