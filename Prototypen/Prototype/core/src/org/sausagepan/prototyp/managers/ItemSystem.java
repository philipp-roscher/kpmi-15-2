package org.sausagepan.prototyp.managers;

import java.util.Iterator;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.components.SERVERNetworkTransmissionComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.items.KeyFragmentItem;
import org.sausagepan.prototyp.model.items.MapItem;
import org.sausagepan.prototyp.network.Network.ItemPickUp;
import org.sausagepan.prototyp.network.Network.NewItem;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

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

    private ImmutableArray<Entity> characters;
    private ImmutableArray<Entity> items;
    private SERVERNetworkTransmissionComponent ntc;
    private SERVEREntityComponentSystem ECS;
    /* ........................................................................... CONSTRUCTOR .. */
    public ItemSystem(SERVEREntityComponentSystem ECS) {
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
                    //System.out.println("Character "+ entity.getComponent(IdComponent.class).id +" picked up Item: " + itemM.get(item).item.type + "(" +item.getComponent(IdComponent.class).id + ")");
                    
                    // if item is key
					if(itemM.get(item).item.type == ItemType.KEY) {
                    	KeyFragmentItem keyFragment = (KeyFragmentItem) itemM.get(item).item;
                    	// add key to character inventory
                    	inventory.ownKeys[keyFragment.keyFragmentNr - 1] = true;
                    	ntc.networkMessagesToProcess.add(new ItemPickUp(entity.getComponent(IdComponent.class).id, item.getComponent(IdComponent.class).id));
                        ECS.deleteItem(item.getComponent(IdComponent.class).id);
                    	// adding key to team inventory is not needed on server
					} else {
						if(CompMappers.inventory.get(entity).pickUpItem(item.getComponent(ItemComponent.class).item)) {
							ntc.networkMessagesToProcess.add(new ItemPickUp(entity.getComponent(IdComponent.class).id, item.getComponent(IdComponent.class).id));
		                    ECS.deleteItem(item.getComponent(IdComponent.class).id);
						}
					}
                    
                }
            }
        }
    }
    
    public void dropItems(int playerId) {
    	Entity player = ECS.getCharacter(playerId);
    	InventoryComponent inventory = CompMappers.inventory.get(player);
    	DynamicBodyComponent body = CompMappers.dynBody.get(player);
    	// create new temporary Vector2 that holds old character position
    	Vector2 position = body.dynamicBody.getPosition().cpy();

    	for(int i=0; i<3; i++) {
    		if(inventory.ownKeys[i]) {
    			// if character is in a position where the key can't be retrieved by other players,
    			// drop it at his starting position instead
    			if(ECS.getMaze().isInvalidKeyPosition(new Rectangle(position.x, position.y, 1f, 1f)))
    				position = body.startPosition.cpy().add(2f, 0f);

    			// create new item and a message to inform the clients
    			MapItem mapItem = new MapItem(position, ItemType.KEY, (i+1));
    			int id = ECS.createItem(mapItem);
    			NewItem newItem = new NewItem(id, mapItem);
    			ntc.networkMessagesToProcess.add(newItem);
    		}
    		inventory.ownKeys[i] = false;
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
