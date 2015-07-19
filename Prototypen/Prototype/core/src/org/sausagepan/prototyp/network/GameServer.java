package org.sausagepan.prototyp.network;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.math.MathUtils;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.managers.PlayerManager;
import org.sausagepan.prototyp.managers.ServerBattleSystem;
import org.sausagepan.prototyp.managers.ServerPlayerManager;
import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.model.ServerPlayer;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.GameStateRequest;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.KeepAliveRequest;
import org.sausagepan.prototyp.network.Network.MapInformation;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.HPUpdate;
import org.sausagepan.prototyp.network.Network.IDAssignment;
import org.sausagepan.prototyp.network.Position;

import box2dLight.RayHandler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
	// Zeit in Millisekunden, bevor ein inaktiver Spieler automatisch gel�scht wird
	public static final int timeoutMs = 10000;
	// Anzahl der GameStateUpdates pro Sekunde
	public static final int updateRate = 32;
	
	public static Server server;
	public static int maxId = 1;
	public static HashMap<InetSocketAddress,Integer> clientIds;
	public static HashMap<Integer,Position> positions;
	public static HashMap<Integer,Long> lastAccess;
	public static HashMap<Integer,HeroInformation> cm;
	public static MapInformation map;
	
	private static ServerPlayerManager playerMan = new ServerPlayerManager();
	private ServerBattleSystem bs;
	
	public static void main (String[] args) {
		GameServer gs = new GameServer();
	}
	
	public GameServer() {
		clientIds = new HashMap<InetSocketAddress, Integer>();
		positions = new HashMap<Integer,Position>();
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
		        		System.out.println("New Hero (ID "+ request.playerId +"): "+ request.hero.name);
		        		
		        		cm.put(request.playerId, hero);
		        		playerMan.addCharacter(request.playerId, new ServerPlayer(
                                hero.name,
                                hero.sex,
                                request.playerId,
                                hero.spriteSheet,
                                hero.status,
                                hero.weapon,
                                false));
		        		NewHeroResponse response = new NewHeroResponse(request.playerId, request.hero);
		        		server.sendToAllUDP(response);
		        		updateLastAccess(request.playerId);
		        	}
		        	
		        	if (object instanceof PositionUpdate) {
					   // System.out.println("PositionUpdate eingegangen");
					
					   PositionUpdate request = (PositionUpdate)object;
					   positions.put(request.playerId, request.position);
					   playerMan.updatePosition(request.playerId, request.position);

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

		        	   FullGameStateResponse response = new FullGameStateResponse(cm, map);
		        	   connection.sendTCP(response);
			       }
		           
		           if (object instanceof AttackRequest) {
					   AttackRequest request = (AttackRequest)object;
					   server.sendToAllUDP(new AttackResponse(request.playerId, request.stop));
					   if(request.stop == false)
						   bs.attack(playerMan.players.get(request.playerId), playerMan.getPlayers());
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
	
	public void inflictDamage(int playerId, int damage) {
		ServerPlayer player = playerMan.players.get(playerId);
		player.getStatus_().doPhysicalHarm(damage);
		
		if (player.getStatus_().getHP() == 0) {
			// Player dies
		}
		
		System.out.println(damage + " Schaden an Spieler Nr. "+playerId+", hat jetzt noch "+ player.getStatus_().getHP() +" HP.");
		
		server.sendToAllTCP(new HPUpdate(playerId, player.getStatus_().getHP()));
	}
	
	public void setupMap(int width, int height) {
		this.map = new MapInformation();
		map.height = height;
		map.width = width;
		map.entries = new HashMap<Vector2, Integer>();
		System.out.println(map.entries);
		for(int i = height; i > 0; i--)
			for(int j = width; j > 0; j--)
				map.entries.put(new Vector2(i,j), MathUtils.random(1,7));
	}
	
	public void stop() {
		server.stop();
	}
	
}
