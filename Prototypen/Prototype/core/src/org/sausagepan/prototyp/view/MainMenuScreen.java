package org.sausagepan.prototyp.view;

import java.util.Map.Entry;

import box2dLight.RayHandler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.network.HeroInformation;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.network.Network.FullGameStateRequest;
import org.sausagepan.prototyp.network.Network.FullGameStateResponse;
import org.sausagepan.prototyp.network.Network.MapInformation;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import org.sausagepan.prototyp.enums.DAMAGETYPE;
import org.sausagepan.prototyp.enums.WEAPONTYPE;
import org.sausagepan.prototyp.managers.BattleSystem;
import org.sausagepan.prototyp.managers.PlayerManager;
import org.sausagepan.prototyp.model.*;

public class MainMenuScreen implements Screen {
	
	/* ........................................................ ATTRIBUTES .. */
	final KPMIPrototype game;
	public OrthographicCamera camera;
	public Viewport viewport;
	private Texture bgImg;
	private int connectionStatus;
	private PlayerManager cm;
	private final World world;
    private final RayHandler rayHandler;
    private MapInformation mapInformation;
	String serverIp;	
	
	/* ...................................................... CONSTRUCTORS .. */
	public MainMenuScreen(final KPMIPrototype game) {
		this.game  = game;
		this.world = new World(new Vector2(0,0), true);
        this.rayHandler = new RayHandler(world);
		camera = new OrthographicCamera();
		viewport = new FitViewport(800, 480, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		connectionStatus = 0;
		
		this.bgImg = game.mediaManager.getMainMenuBackgroundImg();

		game.client.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				if (object instanceof FullGameStateResponse) {
					FullGameStateResponse response = (FullGameStateResponse) object;
					MainMenuScreen.this.mapInformation = response.mapInformation;
					
					cm = new PlayerManager();
					for(Entry<Integer, HeroInformation> e : response.heroes.entrySet()) {
						HeroInformation hero = e.getValue();
						cm.addCharacter(e.getKey(),
								new Player(
                                        hero.name,
                                        hero.sex,
                                        e.getKey(),
                                        hero.spriteSheet,
                                        hero.status,
                                        hero.weapon,
                                        e.getKey().equals(game.clientId),
                                        game.mediaManager,
                                        world,
                                        rayHandler,
										new Vector2(32*2.5f, 32*.5f)));
					}

					game.connected = true;
				}
			}
			
		});

	}


	/* ................................................................................................... METHODS .. */

	public void setUpGame() {
		BattleSystem bs = new BattleSystem();

		// Player 1
		cm.addCharacter(
				game.clientId,
				new Player("hero" + game.clientId,
						"m",
						game.clientId,
						"knight_m.pack",
						new Status(),
						new Weapon(),
						true,
						game.mediaManager, world, rayHandler,
						new Vector2(32*2.5f, 32*.5f))
		);

		game.client.sendTCP(
			new NewHeroRequest(
				game.clientId,
				new HeroInformation("hero" + game.clientId, "m", "knight_m.pack",
						new Status(),
						new Weapon()
				)
			)
		);
		// Player 2
//		cm.addCharacter(
//				new Player("hero2", "m", "knight_m.pack",
//						new Status(),
//						new Weapon("standard_sword", 3, WEAPONTYPE.SWORD, DAMAGETYPE.PHYSICAL, 20, 180))
//		);

		// Switch to game screen
 	   	System.out.println(mapInformation.height + " " + mapInformation.width);
		game.setScreen(new InMaze(game, bs, cm, world, rayHandler, mapInformation));
	}


	/* ............................................................................................ libGDX METHODS .. */

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
						System.out.println("Established connection to "+text);
						game.client.sendTCP(new FullGameStateRequest());
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
				
			}, "Bitte Server-IP eingeben", "127.0.0.1", "");
		}

		if(game.connected == true && game.clientId != 0) {
			setUpGame();
			dispose();
		}
		
		// Update camera
		camera.update();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
	}

}
