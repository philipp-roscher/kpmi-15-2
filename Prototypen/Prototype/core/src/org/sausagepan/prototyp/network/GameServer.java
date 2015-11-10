package org.sausagepan.prototyp.network;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.math.MathUtils;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.managers.ServerBattleSystem;
import org.sausagepan.prototyp.managers.ServerCharacterSystem;
import org.sausagepan.prototyp.managers.ServerPlayerManager;
import org.sausagepan.prototyp.model.ServerPlayer;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.GameStateRequest;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.KeepAliveRequest;
import org.sausagepan.prototyp.network.Network.LoseKeyRequest;
import org.sausagepan.prototyp.network.Network.LoseKeyResponse;
import org.sausagepan.prototyp.network.Network.MapInformation;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.HPUpdateRequest;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.IDAssignment;
import org.sausagepan.prototyp.network.Network.GameClientCount;
import org.sausagepan.prototyp.network.Network.ShootRequest;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.TakeKeyRequest;
import org.sausagepan.prototyp.network.Network.TakeKeyResponse;
import org.sausagepan.prototyp.network.Network.TeamAssignment;
import org.sausagepan.prototyp.network.Network.MaxClients;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
	// Zeit in Millisekunden, bevor ein inaktiver Spieler automatisch gel�scht wird
	public static final int timeoutMs = 5000;
	// Anzahl der GameStateUpdates pro Sekunde
	public static final int updateRate = 32;
	
	public static Server server;
	public static int maxId = 1;
	
	// contains client ids
	public static HashMap<InetSocketAddress,Integer> clientIds;
	// contains current positions of all characters sent by positionupdates
	public static HashMap<Integer,NetworkTransmissionComponent> positions;
	// saves the last time each client was active, used for kicking inactive clients
	public static HashMap<Integer,Long> lastAccess;
	// contains the classes of all characters
	public static HashMap<Integer,HeroInformation> cm;
	// contains the constellation of the individual tiles
	public static MapInformation map;
	//HashMap to save ClientIds,TeamIds
	public static HashMap<Integer,Integer> TeamAssignments;

	private static ServerPlayerManager playerMan = new ServerPlayerManager();
	// manages the characters
	private static ServerCharacterSystem serverCharacterSystem = new ServerCharacterSystem();
	private ServerBattleSystem bs;
	
	public static void main (String[] args) {
		// starts new server
		GameServer gs = new GameServer();
	}

	//to count active Clients in Session
	public static int clientCount;
	//maximal Number of Clients per Session
	private int maxClients = 5;


	public GameServer() {
		this.clientCount = 0;
		clientIds = new HashMap<InetSocketAddress, Integer>();
		positions = new HashMap<Integer,NetworkTransmissionComponent>();
		TeamAssignments = new HashMap<Integer, Integer>();
		lastAccess = new HashMap<Integer,Long>();		
		cm = new HashMap<Integer,HeroInformation>();
		bs = new ServerBattleSystem(this);
		setupMap(5,5);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(deleteOldClients, 0, 1, TimeUnit.SECONDS);
		executor.scheduleAtFixedRate(updateGameState, 0, 1000000L/updateRate, TimeUnit.MICROSECONDS);

		try {
			server = new Server();
			Network.register(server);
			server.start();
			server.bind(Network.TCPPort, Network.UDPPort);
			
		    server.addListener(new Listener() {
		        public void received (Connection connection, Object object) {
		        	if (object instanceof KeepAliveRequest) {
		        		// System.out.println("KeepAliveRequest von "+((KeepAliveRequest)object).playerId);
		        		updateLastAccess(((KeepAliveRequest)object).playerId);      		
		        	}
		        				        	
		        	if (object instanceof NewHeroRequest) {
		        		NewHeroRequest request = (NewHeroRequest) object;
		        		HeroInformation hero = request.hero;
		        		System.out.println("New Hero (ID " + request.playerId + "): " + request.hero.clientClass);
		        		connection.sendTCP(map);
		        		
		        		cm.put(request.playerId, hero);
                        serverCharacterSystem.addCharacter(request.playerId, new
								ServerCharacterEntity(request.playerId));
		        		NewHeroResponse response = new NewHeroResponse(request.playerId, request.hero);
		        		server.sendToAllUDP(response);
		        		//updateLastAccess(request.playerId);
		        	}
		        	
		        	if (object instanceof PositionUpdate) {
					   // System.out.println("PositionUpdate eingegangen");
					
					   PositionUpdate request = (PositionUpdate)object;
					   positions.put(request.playerId, request.position);
					   //serverCharacterSystem.updatePosition(request.playerId, request.position);
					   //playerMan.updatePosition(request.playerId, request.position);

					   updateLastAccess(request.playerId);
		        	}
			           
				    if (object instanceof GameStateRequest) {
					   // System.out.println("GameStateRequest eingegangen");

					   GameStateResponse response = new GameStateResponse();
					   response.positions = positions;
					   connection.sendUDP(response);
				    }

				    if (object instanceof FullGameStateRequest) {
					   System.out.println("FullGameStateRequest eingegangen");
					   FullGameStateResponse response = new FullGameStateResponse(cm);
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
		        	clientIds.put(connection.getRemoteAddressTCP(), maxId);
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
					//kann die getrennte IP nicht ausgeben, weil sie ja schon getrennt ist
					//InetSocketAddress ip = connection.getRemoteAddressTCP();
					//if(ip == null) ip = connection.getRemoteAddressUDP();

					//connection.id is (at the moment) identical to the ID in ClientIds
					int id = connection.getID();
					//only happens if it wasn't deleted with "deleteOldClients" beforehand
					if (positions.containsKey(id)) {
						positions.remove(id);
						lastAccess.remove(id);
						TeamAssignments.remove(id);
						cm.remove(id);
						server.sendToAllUDP(new DeleteHeroResponse(id));
						System.out.println("Automatically deleted Player "+connection);

						//decrease clientCount and send to all clients via TCP
						clientCount--;
						System.out.println("clientCount at: " + clientCount);
						GameClientCount GameClientCount = new GameClientCount();
						GameClientCount.count = clientCount;
						server.sendToAllTCP(clientCount);
						System.out.println("ENDE");
					}
				}
		     });
		    
		    System.out.println("Server up and running");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// saves current timestamp for a players last activity
	public static void updateLastAccess(int clientId) {
		lastAccess.put(clientId, System.nanoTime());
	}
	
	// sends current positions of all characters to all clients, is executed a defined amount of times per second
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
	
	// deletes all characters that haven't been active in the last x seconds
	static Runnable deleteOldClients = new Runnable() {
	    public void run() {
	        for(Map.Entry<Integer,Long> ltime : lastAccess.entrySet())
	        {
	        	if( (System.nanoTime() - ltime.getValue())/1e6 > timeoutMs ) {
	        		int id = ltime.getKey();
					//only happens if it wasn't deleted with "disconnected" beforehand
					if (positions.containsKey(id)) {
						positions.remove(id);
						lastAccess.remove(id);
						TeamAssignments.remove(id);
						cm.remove(id);
						server.sendToAllUDP(new DeleteHeroResponse(id));
						System.out.println("Automatically deleted Player "+ltime.getKey());
						//decrease clientCount and send to all clients via TCP
						clientCount--;
						System.out.println("clientCount at: " + clientCount);
						GameClientCount GameClientCount = new GameClientCount();
						GameClientCount.count = clientCount;
						server.sendToAllTCP(clientCount);
					}
	        	}
	        }
	    }
	};
	
	// inflicts damage to a certain character
	// TODO: update to new system
	/*
	 * @Deprecated
	public void inflictDamage(int playerId, int damage) {
		ServerPlayer player = playerMan.players.get(playerId);
		player.getStatus_().doPhysicalHarm(damage);
		
		if (player.getStatus_().getHP() == 0) {
			// TODO
			// Player dies
		}
		
		System.out.println(damage + " Schaden an Spieler Nr. "+playerId+", hat jetzt noch "+ player.getStatus_().getHP() +" HP.");
		
		server.sendToAllTCP(new HPUpdate(playerId, player.getStatus_().getHP()));
	}*/
	
	// generates random map with given width and height
	public void setupMap(int width, int height) {
		map = new MapInformation();
		map.height = height;
		map.width = width;
		map.entries = new HashMap<Vector2, Integer>();
		System.out.println(map.entries);
		for(int i = height; i > 0; i--)
			for(int j = width; j > 0; j--)
				map.entries.put(new Vector2(i,j), MathUtils.random(1,12));
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

		Collection<Integer> ClientCol = clientIds.values();
		for (int i=1; i<=ClientCol.size(); i++) {
			//System.out.println("Checking TeamId with ClientId: "+ i + " Result: "+ TeamAssignments.get(i));
			//read TeamIds and count them
			if (TeamAssignments.get(i) != null) {
				if (TeamAssignments.get(i) == 0){ Team0++; }
				if (TeamAssignments.get(i) == 1){ Team1++; }
				if (TeamAssignments.get(i) == 2){ Team2++; }
			}
		}
		System.out.println("Team0: "+Team0+" - Team1: "+Team1+" - Team2: "+Team2);

		TeamAssignment TeamAssignment = new TeamAssignment();
		//check for free space in Teams
		if (Team0 < 1) {
			TeamAssignment.id = 0;
			TeamAssignments.put(ClientId, 0);
		}
		else if (Team1 < 2) {
			TeamAssignment.id = 1;
			TeamAssignments.put(ClientId, 1);
		}
		else if (Team2 < 2) {
			TeamAssignment.id = 2;
			TeamAssignments.put(ClientId, 2);
		}
		else { System.out.println("all Teams are full");
		}

		server.sendToTCP(ClientId, TeamAssignment);
		System.out.println("Team Id "+TeamAssignment.id+" assigned to ClientId "+ClientId);

	}
	
}
