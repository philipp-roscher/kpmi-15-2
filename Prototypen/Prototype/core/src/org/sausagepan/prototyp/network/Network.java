package org.sausagepan.prototyp.network;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.enums.Weapontype;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.entities.MapFactoryObject;
import org.sausagepan.prototyp.model.entities.MapMonsterObject;

import java.util.HashMap;

public class Network {
	public static final int TCPPort = 49078;
	public static final int UDPPort = 49318;
	
	public static void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		//kryo.setRegistrationRequired(false);
		
		kryo.register(NewHeroRequest.class);
		kryo.register(NewHeroResponse.class);
		kryo.register(DeleteHeroResponse.class);
		kryo.register(PositionUpdate.class);
		kryo.register(AttackRequest.class);
		kryo.register(AttackResponse.class);
		kryo.register(ShootRequest.class);
		kryo.register(ShootResponse.class);
		kryo.register(HPUpdateResponse.class);
		kryo.register(GameStateResponse.class);
		kryo.register(FullGameStateRequest.class);
		kryo.register(FullGameStateResponse.class);
        kryo.register(DeleteBulletResponse.class);
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
        kryo.register(MapMonsterObject.class);
        kryo.register(MapFactoryObject.class);
        kryo.register(Direction.class);
        kryo.register(Damagetype.class);
        kryo.register(Weapontype.class);
		kryo.register(Rectangle.class);
		kryo.register(Vector2.class);
		kryo.register(Vector3.class);
        kryo.register(HashMap.class);
		kryo.register(CharacterClass.class);
	}
	

	public static class NetworkPosition {
		public Vector2 position;
	    public Vector2 velocity;
		public Vector2 bodyDirection;
	    public Direction direction;
	    public boolean moving;
		
		public NetworkPosition() {}
		public NetworkPosition(Vector2 position, Vector2 velocity, Vector2 bodyDirection, Direction direction, boolean moving) {
			this.position = position;
			this.velocity = velocity;
            this.bodyDirection = bodyDirection;
			this.direction = direction;
			this.moving = moving;
		}
	}
	
	public static class NewHeroRequest {
		public int playerId;
		public CharacterClass clientClass;
		
		public NewHeroRequest() { }
		public NewHeroRequest(int playerId, CharacterClass clientClass) {
			this.playerId = playerId;
			this.clientClass = clientClass;
		}
	}
	
	public static class NewHeroResponse {
		public int playerId;
		public int teamId;
		public CharacterClass clientClass;
		
		public NewHeroResponse() { }
		public NewHeroResponse(int playerId, int teamId, CharacterClass clientClass) {
			this.playerId = playerId;
			this.teamId = teamId;
			this.clientClass = clientClass;
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
		public NetworkPosition position;
		
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

		public ShootRequest() { }
		public ShootRequest(int playerId) {
			this.playerId = playerId;
		}
	}
	
	public static class ShootResponse {
		public int playerId;
		public Vector2 position;
		public Vector2 direction;
        public int bulletId;

		public ShootResponse() { }
		public ShootResponse(int playerId, Vector2 position, Vector2 direction, int bulletId) {
			this.playerId = playerId;
			this.position = position;
			this.direction = direction;
            this.bulletId = bulletId;
		}
	}
	
	public static class HPUpdateResponse {
		public int playerId;
        public boolean isHuman;
		public int HP;

		public HPUpdateResponse() { }
		public HPUpdateResponse(int playerId, boolean isHuman, int HP) {
			this.playerId = playerId;
            this.isHuman = isHuman;
			this.HP = HP;
		}		
	}
	
	public static class GameStateResponse {
		public HashMap<Integer,NetworkPosition> characters;
		public HashMap<Integer,NetworkPosition> monsters;
		
		public GameStateResponse() { }
	}
	
	public static class FullGameStateRequest {
		public FullGameStateRequest() { }
	}
	
	public static class FullGameStateResponse {
		public HashMap<Integer,CharacterClass> heroes;
		public HashMap<Integer,MapMonsterObject> monsters; 
		public HashMap<Integer,Integer> teamAssignments;
		
		public FullGameStateResponse() { }
		public FullGameStateResponse(HashMap<Integer,CharacterClass> heroes, HashMap<Integer,MapMonsterObject> monsters, HashMap<Integer, Integer> teamAssignments) {
			this.heroes = heroes;
			this.monsters = monsters;
			this.teamAssignments = teamAssignments;
		}
	}

    public static class DeleteBulletResponse {
        public int playerId;
        public int bulletId;

        public DeleteBulletResponse() { }
        public DeleteBulletResponse(int playerId, int bulletId) {
            this.playerId = playerId;
            this.bulletId = bulletId;
        }
    }
	
	public static class TakeKeyRequest {
		public int id;
		public int keySection;
		
		public TakeKeyRequest() { }
		public TakeKeyRequest(int id, int keySection) {
			this.id = id;
			this.keySection = keySection;
		}
	}
	
	public static class TakeKeyResponse {
		public int id;
		public int keySection;
		
		public TakeKeyResponse() { }
		public TakeKeyResponse(int id, int keySection) {
			this.id = id;
			this.keySection = keySection;
		}
	}
	
	public static class LoseKeyRequest {
		public int id;
		public int keySection;
		public float x;
		public float y;
		
		public LoseKeyRequest() { }
		public LoseKeyRequest(int id, int keySection, float x, float y) {
			this.id = id;
			this.keySection = keySection;
			this.x = x;
			this.y = y;
		}
	}
	
	public static class LoseKeyResponse {
		public int id;
		public int keySection;
		public float x;
		public float y;
		
		public LoseKeyResponse() { }
		public LoseKeyResponse(int id, int keySection, float x, float y) {
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
