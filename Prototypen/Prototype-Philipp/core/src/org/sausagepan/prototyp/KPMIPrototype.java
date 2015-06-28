package org.sausagepan.prototyp;

import org.sausagepan.prototyp.Network.IDAssignment;
import org.sausagepan.prototyp.view.MainMenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class KPMIPrototype extends Game {
	
	/* ........................................................ ATTRIBUTES .. */
	public SpriteBatch batch;
	public BitmapFont font;
	public Client client;
	public boolean connected = false;
	public int clientId;
	
	
	/* .................................................... LibGDX METHODS .. */
	@Override
	public void create () {
		System.setProperty("java.net.preferIPv4Stack" , "true");
		batch = new SpriteBatch();
		font  = new BitmapFont();
		client = new Client();
		new Thread(client).start();
		Network.register(client);
		client.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				if (object instanceof IDAssignment) {
					IDAssignment result = (IDAssignment)object;
					clientId = result.id;
				}
			}});
		
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
	}
}
