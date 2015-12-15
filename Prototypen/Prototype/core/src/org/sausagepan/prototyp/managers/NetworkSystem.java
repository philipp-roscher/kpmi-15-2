package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
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
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.ShootRequest;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.YouDiedResponse;

import java.util.HashMap;
import java.util.Map.Entry;

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
        
        // send AttackRequest
        if(ntc.attack) {
        	network.client.sendUDP(new AttackRequest(network.id, false));
            ntc.attack = false;
        }
        if(ntc.stopAttacking) {
        	network.client.sendUDP(new AttackRequest(network.id, true));
        	ntc.stopAttacking = false;
        }
        if(ntc.shoot) {
        	network.client.sendUDP(new ShootRequest(network.id));
        	ntc.shoot = false;
        }

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
                        if ((object instanceof NewHeroResponse) ||
                                (object instanceof DeleteHeroResponse) ||
                                (object instanceof GameStateResponse) ||
                                (object instanceof AttackResponse) ||
                                (object instanceof ShootResponse) ||
                                (object instanceof HPUpdateResponse) ||
                                (object instanceof DeleteBulletResponse) ||
                                (object instanceof YouDiedResponse) ||
                                (object instanceof ItemPickUp) ||
                                (object instanceof NewItem) ||
                                (object instanceof GameStart) ||
                                (object instanceof Network.GameExitResponse)) {
                            //System.out.println( object.getClass() +" empfangen");
                            NetworkSystem.this.networkMessages.add(object);
                        }
                    }
                });

                // if server sent GameStart before it was registered, open the entrance doors anyway
                ECS.checkGameReady();
            }

            if (object instanceof NewHeroResponse) {
                NewHeroResponse request = (NewHeroResponse) object;
                ECS.addNewCharacter(request);
            }

            if (object instanceof DeleteHeroResponse) {
                int playerId = ((DeleteHeroResponse) object).playerId;
                System.out.println(playerId + " was inactive for too long and thus removed from the session.");
                ECS.deleteCharacter(playerId);
            }


            /* ........................................................................ GAME EXIT */
            if(object instanceof Network.GameExitResponse) {
                System.out.print("Quit Game Message received ...");
                ECS.quitGame((Network.GameExitResponse)object);
            }
            /* ........................................................................ GAME EXIT */

            if (object instanceof GameStateResponse) {
                GameStateResponse result = (GameStateResponse) object;

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
//            			if(e.getValue().direction != null)
//            				character.getComponent(InputComponent.class).direction
//                                    = e.getValue().direction;
                    }
                }
            }

            if (object instanceof AttackResponse) {
                AttackResponse result = (AttackResponse) object;
                if(result.playerId != posUpdate.playerId) {
                    CharacterEntity character = ECS.getCharacter(result.playerId);
                    if(character != null) {
                    	character.getComponent(InputComponent.class).weaponDrawn = !result.stop;
                    }
                }
            }

            if (object instanceof ShootResponse) {
                ShootResponse result = (ShootResponse) object;

                CharacterEntity character = ECS.getCharacter(result.playerId);
                if(character != null)
                    ((Bow)character.getComponent(WeaponComponent.class).weapon).shoot(result.position, result.direction, result.bulletId);
            }

            if (object instanceof HPUpdateResponse) {
                HPUpdateResponse result = (HPUpdateResponse) object;
                Entity character;

                if(result.isHuman)
                    character = ECS.getCharacter(result.playerId);
                else
                    character = ECS.getMonster(result.playerId);

                if(character != null)
                    character.getComponent(HealthComponent.class).HP = result.HP;
            }

            if (object instanceof DeleteBulletResponse) {
                DeleteBulletResponse result = (DeleteBulletResponse) object;

                CharacterEntity character = ECS.getCharacter(result.playerId);
                if(character != null) {
                    ((Bow)character.getComponent(WeaponComponent.class).weapon).deleteBullet(result.bulletId);
                }
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
            }

            if (object instanceof NewItem) {
            	NewItem result = (NewItem) object;
            	System.out.println("New Item: " + result.id + " : " + result.item.position);
            	ECS.createItem(result.id, result.item);
            }
            
            if (object instanceof ItemPickUp) {
            	ItemPickUp result = (ItemPickUp) object;

				if(ECS.getItem(result.itemId).getComponent(ItemComponent.class).type == ItemType.KEY) {
                	// add key to character inventory
                	KeyFragmentItem keyFragment = (KeyFragmentItem) ECS.getItem(result.itemId).getComponent(ItemComponent.class).item;
                	ECS.getCharacter(result.playerId).getComponent(InventoryComponent.class).ownKeys[keyFragment.keyFragmentNr - 1] = true;               	
				}
                
                ECS.deleteItem(result.itemId);
            }

            if(object instanceof GameStart) {
                ECS.getMaze().openEntranceDoors();
            }
        }

        networkMessages.clear();
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
