package org.sausagepan.prototyp.managers;

import java.util.HashMap;
import java.util.Map.Entry;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.ItemFactory;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.LoseKeyResponse;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.TakeKeyResponse;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkSystem extends ObservingEntitySystem{
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private PositionUpdate posUpdate = new PositionUpdate();
    private Array<Object> networkMessages = new Array<Object>();
	private boolean FGSreceived = false;
	private boolean setup = false;
	
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
    	// set up Client for communication
        if(!setup) {
        	localEntity = entities.get(0);
            NetworkComponent network = nm.get(localEntity);
            posUpdate.playerId = network.id;
            network.client.addListener(new Listener() {
                public void received(Connection connection, Object object) {
                    if ((object instanceof NewHeroResponse) ||
                            (object instanceof DeleteHeroResponse) ||
                            (object instanceof DeleteHeroResponse) ||
                            (object instanceof GameStateResponse) ||
                            (object instanceof AttackResponse) ||
                            (object instanceof ShootResponse) ||
                            (object instanceof HPUpdateResponse) ||
                            (object instanceof FullGameStateResponse) ||
                            (object instanceof LoseKeyResponse) ||
                            (object instanceof TakeKeyResponse)) {
                        //System.out.println( object.getClass() +" empfangen");
                        NetworkSystem.this.networkMessages.add(object);
                    }
                }
            });

            network.client.sendTCP(new FullGameStateRequest());
            
            setup = true;
            System.out.println("NetworkSystem was successfully set up!");
        }
        
        for (Entity entity : entities) {
            DynamicBodyComponent body = dm.get(entity);
            NetworkTransmissionComponent networkTransmissionComponent = ntm.get(entity);
            NetworkComponent network = nm.get(entity);
            WeaponComponent weapon = wm.get(entity);
            InputComponent input = im.get(entity);
            
            // send PositionUpdate (every tick)
            networkTransmissionComponent.moving     = input.moving;
            networkTransmissionComponent.direction  = input.direction;
            networkTransmissionComponent.linearVelocity = body.dynamicBody.getLinearVelocity();
            networkTransmissionComponent.position   = body.dynamicBody.getPosition();
            posUpdate.position = networkTransmissionComponent;
            network.client.sendUDP(posUpdate);
            
            if(weapon.weapon.justUsed) {
            	network.client.sendUDP(new AttackRequest(network.id, false));
            }
            if(network.stopAttacking) {
            	network.client.sendUDP(new AttackRequest(network.id, true));
            	network.stopAttacking = false;
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

                FGSreceived = true;
            }
            if(FGSreceived) {
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

                    for(Entry<Integer, NetworkTransmissionComponent> e : result.positions.entrySet()) {
                        if(e.getKey() != posUpdate.playerId) {
                            CharacterEntity character = ECS.getCharacter(e.getKey());
                            if(character != null) {
                    			character.getComponent(DynamicBodyComponent.class)
                                        .dynamicBody
                                        .setTransform(e.getValue().position, 0f);
                    			character.getComponent(DynamicBodyComponent.class)
                                        .dynamicBody
                                        .setLinearVelocity(e.getValue().linearVelocity);
                    			if(e.getValue().direction != null)
                    				character.getComponent(InputComponent.class).direction
                                            = e.getValue().direction;
                            }
                    	}
                    }
                }

                if (object instanceof AttackResponse) {
                    AttackResponse result = (AttackResponse) object;
                    if(result.playerId != posUpdate.playerId) {
                        CharacterEntity character = ECS.getCharacter(result.playerId);
                        if(character != null) {
	                        if(result.stop == false)
	                			character.getComponent(InputComponent.class).weaponDrawn = true;
	                        else
	                			character.getComponent(InputComponent.class).weaponDrawn = false;
                        }
                    }
                }

                if (object instanceof ShootResponse) {
                    ShootResponse result = (ShootResponse) object;
                    if(result.playerId != posUpdate.playerId) {
                        CharacterEntity character = ECS.getCharacter(result.playerId);
                        if(character != null)
                        	((Bow)character.getComponent(WeaponComponent.class).weapon).shoot(result.position, result.direction);
                    }
                }

                if (object instanceof HPUpdateResponse) {
                    HPUpdateResponse result = (HPUpdateResponse) object;
                    CharacterEntity character = ECS.getCharacter(result.playerId);

            		if(character != null)
            			character.getComponent(HealthComponent.class).HP = result.HP;	
                }

                if (object instanceof LoseKeyResponse) {
                    System.out.println("LoseKeyResponse");
                    LoseKeyResponse result = (LoseKeyResponse) object;
                    CharacterEntity character = ECS.getCharacter(result.id);
            		if(character != null)
            			character.getComponent(InventoryComponent.class).dropAllItems();
                }

                if (object instanceof TakeKeyResponse) {
                    System.out.println("TakeKeyResponse");
                    TakeKeyResponse result = (TakeKeyResponse) object;
                    
                    if(result.id != posUpdate.playerId) {
                        CharacterEntity character = ECS.getCharacter(result.id);
                        if (character != null) {
                			character.getComponent(InventoryComponent.class)
                                    .pickUpItem(ECS.getItemFactory().createKeyFragment(result.keySection), 1);
                		}
                	}
                }
            }
        }

        networkMessages.clear();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
