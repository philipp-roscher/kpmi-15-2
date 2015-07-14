package org.sausagepan.prototyp.network;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.sausagepan.prototyp.managers.BattleSystem;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.GameStateRequest;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.IDAssignment;
import org.sausagepan.prototyp.network.Position;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
	// Zeit in Millisekunden, bevor ein inaktiver Spieler automatisch gelöscht wird
	public static final int timeoutMs = 30000;
	// Anzahl der GameStateUpdates pro Sekunde
	public static final int updateRate = 32;
	
	public static Server server;
	public static int maxId = 1;
	public static HashMap<InetSocketAddress,Integer> clientIds;
	public static HashMap<Integer,Position> positions;
	public static HashMap<Integer,Long> lastAccess;
	public static HashMap<Integer,HeroInformation> cm;
	
	public static BattleSystem bs = new BattleSystem();
	
	public static void main(String[] args) {
		clientIds = new HashMap<InetSocketAddress, Integer>();
		positions = new HashMap<Integer,Position>();
		lastAccess = new HashMap<Integer,Long>();		
		cm = new HashMap<Integer,HeroInformation>();

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		//executor.scheduleAtFixedRate(deleteOldClients, 0, 1, TimeUnit.SECONDS);
		executor.scheduleAtFixedRate(updateGameState, 0, 1000000L/updateRate, TimeUnit.MICROSECONDS);

		try {
			server = new Server();
			Network.register(server);
			server.start();
			server.bind(Network.TCPPort, Network.UDPPort);
			
		    server.addListener(new Listener() {
		        public void received (Connection connection, Object object) {
		        	if (object instanceof NewHeroRequest) {
		        		NewHeroRequest request = (NewHeroRequest) object;
		        		HeroInformation hero = request.hero;
		        		System.out.println("New Hero (ID "+ request.playerId +"): "+ request.hero.name);
		        		
		        		cm.put(request.playerId, hero);
		        		NewHeroResponse response = new NewHeroResponse(request.playerId, request.hero);
		        		server.sendToAllUDP(response);
		        		updateLastAccess(request.playerId);
		        	}
		        	
		        	if (object instanceof PositionUpdate) {
					   // System.out.println("PositionUpdate eingegangen");
					
					   PositionUpdate request = (PositionUpdate)object;
					   positions.put(request.playerId, request.position);

					   updateLastAccess(request.playerId);
		        	}
			           
		           if (object instanceof GameStateRequest) {
		        	   // System.out.println("GameStateRequest eingegangen");

		        	   GameStateResponse response = new GameStateResponse();
		        	   response.positions = positions;
		        	   connection.sendUDP(response);
			       }
		           
		           if (object instanceof FullGameStateRequest) {
		        	   // System.out.println("FullGameStateRequest eingegangen");

		        	   FullGameStateResponse response = new FullGameStateResponse(cm);
		        	   connection.sendTCP(response);
			       }
		        }
		        
		        public void connected( Connection connection ) {
		        	clientIds.put(connection.getRemoteAddressTCP(), maxId);
		        	System.out.println("Connection incoming from " + connection.getRemoteAddressTCP());
		        	System.out.println("Assigned ID "+ maxId + " to Client.");
		        	IDAssignment idAssignment = new IDAssignment();
		        	idAssignment.id = maxId;
		        	connection.sendTCP(idAssignment);
			        updateLastAccess(maxId);
		        	maxId++;
		        }
		        
				public void disconnected (Connection connection) {
					InetSocketAddress ip = connection.getRemoteAddressTCP();
					if(ip == null) ip = connection.getRemoteAddressUDP();
					positions.remove(clientIds.get(ip));
					clientIds.remove(ip);
					System.out.println(ip + " has disconnected");
				}
		     });
		    
		    System.out.println("Server up and running");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateLastAccess(int clientId) {
		lastAccess.put(clientId, System.nanoTime());
	}
	
	public static int i;
	
	static Runnable updateGameState = new Runnable() {
		public void run() {
			if(clientIds.size() > 0) {
				// System.out.println(new java.util.Date() + " - "+ ++i +" - GameState an Clients geschickt ");
				GameStateResponse response = new GameStateResponse();
				response.positions = positions;
				server.sendToAllUDP(response);
			}
		}	
	};
	
	static Runnable deleteOldClients = new Runnable() {
	    public void run() {
	        for(Map.Entry<Integer,Long> ltime : lastAccess.entrySet())
	        {
	        	if( (System.nanoTime() - ltime.getValue())/1e6 > timeoutMs ) {
	        		int id = ltime.getKey();
	        		positions.remove(id);
	        		lastAccess.remove(id);
	        		cm.remove(id);
	        		server.sendToAllUDP(new DeleteHeroResponse(id));
	        		System.out.println("Automatically deleted Player "+ltime.getKey());
	        	}
	        }
	    }
	};
	
	public void stop() {
		server.stop();
	}
	
}
