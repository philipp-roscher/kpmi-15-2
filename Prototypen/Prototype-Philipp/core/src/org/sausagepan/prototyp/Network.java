package org.sausagepan.prototyp;

import java.util.HashMap;

import org.sausagepan.prototyp.model.Position;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	public static final int TCPPort = 49078;
	public static final int UDPPort = 49318;
	
	public static void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
//		kryo.setRegistrationRequired(false);
		kryo.register(PositionUpdate.class);
		kryo.register(GameStateRequest.class);
		kryo.register(GameStateResponse.class);
		kryo.register(IDAssignment.class);
        kryo.register(Position.class);
		kryo.register(Vector3.class);
        kryo.register(HashMap.class);
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
	
	public static class IDAssignment {
		public int id;
		public IDAssignment() { }
	}
}
