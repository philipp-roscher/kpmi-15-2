package org.sausagepan.prototyp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.sausagepan.prototyp.Network.GameStateRequest;
import org.sausagepan.prototyp.Network.GameStateResponse;
import org.sausagepan.prototyp.Network.PositionUpdate;
import org.sausagepan.prototyp.Network.IDAssignment;
import org.sausagepan.prototyp.model.Position;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
	public static Server server;
	public static HashMap<InetSocketAddress,Integer> clientIds;
	public static int maxId = 1;
	public static final int timeoutMs = 5000;
	public static HashMap<Integer,Position> positions;
	public static HashMap<Integer,Long> lastAccess;
	
	public static void main(String[] args) {
		clientIds = new HashMap<InetSocketAddress, Integer>();
		positions = new HashMap<Integer,Position>();
		lastAccess = new HashMap<Integer,Long>();		

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(deleteOldClients, 0, 1, TimeUnit.SECONDS);

		try {
			server = new Server();
			Network.register(server);
			server.start();
			server.bind(Network.TCPPort, Network.UDPPort);
			
		    server.addListener(new Listener() {
		        public void received (Connection connection, Object object) {
		        	if (object instanceof PositionUpdate) {
			        	   //System.out.println("PositionUpdate eingegangen");
		        		
			        	   PositionUpdate request = (PositionUpdate)object;
			        	   positions.put(request.playerId, request.position);
			        	   GameStateResponse response = new GameStateResponse();
			        	   response.positions = positions;
			        	   connection.sendTCP(response);
				           updateLastAccess(request.playerId);
			           }
			           
			           if (object instanceof GameStateRequest) {
			        	   //System.out.println("GameStateRequest eingegangen");

			        	   GameStateResponse response = new GameStateResponse();
			        	   response.positions = positions;
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
	
	static Runnable deleteOldClients = new Runnable() {
	    public void run() {
	        for(Map.Entry<Integer,Long> ltime : lastAccess.entrySet())
	        {
	        	if( (System.nanoTime() - ltime.getValue())/1e6 > timeoutMs ) {
	        		positions.remove(ltime.getKey());
	        		lastAccess.remove(ltime.getKey());
	        		System.out.println("Automatically deleted Player "+ltime.getKey());
	        	}
	        }
	    }
	};
	
	public void stop() {
		server.stop();
	}
	
}
