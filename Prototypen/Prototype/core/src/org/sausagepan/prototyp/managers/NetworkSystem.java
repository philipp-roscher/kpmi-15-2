package org.sausagepan.prototyp.managers;

import java.util.HashMap;
import java.util.Map.Entry;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.components.MonsterSpawnComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.entities.ItemEntity;
import org.sausagepan.prototyp.model.entities.MapCharacterObject;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.KeyFragmentItem;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.network.Network.AcknowledgeDeath;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.DeleteBulletResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.GameStart;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.ItemPickUp;
import org.sausagepan.prototyp.network.Network.NetworkPosition;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.NewItem;
import org.sausagepan.prototyp.network.Network.NewMonster;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.ShootRequest;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.UseItemRequest;
import org.sausagepan.prototyp.network.Network.UseItemResponse;
import org.sausagepan.prototyp.network.Network.WeaponChangeRequest;
import org.sausagepan.prototyp.network.Network.WeaponChangeResponse;
import org.sausagepan.prototyp.network.Network.YouDiedResponse;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Entity localCharEntity;
    private PositionUpdate posUpdate = new PositionUpdate();
    private Array<Object> networkMessages = new Array<Object>();
	
	private EntityComponentSystem ECS;
	
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<NetworkTransmissionComponent> ntm
            = ComponentMapper.getFor(NetworkTransmissionComponent.class);
    private ComponentMapper<NetworkComponent> nm
    		= ComponentMapper.getFor(NetworkComponent.class);
    private ComponentMapper<InputComponent> im
    		= ComponentMapper.getFor(InputComponent.class);
    private ComponentMapper<IsDeadComponent> idm
			= ComponentMapper.getFor(IsDeadComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public NetworkSystem(EntityComponentSystem ECS) {
    	this.ECS = ECS;
    }
    
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        localCharEntity = ECS.getLocalCharacterEntity();
    }

    public void update(float deltaTime) {        
        DynamicBodyComponent body = dm.get(localCharEntity);
        NetworkTransmissionComponent ntc = ntm.get(localCharEntity);
        NetworkComponent network = nm.get(localCharEntity);
        InputComponent input = im.get(localCharEntity);
        IsDeadComponent isDead = idm.get(localCharEntity);
        
        // send PositionUpdate (every tick) unless dead
        if(isDead != null) {
        	if(System.currentTimeMillis() - isDead.deathTime > isDead.deathLength) {
        		localCharEntity.remove(IsDeadComponent.class);
        		body.dynamicBody.setTransform(body.startPosition, 0f);
        	}
        } else {
	        posUpdate.position.moving     = input.moving;
	        posUpdate.position.direction  = input.direction;
	        posUpdate.position.velocity   = body.dynamicBody.getLinearVelocity();
	        posUpdate.position.position   = body.dynamicBody.getPosition();
	        posUpdate.position.bodyDirection = body.direction;
	        network.client.sendUDP(posUpdate);
        }

        // send other network messages
        for(Object object : ntc.networkMessagesToProcess) {
            if ((object instanceof AttackRequest) ||
                (object instanceof ShootRequest)
            )
                network.client.sendUDP(object);
            
            if ((object instanceof MonsterSpawnComponent) ||
            	(object instanceof WeaponChangeRequest) ||
            	(object instanceof UseItemRequest))
            	network.client.sendTCP(object);
        }
        ntc.networkMessagesToProcess.clear();
        

        for(Object object : networkMessages) {
            //System.out.println( object.getClass() +" auswerten");
            if (object instanceof FullGameStateResponse) {
                // adds all the player of the FullGameStateResponse
                //System.out.println("FullGameStateResponse");
                FullGameStateResponse response = (FullGameStateResponse) object;

            	HashMap<Integer,MapCharacterObject> characters = response.characters;

                if (characters.containsKey(posUpdate.playerId))
                    characters.remove(posUpdate.playerId);

                for(Entry<Integer,MapCharacterObject> e : characters.entrySet()) {
                    Integer heroId = e.getKey();
                    MapCharacterObject character = e.getValue();
                    ECS.addNewCharacter(heroId, character);
                }
                
                ECS.setUpMonsters(response.monsters);
                ECS.setUpItems(response.items);

                nm.get(localCharEntity).client.addListener(new Listener() {
                    public void received(Connection connection, Object object) {
                        //System.out.println( object.getClass() +" empfangen");
                        NetworkSystem.this.networkMessages.add(object);
                    }
                });

                // if server sent GameStart before it was registered, open the entrance doors anyway
                ECS.checkGameReady();
				networkMessages.removeValue(object, true);
            }

            if (object instanceof NewHeroResponse) {
                NewHeroResponse request = (NewHeroResponse) object;
                ECS.addNewCharacter(request);
				networkMessages.removeValue(object, true);
            }

            if (object instanceof DeleteHeroResponse) {
                int playerId = ((DeleteHeroResponse) object).playerId;
                System.out.println(playerId + " was inactive for too long and thus removed from the session.");
                ECS.deleteCharacter(playerId);
				networkMessages.removeValue(object, true);
            }


            /* ........................................................................ GAME EXIT */
            if(object instanceof Network.GameExitResponse) {
                System.out.print("Quit Game Message received ...");
                ECS.quitGame((Network.GameExitResponse)object);
				networkMessages.removeValue(object, true);
            }
            /* ........................................................................ GAME EXIT */

            if (object instanceof GameStateResponse) {
                GameStateResponse result = (GameStateResponse) object;
                
                // don't process messages older than the current state
                if(result.tickId > ntc.lastTickId) {
	                for(Entry<Integer, NetworkPosition> e : result.characters.entrySet()) {
	                    if(e.getKey() != posUpdate.playerId) {
	                        CharacterEntity character = ECS.getCharacter(e.getKey());
	                        if(character != null) {
	                			character.getComponent(DynamicBodyComponent.class)
	                                    .dynamicBody
	                                    .setTransform(e.getValue().position, 0f);
	                			character.getComponent(DynamicBodyComponent.class)
	                                    .dynamicBody
	                                    .setLinearVelocity(e.getValue().velocity);
	                            character.getComponent(DynamicBodyComponent.class).direction = e.getValue().bodyDirection;
	
	                			if(e.getValue().direction != null)
	                				character.getComponent(InputComponent.class).direction
	                                        = e.getValue().direction;
	                        }
	                	}
	                }
	
	                for(Entry<Integer, NetworkPosition> e : result.monsters.entrySet()) {
	                    MonsterEntity monster = ECS.getMonster(e.getKey());
	                    if(monster != null) {
	            			monster.getComponent(DynamicBodyComponent.class)
	                                .dynamicBody
	                                .setTransform(e.getValue().position, 0f);
	            			monster.getComponent(DynamicBodyComponent.class)
	                                .dynamicBody
	                                .setLinearVelocity(e.getValue().velocity);
	            			if(e.getValue().direction != null)
	            				monster.getComponent(InputComponent.class).direction
	                            	= e.getValue().direction;
	                    }
	                }
	                
	                ntc.lastTickId = result.tickId;
                }
				networkMessages.removeValue(object, true);
            }

            if (object instanceof AttackResponse) {
                AttackResponse result = (AttackResponse) object;
                if(result.playerId != posUpdate.playerId) {
                    CharacterEntity character = ECS.getCharacter(result.playerId);
                    if(character != null) {
                    	character.getComponent(InputComponent.class).weaponDrawn = !result.stop;
                    }
                }
				networkMessages.removeValue(object, true);
            }

            if (object instanceof ShootResponse) {
                ShootResponse result = (ShootResponse) object;

                CharacterEntity character = ECS.getCharacter(result.playerId);
                if(character != null)
                	if(character.getComponent(WeaponComponent.class).weapon instanceof Bow)
                		((Bow)character.getComponent(WeaponComponent.class).weapon).shoot(result.position, result.direction, result.bulletId);
                	else
                		System.err.println("Server character weapon doesn't match local character weapon!!");
                
				networkMessages.removeValue(object, true);
            }

            if (object instanceof HPUpdateResponse) {
                HPUpdateResponse result = (HPUpdateResponse) object;
                Entity character;

                if(result.isHuman)
                    character = ECS.getCharacter(result.playerId);
                else
                    character = ECS.getMonster(result.playerId);

                if(character != null) {
                    HealthComponent h = CompMappers.health.get(character);
                    if(h.HP > result.HP && result.isHuman && localCharEntity.equals(ECS
                            .getCharacter(result.playerId))) h
                            .justHurt = true;
                    character.getComponent(HealthComponent.class).HP = result.HP;
                }
				networkMessages.removeValue(object, true);
            }

            if (object instanceof DeleteBulletResponse) {
                DeleteBulletResponse result = (DeleteBulletResponse) object;

                CharacterEntity character = ECS.getCharacter(result.playerId);
                if(character != null) {
                	if(character.getComponent(WeaponComponent.class).weapon instanceof Bow)
                		((Bow)character.getComponent(WeaponComponent.class).weapon).deleteBullet(result.bulletId);
                	else
                		System.err.println("Server character weapon doesn't match local character weapon!!");
                }
				networkMessages.removeValue(object, true);
            }
            
            if (object instanceof YouDiedResponse) {
            	YouDiedResponse result = (YouDiedResponse) object;
            	
            	// remove own keys from character
            	InventoryComponent inventory = ECS.getCharacter(result.id).getComponent(InventoryComponent.class);
            	for(int i=0; i<3; i++) {
                	inventory.ownKeys[i] = false;
                }
            	
            	if(result.id == posUpdate.playerId) {
            		localCharEntity.add(new IsDeadComponent(System.currentTimeMillis(), 5000));
            		body.dynamicBody.setTransform(new Vector2(0,0), 0f);
            		network.client.sendTCP(new AcknowledgeDeath(posUpdate.playerId));
            	}
				networkMessages.removeValue(object, true);
            }

            if (object instanceof NewItem) {
            	NewItem result = (NewItem) object;
            	ECS.createItem(result.id, result.item);
				networkMessages.removeValue(object, true);
            }

            if (object instanceof NewMonster) {
                NewMonster result = (NewMonster) object;
                ECS.createMonster(result.id, result.monster);
				networkMessages.removeValue(object, true);
            }
            
            if (object instanceof ItemPickUp) {
            	ItemPickUp result = (ItemPickUp) object;
            	ItemEntity item;
            	CharacterEntity character;
            	
				if((item = ECS.getItem(result.itemId)) != null && (character = ECS.getCharacter(result.playerId)) != null) {
					ImmutableArray<Entity> itemsInEngine = getEngine().getEntitiesFor(EntityFamilies.itemFamily);
					boolean itemAddedToEngine = false;
					
					// check if item has already been added to the engine or only to the ECS
					for(Entity e : itemsInEngine) {
						if(CompMappers.id.get(e).id == result.itemId)
							itemAddedToEngine = true;
					}
					
					// only proceed if item is registered in the engine
					if(itemAddedToEngine) {
						if(item.getComponent(ItemComponent.class).item.type == ItemType.KEY) {
		                	// add key to character inventory
		                	KeyFragmentItem keyFragment = (KeyFragmentItem) item.getComponent(ItemComponent.class).item;
		                	CompMappers.inventory.get(character).ownKeys[keyFragment.keyFragmentNr - 1] = true;
						} else {
							if(result.playerId == network.id) {
								CompMappers.inventory.get(character).pickUpItem(item.getComponent(ItemComponent.class).item);    
		            			ECS.getItemUI().initializeItemMenu();
							}
						}
	
		                ECS.deleteItem(result.itemId);
						networkMessages.removeValue(object, true);
					}
				}
            }

            if (object instanceof GameStart) {
                ECS.getMaze().openEntranceDoors();
				networkMessages.removeValue(object, true);
            }
            
            if (object instanceof WeaponChangeResponse) {
            	WeaponChangeResponse result = (WeaponChangeResponse) object;
            	CharacterEntity character;
            	
            	System.out.println("Player "+result.playerId+" switched to weapon "+result.weaponName);
            	if ((character = ECS.getCharacter(result.playerId)) != null) {
            		if(result.playerId == network.id) {
                		CompMappers.weapon.get(character).weapon =
                				CompMappers.inventory.get(character).weapons.get(result.weaponId);    
            			ECS.getItemUI().initializeItemMenu();
            		} else {
                		CompMappers.weapon.get(character).weapon =
                				ECS.getItemFactory().createWeaponFromName(result.weaponName);            			
            		}
            	}
				networkMessages.removeValue(object, true);
            }
            
            if (object instanceof UseItemResponse) {
            	UseItemResponse result = (UseItemResponse) object;
            	CharacterEntity character;
            	
            	System.out.println("Player "+result.playerId+" used "+result.itemType);
            	if ((character = ECS.getCharacter(result.playerId)) != null) {
            		if(result.playerId == network.id) {
            			CompMappers.inventory.get(character).items.removeIndex(result.itemId);
            			ECS.getItemUI().initializeItemMenu();
            		}
            	}
				networkMessages.removeValue(object, true);
            }
        }
    }
    
    // sets up the system for communication, adds listener
    public void setupSystem() {
        NetworkComponent network = nm.get(localCharEntity);
        posUpdate.playerId = network.id;
        posUpdate.position = new NetworkPosition();
        network.client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof FullGameStateResponse) {
                    //System.out.println( object.getClass() +" empfangen");
                    NetworkSystem.this.networkMessages.add(object);
                }
            }
        });

        network.client.sendTCP(new FullGameStateRequest());
        System.out.println("NetworkSystem was successfully set up!");
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
