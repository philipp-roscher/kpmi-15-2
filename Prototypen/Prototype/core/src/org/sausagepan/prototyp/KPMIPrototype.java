package org.sausagepan.prototyp;

import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.network.Network.GameClientCount;
import org.sausagepan.prototyp.network.Network.GameStart;
import org.sausagepan.prototyp.network.Network.IDAssignment;
import org.sausagepan.prototyp.network.Network.MaxClients;
import org.sausagepan.prototyp.network.Network.TeamAssignment;
import org.sausagepan.prototyp.view.IntroScreen;
import org.sausagepan.prototyp.view.MainMenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class KPMIPrototype extends Game {
	
	/* ............................................................................ ATTRIBUTES .. */
	public SpriteBatch batch;
	public BitmapFont font;
	public MediaManager mediaManager;
	public Client client;
	public boolean connected = false;
	public int clientId;

	//Number of Players needed to start game -> set on Server!
	public int maxClients;
	//counts players on server
	public int clientCount;

	public boolean gameReady = false;
	public boolean gameWon = false;
	public boolean gameLost = false;

	public int TeamId;
	public boolean TeamAssignmentReceived = false;

	/* .................................................... LibGDX METHODS .. */
	@Override
	public void create () {
		batch = new SpriteBatch();
		font  = new BitmapFont();
		mediaManager = new MediaManager();

		startClient();
		
		// switch to main menu screen
//		this.setScreen(new MainMenuScreen(this));
		this.setScreen(new IntroScreen(this));
	}

	public void startClient() {
		// Client starten
		client = new Client();
		new Thread(client).start();
		Network.register(client);
		
		//Listeners to receive data from server
		client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				//receives Client ID
				if (object instanceof IDAssignment) {
					IDAssignment result = (IDAssignment) object;
					clientId = result.id;
				}
	
				//receives current number of clients
				if (object instanceof GameClientCount) {
					GameClientCount result = (GameClientCount) object;
					clientCount = result.count;
					if(result.gameReady)
	                    gameReady = true;
				}
				
				//receives Team ID
				if(object instanceof TeamAssignment) {
					TeamAssignment result = (TeamAssignment) object;
					TeamId = result.id;
					TeamAssignmentReceived = true;
				}
				
				//receives max. number of clients
				if(object instanceof MaxClients) {
					MaxClients result = (MaxClients) object;
					maxClients = result.count;
				}
	
				//receives game start notifier
				if(object instanceof GameStart) {
					gameReady = true;
				}
			}
		});
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose() {
		// Getting rid ob objects, as garbage collector won't do it
		batch.dispose();
		font.dispose();
		mediaManager.dispose();
		client.stop();
	}

	public void reset() {
		client.stop();
		this.connected = false;
		this.clientId = 0;
		this.clientCount = 0;
		this.maxClients = 0;
		this.TeamId = 0;
		this.TeamAssignmentReceived = false;
		this.gameLost = false;
		this.gameReady = false;
		this.gameWon = false;
		
		startClient();
	}
	
}
