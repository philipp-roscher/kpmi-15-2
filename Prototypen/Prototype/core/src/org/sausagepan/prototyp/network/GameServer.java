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
import org.sausagepan.prototyp.model.ServerSettings;
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
	private static final int timeoutMs = ServerSettings.TIMEOUT_MS;
	
	private static Server server;
	private static ServerEntityComponentSystem ECS;
	private static long lastUpdate;
	private static float delta;
	
	// saves the last time each client was active, used for kicking inactive clients
	private static HashMap<Integer,Long> lastAccess;
	// container for deleted clients
	private static ArrayList<Integer> toDelete = new ArrayList<Integer>();
	// contains the constellation of the individual tiles
	private static MapInformation map;
	//HashMap to save ClientIds,TeamIds
	private static HashMap<Integer,Integer> teamAssignments;
	// manages the characters
    private List<Integer> roomList;

	//to count active Clients in Session
    private static int clientCount;
	//maximal Number of Clients per Session
	private int maxClients = ServerSettings.MANDATORY_CLIENTS;
	
	public void create () {
        this.roomList = new LinkedList<Integer>();
        for(int i=1; i <= ServerSettings.MAZE_AREAS; i++) roomList.add(i);

		clientCount = 0;
		teamAssignments = new HashMap<Integer, Integer>();
		lastAccess = new HashMap<Integer,Long>();
		setupMap(5,5);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		executor.scheduleAtFixedRate(deleteOldClients, 0, 1, TimeUnit.SECONDS);

		try {
			server = new Server();
			Network.register(server);
			server.start();
			server.bind(Network.TCPPort, Network.UDPPort);
			
		    server.addListener(new Listener() {
		        public void connected( Connection connection ) {
		        	System.out.println("Connection incoming from " + connection.getRemoteAddressTCP());
		        	IDAssignment idAssignment = new IDAssignment();
		        	idAssignment.id = connection.getID();
		        	System.out.println("Assigned ID "+ idAssignment.id + " to Client.");
		        	connection.sendTCP(idAssignment);
		        	
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
		    
			ECS = new ServerEntityComponentSystem(map, server, this);
		    System.out.println("Server up and running");
		    lastUpdate = System.nanoTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resize (int width, int height) {
		
	}

	public void render () {
		long currentTime = System.nanoTime();
		delta = (currentTime - lastUpdate)/ 1e9f;
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
		response = ECS.getGameState();
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
	
	public MapInformation getMap() {
		return map;
	}
	
	public HashMap<Integer,Integer> getTeamAssignments() {
		return teamAssignments;
	}
}
