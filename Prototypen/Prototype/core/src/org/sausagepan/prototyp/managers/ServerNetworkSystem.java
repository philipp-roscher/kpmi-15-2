package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.ServerNetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;
import org.sausagepan.prototyp.model.items.MapItem;
import org.sausagepan.prototyp.network.GameServer;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.network.Network.AcknowledgeDeath;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.DeleteBulletResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.ItemPickUp;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.NewItem;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.ShootRequest;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.YouDiedResponse;
import org.sausagepan.prototyp.network.Network.GameExitRequest;
import org.sausagepan.prototyp.network.Network.GameExitResponse;

/**
 * Created by philipp on 06.12.15.
 */
public class ServerNetworkSystem extends EntitySystem {
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
	private ServerEntityComponentSystem ECS;
    private ServerNetworkTransmissionComponent ntc;
	
    /* ........................................................................... CONSTRUCTOR .. */
    public ServerNetworkSystem(ServerEntityComponentSystem ECS, Server server, GameServer gameServer) {
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
                (object instanceof YouDiedResponse)
            )
                server.sendToAllTCP(object);
            
            if (object instanceof ShootResponse)
            	server.sendToAllUDP(object);
        }
        ntc.networkMessagesToProcess.clear();

        for(NetworkMessage nm : networkMessages) {
        	Connection connection =
        			nm.connection;
        	Object object = nm.object;

            // handle disconnection messages
            if (object instanceof DisconnectionMessage) {
            	int id = connection.getID();
            	
            	System.out.println("Player " + id + " has disconnected");
            	
            	ServerCharacterEntity character = ECS.getCharacter(id);
            	DynamicBodyComponent body = character.getComponent(DynamicBodyComponent.class);
    			InventoryComponent inventory = character.getComponent(InventoryComponent.class);
    			// create new temporary Vector2 that holds old character position
				Vector2 position = new Vector2(body.dynamicBody.getPosition());
    			
    			// Drop his keys        			
    			for(int i=0; i<3; i++) {
    				if(inventory.ownKeys[i]) {
    					MapItem mapItem = new MapItem(position, ItemType.KEY, (i+1));
    					int itemId = ECS.createItem(mapItem);
    					NewItem newItem = new NewItem(itemId, mapItem);
    					ntc.networkMessagesToProcess.add(newItem);
    				}
    			}

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
        }

        networkMessages.clear();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
