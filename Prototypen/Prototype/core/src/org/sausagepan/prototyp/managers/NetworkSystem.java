package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkSystem extends EntitySystem{
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<NetworkTransmissionComponent> nm
            = ComponentMapper.getFor(NetworkTransmissionComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public NetworkSystem() {}
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class,
                InputComponent.class,
                CharacterSpriteComponent.class,
                LightComponent.class,
                NetworkTransmissionComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            DynamicBodyComponent body = dm.get(entity);
            NetworkTransmissionComponent networkTransmissionComponent = nm.get(entity);
            InputComponent input = im.get(entity);
            networkTransmissionComponent.direction  = input.direction;
            networkTransmissionComponent.moving     = input.moving;
            networkTransmissionComponent.position   = body.dynamicBody.getPosition();
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
