package org.sausagepan.prototyp;

import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.network.Network.IDAssignment;
import org.sausagepan.prototyp.network.Network.GameClientCount;
import org.sausagepan.prototyp.network.Network.TeamAssignment;
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

	//Numer of Players needed to start game:
	public int maxClients = 3;
	//counts players on server
	public int clientCount;

	public int TeamId;
	/* .................................................... LibGDX METHODS .. */
	@Override
	public void create () {
		batch = new SpriteBatch();
		font  = new BitmapFont();
		mediaManager = new MediaManager();

		// Client starten
		client = new Client();
		new Thread(client).start();
		Network.register(client);
		client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (object instanceof IDAssignment) {
					IDAssignment result = (IDAssignment) object;
					clientId = result.id;
				}
			}
		});
		client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (object instanceof GameClientCount) {
					GameClientCount result = (GameClientCount) object;
					clientCount = result.count;
				}
			}
		});
		client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if(object instanceof TeamAssignment) {
					TeamAssignment result = (TeamAssignment) object;
					TeamId = result.id;
				}
			}
		});



		// switch to main menu screen
		this.setScreen(new MainMenuScreen(this));
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

}
