package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.network.Network.PositionUpdate;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkSystem extends ObservingEntitySystem{
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private PositionUpdate posUpdate = new PositionUpdate();

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
            
            // send PositionUpdate (every tick)
            networkTransmissionComponent.moving     = input.moving;
            networkTransmissionComponent.direction  = input.direction;
            networkTransmissionComponent.linearVelocity = body.dynamicBody.getLinearVelocity();
            networkTransmissionComponent.position   = body.dynamicBody.getPosition();
            posUpdate.playerId = network.id;
            posUpdate.position = networkTransmissionComponent;
            network.client.sendUDP(posUpdate);
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
