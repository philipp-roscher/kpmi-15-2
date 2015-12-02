package org.sausagepan.prototyp.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.managers.ServerEntityComponentSystem;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.GameClientCount;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.HPUpdateRequest;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.IDAssignment;
import org.sausagepan.prototyp.network.Network.LoseKeyRequest;
import org.sausagepan.prototyp.network.Network.LoseKeyResponse;
import org.sausagepan.prototyp.network.Network.MapInformation;
import org.sausagepan.prototyp.network.Network.MaxClients;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.ShootRequest;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.TakeKeyRequest;
import org.sausagepan.prototyp.network.Network.TakeKeyResponse;
import org.sausagepan.prototyp.network.Network.TeamAssignment;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer implements ApplicationListener {
	// Zeit in Millisekunden, bevor ein inaktiver Spieler automatisch gel�scht wird
	public static final int timeoutMs = GlobalSettings.TIMEOUT_MS;
	
	public static Server server;
	public static int maxId = 1;
	public static ServerEntityComponentSystem ECS;
	private static long lastUpdate;
	private static long delta;
	
	// saves the last time each client was active, used for kicking inactive clients
	public static HashMap<Integer,Long> lastAccess;
	// container for deleted clients
	public static ArrayList<Integer> toDelete = new ArrayList<Integer>();
	// contains the classes of all characters
	public static HashMap<Integer,CharacterClass> cm;
	// contains the constellation of the individual tiles
	public static MapInformation map;
	//HashMap to save ClientIds,TeamIds
	public static HashMap<Integer,Integer> teamAssignments;
	// manages the characters
    private List<Integer> roomList;

	//to count active Clients in Session
	public static int clientCount;
	//maximal Number of Clients per Session
	private int maxClients = GlobalSettings.MANDATORY_CLIENTS;
	
	public void create () {
        this.roomList = new LinkedList<Integer>();
        for(int i=1; i <= GlobalSettings.MAZE_AREAS; i++) roomList.add(i);

		clientCount = 0;
		teamAssignments = new HashMap<Integer, Integer>();
		lastAccess = new HashMap<Integer,Long>();
		cm = new HashMap<Integer,CharacterClass>();
		setupMap(5,5);
		
		ECS = new ServerEntityComponentSystem(map);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		executor.scheduleAtFixedRate(deleteOldClients, 0, 1, TimeUnit.SECONDS);

		try {
			server = new Server();
			Network.register(server);
			server.start();
			server.bind(Network.TCPPort, Network.UDPPort);
			
		    server.addListener(new Listener() {
		        public void received (Connection connection, Object object) {		        				        	
		        	if (object instanceof NewHeroRequest) {
		        		NewHeroRequest request = (NewHeroRequest) object;
		        		System.out.println("New Hero (ID " + request.playerId + "): " + request.clientClass);
		        		connection.sendTCP(map);
		        		ECS.addNewCharacter(request.playerId, teamAssignments.get(request.playerId), request.clientClass);
		        		cm.put(request.playerId, request.clientClass);
		        		NewHeroResponse response = new NewHeroResponse(request.playerId, teamAssignments.get(request.playerId), request.clientClass);
		        		server.sendToAllUDP(response);
		        		//updateLastAccess(request.playerId);
		        	}
		        	
		        	if (object instanceof PositionUpdate) {
					   // System.out.println("PositionUpdate eingegangen");
					
					   PositionUpdate request = (PositionUpdate)object;
					   ECS.updatePosition(request.playerId, request.position);
					   updateLastAccess(request.playerId);
		        	}

				    if (object instanceof FullGameStateRequest) {
					   System.out.println("FullGameStateRequest eingegangen");
					   FullGameStateResponse response = new FullGameStateResponse(cm, teamAssignments);
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
		        
		        public void connected( Connection connection ) {
		        	System.out.println("Connection incoming from " + connection.getRemoteAddressTCP());
		        	System.out.println("Assigned ID "+ maxId + " to Client.");
		        	IDAssignment idAssignment = new IDAssignment();
		        	idAssignment.id = maxId;
		        	connection.sendTCP(idAssignment);
			        //updateLastAccess(maxId);
		        	maxId++;
					//send maxClients to Client(s)
					MaxClients MaxClients = new MaxClients();
					MaxClients.count = maxClients;
					connection.sendTCP(MaxClients);

					//MUSS HIER PASSIEREN!!! nicht in NewHeroRequest verschieben!!!
					//increase ClientCount and send to all clients via TCP
					clientCount++;
					System.out.println("clientCount at: "+clientCount);
					GameClientCount GameClientCount = new GameClientCount();
					GameClientCount.count = clientCount;
					server.sendToAllTCP(GameClientCount);
					//assignTeamId
					assignTeam(idAssignment.id);

		        }
		        
				public void disconnected (Connection connection) {
					//connection.id is (at the moment) identical to the ID in ClientIds
					int id = connection.getID();
					//only happens if it wasn't deleted with "deleteOldClients" beforehand
					if (ECS.getCharacter(id) != null) {
						ECS.deleteCharacter(id);
						lastAccess.remove(id);
						teamAssignments.remove(id);
						cm.remove(id);
						server.sendToAllUDP(new DeleteHeroResponse(id));
						System.out.println("Automatically deleted Player "+connection);

						//decrease clientCount and send to all clients via TCP
						clientCount--;
						System.out.println("clientCount at: " + clientCount);
						GameClientCount GameClientCount = new GameClientCount();
						GameClientCount.count = clientCount;
						server.sendToAllTCP(clientCount);
					}
				}
		     });
		    
		    System.out.println("Server up and running");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resize (int width, int height) {
		
	}

	public void render () {
		long currentTime = System.nanoTime();
		delta = lastUpdate - currentTime;
		lastUpdate = currentTime;
		updateGameState();
	}

	public void pause () {
		
	}

	public void resume () {
		
	}

	public void dispose () {
		stop();
	}
	
	// saves current timestamp for a players last activity
	public static void updateLastAccess(int clientId) {
		lastAccess.put(clientId, System.nanoTime());
	}
	
	public static void sendGameState() {
		GameStateResponse response = new GameStateResponse();
		response.positions = ECS.getGameState();
		server.sendToAllUDP(response);
	}
	
	// sends current positions of all characters to all clients, is executed a defined amount of times per second
	public static void updateGameState() {
		ECS.update(delta);
		if(clientCount > 0) {
			// System.out.println(new java.util.Date() + " - "+ ++i +" - GameState an Clients geschickt ");
			sendGameState();				
		}
	}
	
	// deletes all characters that haven't been active in the last x seconds
	static Runnable deleteOldClients = new Runnable() {
	    public void run() {
	    	//System.out.println("Trying to remove old clients...");
	        for(Map.Entry<Integer,Long> ltime : lastAccess.entrySet())
	        {
	        	if( (System.nanoTime() - ltime.getValue())/1e6 > timeoutMs ) {
	        		int id = ltime.getKey();
					//only happens if it wasn't deleted with "disconnected" beforehand
					if (ECS.getCharacter(id) != null) {
						ECS.deleteCharacter(id);
						teamAssignments.remove(id);
						toDelete.add(id);
						cm.remove(id);
						server.sendToAllUDP(new DeleteHeroResponse(id));
						System.out.println("Automatically deleted Player "+ltime.getKey());

						//decrease clientCount and send to all clients via TCP
						clientCount--;
						System.out.println("clientCount at: "+clientCount);
						GameClientCount GameClientCount = new GameClientCount();
						GameClientCount.count = clientCount;
						server.sendToAllTCP(clientCount);
					}
	        	}
	        }
	        
	        for(int id : toDelete)
	        	lastAccess.remove(id);
	        
	        toDelete.clear();
	    }
	};
	
	// generates random map with given width and height
	public void setupMap(int width, int height) {
		map = new MapInformation();
		map.height = height;
		map.width = width;
		map.entries = new HashMap<Vector2, Integer>();
		System.out.println(map.entries);
		for(int i = height; i > 0; i--)
			for(int j = width; j > 0; j--) {
                System.out.println(roomList);
                int r = MathUtils.random(1, roomList.size());
                map.entries.put(new Vector2(i, j), roomList.get(r-1));
                roomList.remove(r-1);
            }
	}
	
	// stops the server
	public void stop() {
		server.stop();
	}

	//"random" Team-assignment
	public void assignTeam(int ClientId) {
		int Team0 = 0;
		int Team1 = 0;
		int Team2 = 0;

		for (int i=1; i<=clientCount; i++) {
			//System.out.println("Checking TeamId with ClientId: "+ i + " Result: "+ teamAssignments.get(i));
			//read TeamIds and count them
			if (teamAssignments.get(i) != null) {
				if (teamAssignments.get(i) == 0){ Team0++; }
				if (teamAssignments.get(i) == 1){ Team1++; }
				if (teamAssignments.get(i) == 2){ Team2++; }
			}
		}
		System.out.println("Team0: "+Team0+" - Team1: "+Team1+" - Team2: "+Team2);

		TeamAssignment TeamAssignment = new TeamAssignment();
		//check for free space in Teams
		if (Team0 < 1) {
			TeamAssignment.id = 0;
			teamAssignments.put(ClientId, 0);
		}
		else if (Team1 < 2) {
			TeamAssignment.id = 1;
			teamAssignments.put(ClientId, 1);
		}
		else if (Team2 < 2) {
			TeamAssignment.id = 2;
			teamAssignments.put(ClientId, 2);
		}
		else { System.out.println("all Teams are full");
		}

		server.sendToTCP(ClientId, TeamAssignment);
		System.out.println("Team Id "+TeamAssignment.id+" assigned to ClientId "+ClientId);

	}
	
}
