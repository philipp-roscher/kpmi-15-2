package org.sausagepan.prototyp.view;

import java.util.Random;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.network.Network.MapInformation;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class MainMenuScreen implements Screen {
	
	/* ........................................................ ATTRIBUTES .. */
	final KPMIPrototype game;
	public OrthographicCamera camera;
	public Viewport viewport;
	private Texture bgImg;
	private Texture SelArcherF;
	private Texture SelKnightM;
	private Texture SelFighterM;
	private Texture SelShamanM;
	private Texture SelDragonRed;

	private int connectionStatus;
	private final World world;
    private final RayHandler rayHandler;
    private MapInformation mapInformation;
	String serverIp;

	private boolean heroRequestSent = false;
	private boolean mapInformationReceived = false;
	
	//chosen Player Class
	private CharacterClass clientClass;
	private boolean clientSel = false;

	
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
		this.SelArcherF = game.mediaManager.getSelectionArcherFBig();
		this.SelDragonRed = game.mediaManager.getSelectionDragonRedBig();
		this.SelFighterM = game.mediaManager.getSelectionFighterMBig();
		this.SelKnightM = game.mediaManager.getSelectionKnightMBig();
		this.SelShamanM = game.mediaManager.getSelectionShamanMBig();

		game.client.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				if (object instanceof MapInformation) {
					System.out.println("Received MapInformation");
					MainMenuScreen.this.mapInformation = (MapInformation)object;
					mapInformationReceived = true;
				}
			}

		});

	}


	/* ................................................................................................... METHODS .. */

	public void setUpGame() {
//		BattleSystem bs = new BattleSystem();
 	   	System.out.println(mapInformation.height + " " + mapInformation.width);

		System.out.println("Assigned teamId is: "+game.TeamId);

		game.setScreen(new InMaze(game, world, rayHandler, mapInformation, clientClass, game.TeamId));
	}

	//random chose Class for clients according to their TeamId
	public void randomClassSel() {

		game.batch.begin();
		//GameMaster
		if (game.TeamId == 0) {
			game.batch.draw(SelDragonRed, (camera.viewportWidth / 2) - 100, (camera.viewportHeight / 2) - 100, 200, 200);
			clientClass = CharacterClass.DRAGON;
		}
		//Teams
		else {
			Random ran = new Random();
			int x = ran.nextInt(4);                                        //TODO: raise number according to available character sheets!!!!

			//System.out.println("random number = "+x);

			switch (x) {
				case 0: {
					clientClass = CharacterClass.KNIGHT_M;
					game.batch.draw(SelKnightM, (camera.viewportWidth / 2) - 100, (camera.viewportHeight / 2) - 100, 200, 200);
					break;
				}
				case 1: {
					clientClass = CharacterClass.ARCHER_F;
					game.batch.draw(SelArcherF, (camera.viewportWidth / 2) - 100, (camera.viewportHeight / 2) - 100, 200, 200);
					break;
				}
				case 2: {
					clientClass = CharacterClass.SHAMAN_M;
					game.batch.draw(SelShamanM, (camera.viewportWidth / 2) - 100, (camera.viewportHeight / 2) - 100, 200, 200);
					break;
				}
				case 3: {
					clientClass = CharacterClass.FIGHTER_M;
					game.batch.draw(SelFighterM, (camera.viewportWidth / 2) - 100, (camera.viewportHeight / 2) - 100, 200, 200);
					break;
				}
				default: {
					clientClass = CharacterClass.KNIGHT_M;
					game.batch.draw(SelKnightM, (camera.viewportWidth / 2) - 100, (camera.viewportHeight / 2) - 100, 200, 200);
					break;
				}
			}
		}
		game.batch.end();
		System.out.println("Chosen Client Class is: " + clientClass);

		clientSel = true;

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
		if(Gdx.input.justTouched() && connectionStatus != 1) {
			Gdx.input.getTextInput(new TextInputListener() {
				
				@Override
				public void input(String text) {
					// trim IP to remove unnecessary whitepaces (sometimes created by android auto-correct)
					text = text.trim();
					
					Gdx.app.log("ServerConnector", "Attempting Connection to: "+ text);
					try {
						connectionStatus = 1;
						game.client.connect(2000, text, Network.TCPPort, Network.UDPPort);
						System.out.println("Established connection to "+text);

						game.connected = true;
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


			if (game.TeamAssignmentReceived) {

				//class selection
				if (!clientSel) {
					randomClassSel();
				}

				//send Hero/Client info to server after class Selection
				if(!heroRequestSent && clientSel) {
					game.client.sendTCP(
							new NewHeroRequest(
									game.clientId,
									clientClass
							)
					);
					heroRequestSent = true;
				}
			}

			//too few clients
			if(game.clientCount < game.maxClients) {
				game.batch.begin();
				game.font.setColor(1, 0, 0, 1);
				game.font.draw(game.batch, "Waiting for players... " + game.clientCount + "/" + game.maxClients, 320, 380);
				game.font.setColor(1, 1, 1, 1);
				game.batch.end();
			}

			//right amount of players: set up game
			if(game.clientCount == game.maxClients) {
				game.batch.begin();
				game.font.setColor(0, 1, 0, 1);
				game.font.draw(game.batch, "Starting... " + game.clientCount + "/" + game.maxClients, 340, 380);
				game.font.setColor(1, 1, 1, 1);
				game.batch.end();

				//after sending infos was successful: start game
				if (mapInformationReceived) {
					setUpGame();
				}
			}

			//too many players
			if(game.clientCount > game.maxClients) {
				game.batch.begin();
				game.font.setColor(1, 0, 0, 1);
				game.font.draw(game.batch, "Sorry, server is already full!"+game.clientCount+"/"+game.maxClients, 320, 380);
				game.font.setColor(1, 1, 1, 1);
				game.batch.end();
			}
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
