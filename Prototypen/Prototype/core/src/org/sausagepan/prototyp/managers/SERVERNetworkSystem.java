package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.MonsterSpawnComponent;
import org.sausagepan.prototyp.model.components.SERVERNetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;
import org.sausagepan.prototyp.model.items.Item;
import org.sausagepan.prototyp.model.items.PotionHP;
import org.sausagepan.prototyp.model.items.PotionMP;
import org.sausagepan.prototyp.network.GameServer;
import org.sausagepan.prototyp.network.Network.AcknowledgeDeath;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.DeleteBulletResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.GameExitRequest;
import org.sausagepan.prototyp.network.Network.GameExitResponse;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.ItemPickUp;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;
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

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * Created by philipp on 06.12.15.
 */
public class SERVERNetworkSystem extends EntitySystem {
	/* ............................................................................... CLASSES .. */
	class NetworkMessage {
		Connection connection;
		Object object;
		
		public NetworkMessage(Connection connection, Object object) {
			this.connection = connection;
			this.object = object;
		}
	}

    class DisconnectionMessage { }
	
    /* ............................................................................ ATTRIBUTES .. */
    private Array<NetworkMessage> networkMessages = new Array<NetworkMessage>();
    private Server server;
    private GameServer gameServer;
	private SERVEREntityComponentSystem ECS;
    private SERVERNetworkTransmissionComponent ntc;
	
    /* ........................................................................... CONSTRUCTOR .. */
    public SERVERNetworkSystem(SERVEREntityComponentSystem ECS, Server server, GameServer gameServer) {
    	this.ECS = ECS;
    	this.server = server;
    	this.gameServer = gameServer;

        server.addListener(new Listener() {
        	public void received(Connection connection, Object object) {
        		networkMessages.add(new NetworkMessage(connection, object));
        	}

            public void disconnected(Connection connection) {
                networkMessages.add(new NetworkMessage(connection, new DisconnectionMessage()));
            }
        });
        
        ntc = ECS.getSNTC();
    }
    
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) { }

    public void update(float deltaTime) {
        for(Object object : ntc.networkMessagesToProcess) {
            if ((object instanceof HPUpdateResponse) ||
                (object instanceof DeleteBulletResponse) ||
                (object instanceof ItemPickUp) ||
                (object instanceof NewItem) ||
                (object instanceof YouDiedResponse) ||
                (object instanceof GameExitResponse) ||
                (object instanceof NewMonster)
            )
                server.sendToAllTCP(object);
            
            if (object instanceof ShootResponse)
            	server.sendToAllUDP(object);
        }
        ntc.networkMessagesToProcess.clear();

        for(NetworkMessage nm : networkMessages) {
        	Connection connection = nm.connection;
        	Object object = nm.object;

            // handle disconnection messages
            if (object instanceof DisconnectionMessage) {
            	int id = connection.getID();
            	
            	System.out.println("Player " + id + " has disconnected");
            	
            	ServerCharacterEntity character = ECS.getCharacter(id);
    			
    			// if player hasn't chosen a character class yet, he isn't registered by the ECS yet
            	if(character != null)
            		this.getEngine().getSystem(ItemSystem.class).dropItems(id);

            	ECS.deleteCharacter(id);
            	server.sendToAllTCP(new DeleteHeroResponse(id));
            }

        	if (object instanceof NewHeroRequest) {
        		NewHeroRequest request = (NewHeroRequest) object;

                // check if server is full
                if(gameServer.clientCount <= gameServer.maxClients) {
                    System.out.println("New Hero (ID " + request.playerId + "): " + request.clientClass);
                    connection.sendTCP(gameServer.getMap());
                    ECS.addNewCharacter(request.playerId, gameServer.getTeamAssignments().get(request.playerId), request.clientClass);
                    NewHeroResponse response = new NewHeroResponse(request.playerId, gameServer.getTeamAssignments().get(request.playerId), request.clientClass);
                    server.sendToAllTCP(response);
                }
        	}
        	
        	if (object instanceof PositionUpdate) {
        	    // System.out.println("PositionUpdate eingegangen");
                PositionUpdate request = (PositionUpdate)object;

                ServerCharacterEntity character = ECS.getCharacter(request.playerId);
                
                if(character != null) {
                	// Check if character is dead and therefore not allowed to move
                	IsDeadComponent id = character.getComponent(IsDeadComponent.class);
                    if(id != null) {
                    	if( (System.currentTimeMillis() - id.deathTime > id.deathLength)
                    			&& id.deathAcknowledged)
                    		character.remove(IsDeadComponent.class);
                    } else {
	                    character.getComponent(DynamicBodyComponent.class)
	                            .dynamicBody
	                            .setTransform(request.position.position, 0f);
	                    character.getComponent(DynamicBodyComponent.class)
	                            .dynamicBody
	                            .setLinearVelocity(request.position.velocity);
	                    character.getComponent(DynamicBodyComponent.class)
	                            .direction = request.position.bodyDirection;
	
	                    if (request.position.direction != null)
	                        character.getComponent(InputComponent.class).direction = request.position.direction;
                    }
                }
        	}

		    if (object instanceof FullGameStateRequest) {
			   System.out.println("FullGameStateRequest eingegangen");
			   FullGameStateResponse response = ECS.generateFullGameStateResponse();
        	   connection.sendTCP(response);
	       }
           

           if (object instanceof AttackRequest) {
			   AttackRequest request = (AttackRequest)object;
			   server.sendToAllUDP(new AttackResponse(request.playerId, request.stop));

               ServerCharacterEntity character = ECS.getCharacter(request.playerId);
			   if(character != null) {
                   character.getComponent(WeaponComponent.class).weapon.justUsed = !request.stop;
               }
           }
           
           if (object instanceof ShootRequest) {
        	   ShootRequest request = (ShootRequest) object;
               ServerCharacterEntity character = ECS.getCharacter(request.playerId);
               if(character != null) {
                   character.getComponent(WeaponComponent.class).weapon.justUsed = true;
               }
           }

           if (object instanceof AcknowledgeDeath) {
        	   AcknowledgeDeath result = (AcknowledgeDeath) object;

               ServerCharacterEntity character = ECS.getCharacter(result.id);
               if(character != null) {
                   character.getComponent(IsDeadComponent.class).deathAcknowledged = true;
               }        	   
           }

            //end game because player entered exit area
            if (object instanceof GameExitRequest) {
                GameExitRequest result = (GameExitRequest) object;
                ServerCharacterEntity character = ECS.getCharacter(result.id);
                if(character != null) {
                    //send winning Team-Id to all Clients
                    int teamId = character.getComponent(TeamComponent.class).TeamId;
                    server.sendToAllTCP(new GameExitResponse(teamId));
                }
            }
            
            if (object instanceof MonsterSpawnComponent) {
                MonsterSpawnComponent mon = (MonsterSpawnComponent) object;

                if (mon.monsterSpawn) {
                    int count = mon.getSpawnCount();
                    //create as many monsters as count implies
                    for (int i=1; i <= count; i++) {
                        int id = ECS.createMonster(mon.getMonster());
                        NewMonster newMonster = new NewMonster(id, mon.getMonster());
                        ntc.networkMessagesToProcess.add(newMonster);
                    }

                    //so it only spawns monster one time per button press
                    mon.monsterSpawn = false;
                }
            }
            
            if (object instanceof WeaponChangeRequest) {
            	WeaponChangeRequest result = (WeaponChangeRequest) object;
            	ServerCharacterEntity character;

            	if((character = ECS.getCharacter(result.playerId)) != null) {
            		if(CompMappers.inventory.get(character).weapons.get(result.weaponId).name.equals(result.weaponName)) {
	            		CompMappers.weapon.get(character).weapon =
	            				CompMappers.inventory.get(character).weapons.get(result.weaponId);
	            		server.sendToAllTCP(new WeaponChangeResponse(result.playerId, result.weaponId, result.weaponName));
            		} else {
            			System.err.println("Sync issue: Weapon on server differs from client weapon.");
            			System.err.println("Client: "+ result.weaponName + " | Server: " + CompMappers.inventory.get(character).weapons.get(result.weaponId).name);
            		}
            	}
            }
            
            if (object instanceof UseItemRequest) {
            	UseItemRequest result = (UseItemRequest) object;
            	ServerCharacterEntity character;
            	Item item;

            	System.out.println(result.playerId + " - " + result.itemId + " - "+ result.itemType);
            	
            	if((character = ECS.getCharacter(result.playerId)) != null && (item = CompMappers.inventory.get(character).items.get(result.itemId)) != null) {
            		if(item.type.equals(result.itemType)) {
	            		// if item is potion
            			if(result.itemType == ItemType.POTION_HP) {
                        	PotionHP potion = (PotionHP) item;
                        	HealthComponent health = character.getComponent(HealthComponent.class);
                        	if(health.HP != health.initialHP) {
	                        	health.HP += potion.strength;
	                        	// health can't surpass max HP
	                        	if(health.HP > health.initialHP) health.HP = health.initialHP;
	    	            		server.sendToAllTCP(new UseItemResponse(result.playerId, result.itemId, result.itemType));
	                        	ntc.networkMessagesToProcess.add(new HPUpdateResponse(character.getComponent(IdComponent.class).id, true, health.HP));
                        	}
            			}
            			
            			if(result.itemType == ItemType.POTION_MP) {
                        	PotionMP potion = (PotionMP) item;
	    	            	server.sendToAllTCP(new UseItemResponse(result.playerId, result.itemId, result.itemType));
            			}
            			
            			CompMappers.inventory.get(character).items.removeIndex(result.itemId);
            			// TODO: add more items?
            		} else {
            			System.err.println("Sync issue: Item on server differs from client item.");
            			System.err.println("Client: "+ result.itemType + " | Server: " + item.type);
                		Array<Item> items = CompMappers.inventory.get(character).items;
                		for(Item i : items) {
                			System.out.println(items.indexOf(i, true) + " - " + i.type);
                		}
            		}
            	} else {
            		System.err.println("ERR2");
            		Array<Item> items = CompMappers.inventory.get(character).items;
            		for(Item i : items) {
            			System.out.println(items.indexOf(i, true) + " - " + i.type);
            		}
            	}
            }
        }

        networkMessages.clear();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
