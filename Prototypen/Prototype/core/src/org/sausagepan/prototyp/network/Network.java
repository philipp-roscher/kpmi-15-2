package org.sausagepan.prototyp.network;

import java.util.HashMap;

import org.sausagepan.prototyp.enums.DAMAGETYPE;
import org.sausagepan.prototyp.enums.WEAPONTYPE;
import org.sausagepan.prototyp.model.Status;
import org.sausagepan.prototyp.model.Weapon;
import org.sausagepan.prototyp.network.Position;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	public static final int TCPPort = 49078;
	public static final int UDPPort = 49318;
	
	public static void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
//		kryo.setRegistrationRequired(false);
		
		kryo.register(NewHeroRequest.class);
		kryo.register(NewHeroResponse.class);
		kryo.register(DeleteHeroResponse.class);
		kryo.register(PositionUpdate.class);
		kryo.register(GameStateRequest.class);
		kryo.register(GameStateResponse.class);
		kryo.register(FullGameStateRequest.class);
		kryo.register(FullGameStateResponse.class);		
		kryo.register(IDAssignment.class);
		
        kryo.register(Position.class);
        kryo.register(HeroInformation.class);
        kryo.register(Status.class);
        kryo.register(Weapon.class);
        kryo.register(DAMAGETYPE.class);
        kryo.register(WEAPONTYPE.class);
		kryo.register(Rectangle.class);
		kryo.register(Vector2.class);
		kryo.register(Vector3.class);
        kryo.register(HashMap.class);
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
		public Position position;
		
		public PositionUpdate() { }
	}
	
	
	public static class GameStateRequest {
		public GameStateRequest() { }
	}
	
	public static class GameStateResponse {
		public HashMap<Integer, Position> positions;
		
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
	
	public static class IDAssignment {
		public int id;
		
		public IDAssignment() { }
	}
}
