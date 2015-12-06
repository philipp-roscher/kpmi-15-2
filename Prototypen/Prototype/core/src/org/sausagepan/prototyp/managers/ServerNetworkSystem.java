package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.network.GameServer;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.HPUpdateRequest;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.LoseKeyRequest;
import org.sausagepan.prototyp.network.Network.LoseKeyResponse;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.ShootRequest;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.TakeKeyRequest;
import org.sausagepan.prototyp.network.Network.TakeKeyResponse;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * Created by georg on 29.10.15.
 */
// TODO IMPLEMENT	
public class ServerNetworkSystem extends ObservingEntitySystem{
	/* ............................................................................... CLASSES .. */
	class NetworkMessage {
		Connection connection;
		Object object;
		
		public NetworkMessage(Connection connection, Object object) {
			this.connection = connection;
			this.object = object;
		}
	}
	
    /* ............................................................................ ATTRIBUTES .. */
    private Array<NetworkMessage> networkMessages = new Array<NetworkMessage>();
    private Server server;
    private GameServer gameServer;
	private ServerEntityComponentSystem ECS;
	
    /* ........................................................................... CONSTRUCTOR .. */
    public ServerNetworkSystem(ServerEntityComponentSystem ECS, Server server, GameServer gameServer) {
    	this.ECS = ECS;
    	this.server = server;
    	this.gameServer = gameServer;

        server.addListener(new Listener() {
        	public void received(Connection connection, Object object) {
        		networkMessages.add(new NetworkMessage(connection, object));
        	}
        });
    }
    
    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) { }

    public void update(float deltaTime) {        
        for(NetworkMessage nm : networkMessages) {
        	Connection connection = nm.connection;
        	Object object = nm.object;
        	
        	if (object instanceof NewHeroRequest) {
        		NewHeroRequest request = (NewHeroRequest) object;
        		System.out.println("New Hero (ID " + request.playerId + "): " + request.clientClass);
        		connection.sendTCP(gameServer.getMap());
        		ECS.addNewCharacter(request.playerId, gameServer.getTeamAssignments().get(request.playerId), request.clientClass);
        		NewHeroResponse response = new NewHeroResponse(request.playerId, gameServer.getTeamAssignments().get(request.playerId), request.clientClass);
        		server.sendToAllUDP(response);
        		//updateLastAccess(request.playerId);
        	}
        	
        	if (object instanceof PositionUpdate) {
			   // System.out.println("PositionUpdate eingegangen");
			
			   PositionUpdate request = (PositionUpdate)object;
			   ECS.updatePosition(request.playerId, request.position);
        	}

		    if (object instanceof FullGameStateRequest) {
			   System.out.println("FullGameStateRequest eingegangen");
			   FullGameStateResponse response = ECS.generateFullGameStateResponse();
        	   connection.sendTCP(response);
	       }
           

           if (object instanceof AttackRequest) {
			   AttackRequest request = (AttackRequest)object;
			   server.sendToAllUDP(new AttackResponse(request.playerId, request.stop));
           }
           
           if (object instanceof ShootRequest) {
        	   ShootRequest request = (ShootRequest) object;
        	   server.sendToAllUDP(new ShootResponse(request.playerId, request.position, request.direction));
           }
           
           if (object instanceof HPUpdateRequest) {
        	   HPUpdateRequest request = (HPUpdateRequest) object;
        	   server.sendToAllTCP(new HPUpdateResponse(request.playerId, request.HP));
           }

           if (object instanceof TakeKeyRequest) {
        	   	System.out.println("TakeKeyResponse");
        	   	TakeKeyRequest request = (TakeKeyRequest) object;
        	   	server.sendToAllTCP(new TakeKeyResponse(request.id, request.keySection));
           }
           
           if (object instanceof LoseKeyRequest) {
				System.out.println("LoseKeyResponse");
				LoseKeyRequest request = (LoseKeyRequest) object;
				server.sendToAllTCP(new LoseKeyResponse(request.id, request.keySection, request.x, request.y));
           }
        }
        networkMessages.clear();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
