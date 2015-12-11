package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.KeyFragmentItem;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.DeleteBulletResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.ItemPickUp;
import org.sausagepan.prototyp.network.Network.NetworkPosition;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.ShootRequest;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.YouDiedResponse;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkSystem extends ObservingEntitySystem{
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private PositionUpdate posUpdate = new PositionUpdate();
    private Array<Object> networkMessages = new Array<Object>();
	
	private Entity localEntity;
	private EntityComponentSystem ECS;
	
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<NetworkTransmissionComponent> ntm
            = ComponentMapper.getFor(NetworkTransmissionComponent.class);
    private ComponentMapper<NetworkComponent> nm
    		= ComponentMapper.getFor(NetworkComponent.class);
    private ComponentMapper<InputComponent> im
    		= ComponentMapper.getFor(InputComponent.class);
    private ComponentMapper<WeaponComponent> wm
    		= ComponentMapper.getFor(WeaponComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public NetworkSystem(EntityComponentSystem ECS) {
    	this.ECS = ECS;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(
                DynamicBodyComponent.class,
                InputComponent.class,
                CharacterSpriteComponent.class,
                LightComponent.class,
                NetworkTransmissionComponent.class,
                NetworkComponent.class).get());
				
        if(entities.size() != 1)
        	System.err.println("Entities: "+ entities.size());
    }

    public void update(float deltaTime) {        
        for (Entity entity : entities) {
            DynamicBodyComponent body = dm.get(entity);
            NetworkTransmissionComponent ntc = ntm.get(entity);
            NetworkComponent network = nm.get(entity);
            WeaponComponent weapon = wm.get(entity);
            InputComponent input = im.get(entity);
            
            // send PositionUpdate (every tick)
            posUpdate.position.moving     = input.moving;
            posUpdate.position.direction  = input.direction;
            posUpdate.position.velocity   = body.dynamicBody.getLinearVelocity();
            posUpdate.position.position   = body.dynamicBody.getPosition();
            posUpdate.position.bodyDirection = body.direction;
            network.client.sendUDP(posUpdate);
            
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
        }
        
        for(Object object : networkMessages) {
            //System.out.println( object.getClass() +" auswerten");
            if (object instanceof FullGameStateResponse) {
                // adds all the player of the FullGameStateResponse
                //System.out.println("FullGameStateResponse");
                FullGameStateResponse response = (FullGameStateResponse) object;

            	HashMap<Integer,CharacterClass> otherCharacters = response.heroes;

                if (otherCharacters.containsKey(posUpdate.playerId))
                    otherCharacters.remove(posUpdate.playerId);

                for(Entry<Integer,CharacterClass> e : otherCharacters.entrySet()) {
                    Integer heroId = e.getKey();
                    CharacterClass clientClass = e.getValue();
                    int teamId = response.teamAssignments.get(heroId);
                    ECS.addNewCharacter(heroId, teamId, clientClass);
                }
                
                ECS.setUpMonsters(response.monsters);
                ECS.setUpItems(response.items);

                nm.get(localEntity).client.addListener(new Listener() {
                    public void received(Connection connection, Object object) {
                        if ((object instanceof NewHeroResponse) ||
                                (object instanceof DeleteHeroResponse) ||
                                (object instanceof DeleteHeroResponse) ||
                                (object instanceof GameStateResponse) ||
                                (object instanceof AttackResponse) ||
                                (object instanceof ShootResponse) ||
                                (object instanceof HPUpdateResponse) ||
                                (object instanceof DeleteBulletResponse) ||
                                (object instanceof YouDiedResponse) ||
                                (object instanceof ItemPickUp)) {
                            //System.out.println( object.getClass() +" empfangen");
                            NetworkSystem.this.networkMessages.add(object);
                        }
                    }
                });
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
            	if(result.id == posUpdate.playerId) {
            		DynamicBodyComponent body = ECS.getLocalCharacterEntity().getComponent(DynamicBodyComponent.class);
            		body.dynamicBody.setTransform(body.startPosition, 0f);
            	}
            }
            
            if (object instanceof ItemPickUp) {
            	ItemPickUp result = (ItemPickUp) object;

				if(ECS.getItem(result.itemId).getComponent(ItemComponent.class).type == ItemType.KEY) {
                	KeyFragmentItem keyFragment = (KeyFragmentItem) ECS.getItem(result.itemId).getComponent(ItemComponent.class).item;
                	// add key to character inventory
                	ECS.getCharacter(result.playerId).getComponent(InventoryComponent.class).ownKeys[keyFragment.keyFragmentNr - 1] = true;
                	
                	// add key to team inventory of all team members
                	int teamId = ECS.getCharacter(result.playerId).getComponent(TeamComponent.class).TeamId;
                	ImmutableArray<Entity> characters = this.getEngine().getEntitiesFor(EntityFamilies.characterFamily);
                	for(Entity character : characters) {
                		if (character.getComponent(TeamComponent.class).TeamId == teamId) {
                			InventoryComponent inventory = character.getComponent(InventoryComponent.class);
                        	inventory.teamKeys[keyFragment.keyFragmentNr - 1] = true;
                        	if(inventory.getKeyAmount() == 3) inventory.needsUpdate = true;
                		}
                	}                    	
				}
                
                ECS.deleteItem(result.itemId);
            }
        }

        networkMessages.clear();
    }
    
    // sets up the system for communication, adds listener
    public void setupSystem() {
    	localEntity = ECS.getLocalCharacterEntity();
        NetworkComponent network = nm.get(localEntity);
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
