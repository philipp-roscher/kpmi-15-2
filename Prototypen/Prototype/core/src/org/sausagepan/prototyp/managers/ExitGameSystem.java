package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;

import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.ServerNetworkTransmissionComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.items.KeyFragmentItem;
import org.sausagepan.prototyp.model.items.PotionHP;
import org.sausagepan.prototyp.network.Network;

import java.util.Iterator;

/**
 * Created by Sara on 15.12.2015.
 */
public class ExitGameSystem  extends EntitySystem implements EntityListener {
    /* ............................................................................ ATTRIBUTES .. */
    private ComponentMapper<InjurableAreaComponent> am
            = ComponentMapper.getFor(InjurableAreaComponent.class);

    private ImmutableArray<Entity> characters;
    private ImmutableArray<Rectangle> exitWays;
    private ServerNetworkTransmissionComponent ntc;
    private ServerEntityComponentSystem ECS;


    /* ........................................................................... CONSTRUCTOR .. */
    public ExitGameSystem(ServerEntityComponentSystem ECS) {
        this.ECS = ECS;
        ntc = ECS.getSNTC();

    }

    /* ............................................................................... METHODS .. */
    @Override
    public void addedToEngine(Engine engine) {
        characters = engine.getEntitiesFor(EntityFamilies.serverCharacterFamily);
    }

    public void update(float deltaTime)
    {
        for (Entity entity : characters) {

            InjurableAreaComponent area  = am.get(entity);

            /*while (itemIterator.hasNext()) {
                Entity item = itemIterator.next();
                if(area.area.overlaps(am.get(item).area)) {
                    System.out.println("Character "+ entity.getComponent(IdComponent.class).id +" picked up Item: " + itemM.get(item).type + "(" +item.getComponent(IdComponent.class).id + ")");
                    ntc.networkMessagesToProcess.add(new Network.ItemPickUp(entity.getComponent(IdComponent.class).id, item.getComponent(IdComponent.class).id));


                    ECS.deleteItem(item.getComponent(IdComponent.class).id);
                }
            }*/
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        addedToEngine(this.getEngine());
    }

    @Override
    public void entityRemoved(Entity entity) {
        addedToEngine(this.getEngine());
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
