package org.sausagepan.prototyp.network;

import java.util.HashMap;

import org.sausagepan.prototyp.User_Interface.Actors.KeyActor;
import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.enums.Weapontype;
import org.sausagepan.prototyp.graphics.EntitySprite;
import org.sausagepan.prototyp.model.Key;
import org.sausagepan.prototyp.model.Status;
import org.sausagepan.prototyp.model.Weapon;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.IntArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	public static final int TCPPort = 49078;
	public static final int UDPPort = 49318;
	
	public static void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		//kryo.setRegistrationRequired(false);
		
		kryo.register(KeepAliveRequest.class);
		kryo.register(NewHeroRequest.class);
		kryo.register(NewHeroResponse.class);
		kryo.register(DeleteHeroResponse.class);
		kryo.register(PositionUpdate.class);
		kryo.register(AttackRequest.class);
		kryo.register(AttackResponse.class);
		kryo.register(ShootRequest.class);
		kryo.register(ShootResponse.class);
		kryo.register(HPUpdateRequest.class);
		kryo.register(HPUpdateResponse.class);
		kryo.register(GameStateRequest.class);
		kryo.register(GameStateResponse.class);
		kryo.register(FullGameStateRequest.class);
		kryo.register(FullGameStateResponse.class);
		kryo.register(TakeKeyRequest.class);
		kryo.register(TakeKeyResponse.class);
		kryo.register(LoseKeyRequest.class);
		kryo.register(LoseKeyResponse.class);
		kryo.register(IDAssignment.class);
		kryo.register(GameClientCount.class);
		kryo.register(TeamAssignment.class);
		kryo.register(MaxClients.class);
		kryo.register(MapInformation.class);

        kryo.register(NetworkPosition.class);
        kryo.register(NetworkTransmissionComponent.class);
        kryo.register(Direction.class);
        kryo.register(HeroInformation.class);
        kryo.register(Status.class);
        kryo.register(Weapon.class);
        kryo.register(Damagetype.class);
        kryo.register(Weapontype.class);
		kryo.register(Rectangle.class);
		kryo.register(Vector2.class);
		kryo.register(Vector3.class);
        kryo.register(HashMap.class);
        kryo.register(KeySection.class);
	}

	public static class KeepAliveRequest {
		public int playerId;
		
		public KeepAliveRequest() { }
		public KeepAliveRequest(int playerId) {
			this.playerId = playerId;
		}
	}
	public static class NewHeroRequest {
		public int playerId;
		public HeroInformation hero;
		
		public NewHeroRequest() { }
		public NewHeroRequest(int playerId, HeroInformation hero) {
			this.playerId = playerId;
			this.hero = hero;
		}
	}
	
	public static class NewHeroResponse {
		public int playerId;
		public HeroInformation hero;
		
		public NewHeroResponse() { }
		public NewHeroResponse(int playerId, HeroInformation hero) {
			this.playerId = playerId;
			this.hero = hero;
		}
	}	
	
	public static class DeleteHeroResponse {
		public int playerId;
		public DeleteHeroResponse() { }
		public DeleteHeroResponse(int playerId) {
			this.playerId = playerId;
		}
	}
	
	public static class PositionUpdate {
		public int playerId;
		public NetworkTransmissionComponent position;
		
		public PositionUpdate() { }
	}	
	
	public static class AttackRequest {
		public int playerId;
		public boolean stop;

		public AttackRequest() { }
		public AttackRequest(int playerId, boolean stop) {
			this.playerId = playerId;
			this.stop = stop;
		}
	}
	
	public static class AttackResponse {
		public int playerId;
		public boolean stop;

		public AttackResponse() { }
		public AttackResponse(int playerId, boolean stop) {
			this.playerId = playerId;
			this.stop = stop;
		}
	}
	
	public static class ShootRequest {
		public int playerId;
		public Vector2 position;
		public Vector2 direction;

		public ShootRequest() { }
		public ShootRequest(int playerId, Vector2 position, Vector2 direction) {
			this.playerId = playerId;
			this.position = position;
			this.direction = direction;
		}
	}
	
	public static class ShootResponse {
		public int playerId;
		public Vector2 position;
		public Vector2 direction;

		public ShootResponse() { }
		public ShootResponse(int playerId, Vector2 position, Vector2 direction) {
			this.playerId = playerId;
			this.position = position;
			this.direction = direction;
		}
	}
	
	public static class HPUpdateRequest {
		public int playerId;
		public int HP;

		public HPUpdateRequest() { }
		public HPUpdateRequest(int playerId, int HP) {
			this.playerId = playerId;
			this.HP = HP;
		}		
	}
	
	public static class HPUpdateResponse {
		public int playerId;
		public int HP;

		public HPUpdateResponse() { }
		public HPUpdateResponse(int playerId, int HP) {
			this.playerId = playerId;
			this.HP = HP;
		}		
	}
	
	public static class GameStateRequest {
		public GameStateRequest() { }
	}
	
	public static class GameStateResponse {
		public HashMap<Integer, NetworkTransmissionComponent> positions;
		
		public GameStateResponse() { }
	}
	
	public static class FullGameStateRequest {
		public FullGameStateRequest() { }
	}
	
	public static class FullGameStateResponse {
		public HashMap<Integer,HeroInformation> heroes;
		
		public FullGameStateResponse() { }
		public FullGameStateResponse(HashMap<Integer,HeroInformation> heroes) {
			this.heroes = heroes;
		}
	}
	
	public static class TakeKeyRequest {
		public int id;
		public KeySection keySection;
		
		public TakeKeyRequest() { }
		public TakeKeyRequest(int id, KeySection keySection) {
			this.id = id;
			this.keySection = keySection;
		}
	}
	
	public static class TakeKeyResponse {
		public int id;
		public KeySection keySection;
		
		public TakeKeyResponse() { }
		public TakeKeyResponse(int id, KeySection keySection) {
			this.id = id;
			this.keySection = keySection;
		}
	}
	
	public static class LoseKeyRequest {
		public int id;
		public KeySection keySection;
		public float x;
		public float y;
		
		public LoseKeyRequest() { }
		public LoseKeyRequest(int id, KeySection keySection, float x, float y) {
			this.id = id;
			this.keySection = keySection;
			this.x = x;
			this.y = y;
		}
	}
	
	public static class LoseKeyResponse {
		public int id;
		public KeySection keySection;
		public float x;
		public float y;
		
		public LoseKeyResponse() { }
		public LoseKeyResponse(int id, KeySection keySection, float x, float y) {
			this.id = id;
			this.keySection = keySection;
			this.x = x;
			this.y = y;
		}
	}
	
	public static class IDAssignment {
		public int id;
		
		public IDAssignment() { }
	}

	public static class GameClientCount {
		public int count;

		public GameClientCount() { }
	}

	public static class TeamAssignment {
		public int id;

		public TeamAssignment() { }
	}

	public static class MaxClients {
		public int count;

		public MaxClients() { }
	}

	public static class MapInformation {
		public int height, width;
		public HashMap<Vector2,Integer> entries;
		
		public MapInformation() { }
		public MapInformation(int height, int width, HashMap<Vector2,Integer> entries) {
			this.height = height;
			this.width = width;
			this.entries = entries;
		}
	}
}
