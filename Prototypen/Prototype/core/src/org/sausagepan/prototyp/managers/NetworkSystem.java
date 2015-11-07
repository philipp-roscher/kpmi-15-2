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
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkSystem extends ObservingEntitySystem{
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<NetworkTransmissionComponent> ntm
            = ComponentMapper.getFor(NetworkTransmissionComponent.class);
    private ComponentMapper<NetworkComponent> nm
    = ComponentMapper.getFor(NetworkComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public NetworkSystem() {}
    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class,
                InputComponent.class,
                CharacterSpriteComponent.class,
                LightComponent.class,
                NetworkTransmissionComponent.class,
                NetworkComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            DynamicBodyComponent body = dm.get(entity);
            NetworkTransmissionComponent networkTransmissionComponent = ntm.get(entity);
            NetworkComponent network = nm.get(entity);
            InputComponent input = im.get(entity);
            networkTransmissionComponent.moving     = input.moving;
            networkTransmissionComponent.linearVelocity = body.dynamicBody.getLinearVelocity();
            networkTransmissionComponent.position   = body.dynamicBody.getPosition();
            network.posUpdate.position = networkTransmissionComponent;
            network.sendPositionUpdate();            
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
