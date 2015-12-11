package org.sausagepan.prototyp.managers;

import java.util.Iterator;

import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.components.ServerNetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.items.KeyFragmentItem;
import org.sausagepan.prototyp.model.items.PotionHP;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.ItemPickUp;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * Created by georg on 13.11.15.
 */
public class ItemSystem extends EntitySystem implements EntityListener {
    /* ............................................................................ ATTRIBUTES .. */
    private ComponentMapper<InventoryComponent> im
            = ComponentMapper.getFor(InventoryComponent.class);
    private ComponentMapper<ItemComponent> itemM
            = ComponentMapper.getFor(ItemComponent.class);
    private ComponentMapper<InjurableAreaComponent> am
            = ComponentMapper.getFor(InjurableAreaComponent.class);
    private ComponentMapper<SpriteComponent> sm
            = ComponentMapper.getFor(SpriteComponent.class);

    private ImmutableArray<Entity> characters;
    private ImmutableArray<Entity> items;
    private ServerNetworkTransmissionComponent ntc;
    private ServerEntityComponentSystem ECS;
    /* ........................................................................... CONSTRUCTOR .. */
    public ItemSystem(ServerEntityComponentSystem ECS) {
    	this.ECS = ECS;
    	ntc = ECS.getSNTC();
    }
    
    /* ............................................................................... METHODS .. */
    @Override
    public void addedToEngine(Engine engine) {
        characters = engine.getEntitiesFor(EntityFamilies.serverCharacterFamily);
        items = engine.getEntitiesFor(EntityFamilies.itemFamily);
    }

    public void update(float deltaTime)
    {
        for (Entity entity : characters) {
            InventoryComponent inventory = im.get(entity);
            InjurableAreaComponent area  = am.get(entity);
            Iterator<Entity> itemIterator = items.iterator();
            while (itemIterator.hasNext()) {
                Entity item = itemIterator.next();
                if(area.area.overlaps(am.get(item).area)) {
                    System.out.println("Character "+ entity.getComponent(IdComponent.class).id +" picked up Item: " + itemM.get(item).item.getClass());
                    ntc.networkMessagesToProcess.add(new ItemPickUp(entity.getComponent(IdComponent.class).id, item.getComponent(IdComponent.class).id));
                    
                    // if item is key
					if(itemM.get(item).type == ItemType.KEY) {
                    	KeyFragmentItem keyFragment = (KeyFragmentItem) itemM.get(item).item;
                    	// add key to character inventory
                    	inventory.ownKeys[keyFragment.keyFragmentNr - 1] = true;
                    	// adding key to team inventory is not needed on server
					}
					
					// if item is potion
					if(itemM.get(item).type == ItemType.POTION_HP) {
                    	PotionHP potion = (PotionHP) itemM.get(item).item;
                    	HealthComponent health = entity.getComponent(HealthComponent.class);
                    	health.HP += potion.strength;
                    	// health can't surpass max HP
                    	if(health.HP > health.initialHP) health.HP = health.initialHP;
                    	ntc.networkMessagesToProcess.add(new HPUpdateResponse(entity.getComponent(IdComponent.class).id, true, health.HP));
					}
                    
                    ECS.deleteItem(item.getComponent(IdComponent.class).id);
                }
            }
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
