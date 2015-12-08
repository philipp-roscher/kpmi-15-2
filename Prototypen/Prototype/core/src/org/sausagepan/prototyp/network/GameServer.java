package org.sausagepan.prototyp.network;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import org.sausagepan.prototyp.managers.ServerEntityComponentSystem;
import org.sausagepan.prototyp.model.ServerSettings;
import org.sausagepan.prototyp.network.Network.GameClientCount;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.IDAssignment;
import org.sausagepan.prototyp.network.Network.MapInformation;
import org.sausagepan.prototyp.network.Network.MaxClients;
import org.sausagepan.prototyp.network.Network.TeamAssignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GameServer implements ApplicationListener {
	// Zeit in Millisekunden, bevor ein inaktiver Spieler automatisch gel�scht wird
	private static final int timeoutMs = ServerSettings.TIMEOUT_MS;
	
	private static Server server;
	private static ServerEntityComponentSystem ECS;
	private static long lastUpdate;
	private static float delta;

	// container for deleted clients
	private static ArrayList<Integer> toDelete = new ArrayList<Integer>();
	// contains the constellation of the individual tiles
	private static MapInformation map;
	// HashMap to save ClientIds,TeamIds
	private static HashMap<Integer,Integer> teamAssignments;
	// manages the characters
    private List<Integer> roomList;

	// to count active Clients in Session
    private static int clientCount;
	// maximum Number of Clients per Session
	private int maxClients = ServerSettings.MANDATORY_CLIENTS;
	
	public void create () {
		clientCount = 0;
		teamAssignments = new HashMap<Integer, Integer>();
		setupMap(ServerSettings.MAZE_WIDTH, ServerSettings.MAZE_HEIGHT);

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
					GameClientCount gameClientCount = new GameClientCount();
					gameClientCount.count = clientCount;
					server.sendToAllTCP(gameClientCount);
					//assignTeamId
					assignTeam(idAssignment.id);

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
	
	public static void sendGameState() {
		GameStateResponse response = new GameStateResponse();
		response = ECS.getGameState();
		server.sendToAllUDP(response);
	}
	
	//  updates ECS and sends current positions of all characters to all clients, is executed a defined amount of times per second
	public static void updateGameState() {
		ECS.update(delta);
		if(clientCount > 0) {
			// System.out.println(new java.util.Date() + " - "+ ++i +" - GameState an Clients geschickt ");
			sendGameState();			
		}
	}
	
	// generates random map with given width and height
	public void setupMap(int width, int height) {
        this.roomList = new LinkedList<Integer>();
        for(int i=1; i <= ServerSettings.MAZE_AREAS; i++) roomList.add(i);

		map = new MapInformation();
		map.height = height;
		map.width = width;
		map.entries = new HashMap<Vector2, Integer>();
		System.out.println(map.entries);
		for(int i = width; i > 0; i--)
			for(int j = height; j > 0; j--) {
				if (i == (int) Math.ceil(width / 2f) && j == (int) Math.ceil(height / 2f)) {
					// Mark treasure room with -1
	                map.entries.put(new Vector2(i, j), -1);
				} else {
	                int r = MathUtils.random(1, roomList.size());
	                map.entries.put(new Vector2(i, j), roomList.get(r-1));
	                roomList.remove(r-1);
	                if(roomList.isEmpty())
	                    for(int t=1; t <= ServerSettings.MAZE_AREAS; t++)
	                    	roomList.add(t);
				}
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

    public void deleteCharacter(int id) {
        teamAssignments.remove(id);

        //decrease clientCount and send to all clients via TCP
        clientCount--;
        System.out.println("clientCount at: " + clientCount);
        GameClientCount gameClientCount = new GameClientCount();
        gameClientCount.count = clientCount;
        server.sendToAllTCP(gameClientCount);
    }
}
