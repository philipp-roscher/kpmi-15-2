package org.sausagepan.prototyp.view;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.Network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {
	
	/* ........................................................ ATTRIBUTES .. */
	final KPMIPrototype game;
	public OrthographicCamera camera;
	public Viewport viewport;
	private Texture bgImg;
	private int connectionStatus;
	
	/* ...................................................... CONSTRUCTORS .. */
	public MainMenuScreen(final KPMIPrototype game) {
		this.game  = game;
		camera     = new OrthographicCamera();
		viewport        = new FitViewport(800, 480, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2,0);
		connectionStatus = 0;
		
		this.bgImg = new Texture("textures/backgrounds/main_menu_bg.png");
		
//		connectButtonStyle = new TextButtonStyle();
//		connectButtonStyle.font = game.font;
//		connectButton = new TextButton("Connect", connectButtonStyle);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.setProjectionMatrix(camera.combined);
		
		// Start collecting textures for OpenGL
		game.batch.begin();
			game.batch.draw(bgImg, 0, 0);
			game.font.draw(game.batch, "Tap to start", 365, 100);

			if(connectionStatus == -1) {
			    game.font.setColor(1, 0, 0, 1);
				game.font.draw(game.batch, "Connection failed", 343, 350);
			    game.font.setColor(1, 1, 1, 1);
			}
			
			if(connectionStatus == 1) {
			    game.font.setColor(0, 1, 0, 1);
				game.font.draw(game.batch, "Connecting", 360, 350);
			    game.font.setColor(1, 1, 1, 1);
			}
		game.batch.end();
		
		// If screen is touched
		if(Gdx.input.justTouched()) {
			Gdx.input.getTextInput(new TextInputListener() {
					
					@Override
					public void input(String text) {
						// TODO Auto-generated method stub
						Gdx.app.log("ServerConnector", "Attempting Connection to: "+ text);
						try {
							connectionStatus = 1;
							game.client.connect(2000, text, Network.TCPPort, Network.UDPPort);
							game.connected = true;
							System.out.println("Established connection to "+text);
						} catch (Exception e) {
							System.out.println("Couldn't find running server at "+text);
							e.printStackTrace();
							connectionStatus = -1;
						}
					}
	
					@Override
					public void canceled() {
						// TODO Auto-generated method stub
					}
					
				}, "Bitte Server-IP eingeben", "192.168.0.105", "");
			
		}
		
		if(game.connected == true && game.clientId != 0) {
			game.setScreen(new InMaze(game));
			dispose();
		}

		// Update camera
		camera.update();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
		camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		bgImg.dispose();
	}

}
