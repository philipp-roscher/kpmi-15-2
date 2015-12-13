package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;

/**
 * Created by Bettina on 03.11.2015.
 */
public class InventorySystem extends EntitySystem implements EntityListener {

    /*...................................................................................Atributes*/
    private CharacterEntity localCharacter;
    private ImmutableArray<Entity> entities;
    private Maze maze;
    private int teamId;

    private ComponentMapper<InventoryComponent> im
            = ComponentMapper.getFor(InventoryComponent.class);
    private ComponentMapper<TeamComponent> tm
    		= ComponentMapper.getFor(TeamComponent.class);

    public InventorySystem(Maze maze, CharacterEntity character) {
        this.maze = maze;
        localCharacter = character;
        teamId = tm.get(localCharacter).TeamId;
    }

    /*...................................................................................Functions*/
    public void addedToEngine(Engine engine) {
    	entities = engine.getEntitiesFor(EntityFamilies.characterFamily);
    }

    public void update(float deltaTime)
    {
        InventoryComponent inventory = im.get(localCharacter);
        
        // calculate how many keys the whole team has
        inventory.teamKeys = inventory.ownKeys.clone();
    	for(Entity character : entities) {
    		if (tm.get(character).TeamId == teamId) {
    			InventoryComponent teamInventory = im.get(character);
            	for(int i=0; i<3; i++) {
            		inventory.teamKeys[i] = inventory.teamKeys[i] || teamInventory.ownKeys[i];
            	}
    		}
    	}
        
        if(inventory.getKeyAmount() == 3 && !inventory.treasureRoomOpen) {
        	// Open treasure room
        	maze.openTreasureRoom();
        	inventory.treasureRoomOpen = true;
        }
        
        if(inventory.treasureRoomOpen && inventory.getKeyAmount() != 3) {
        	// Close treasure room
        	maze.lockTreasureRoom();
        	inventory.treasureRoomOpen = false;
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
}
