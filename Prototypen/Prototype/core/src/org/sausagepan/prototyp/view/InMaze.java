package org.sausagepan.prototyp.view;

import java.util.HashMap;
import java.util.Map.Entry;

import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.enums.PlayerAction;
import org.sausagepan.prototyp.managers.BattleSystem;
import org.sausagepan.prototyp.managers.EntityComponentSystem;
import org.sausagepan.prototyp.managers.PlayerManager;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.model.PlayerObserver;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.PositionComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.HPUpdate;
import org.sausagepan.prototyp.network.Network.KeepAliveRequest;
import org.sausagepan.prototyp.network.Network.MapInformation;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.HeroInformation;
import org.sausagepan.prototyp.network.NetworkPosition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Screen for all ingame action. Here everything is rendered to the screen
 */
public class InMaze implements Screen, PlayerObserver {
	
	/* ................................................................................................ ATTRIBUTES .. */
	final   KPMIPrototype game;

    // Renderers and Cameras
    private Box2DDebugRenderer debugRenderer;   // a debugging renderer
    public  OrthographicCamera camera;
    private Viewport           viewport;
	private SpriteBatch        batch;
	private ShapeRenderer      shpRend;
	private BitmapFont         font;

    // Managers
	public EntityComponentSystem ECS;		// entity component system

	// Media
	private Music   bgMusic;
	private float   elapsedTime    = 0;
	private int     elapsedTimeSec = 0;
	private float   disconnectTime = 0;
	private float   timeOut        = 5;

    // Containers
	private PositionUpdate posUpdate;
	private KeepAliveRequest keepAliveRequest;
	private Array<Object> networkMessages;

    private Maze maze;

    // Physics
    private final World world;    // create a box2d world which calculates all physics

    // Light
    RayHandler rayHandler;  // handles rays of light
	
	/* .......................................................................... CONSTRUCTORS .. */

    /**
     * Creates an ingame object for rendering ingame action
     * @param game              the game main class itself
     */
	public InMaze(final KPMIPrototype game,
                  final World world,
                  final RayHandler rayHandler,
                  final MapInformation mapInformation,
                  final HashMap<Integer,HeroInformation> otherCharacters,
				  String clientClass,
				  int TeamId) {

        Box2D.init();   // initialize Box2D

		this.game = game;

        // Rendering ...............................................................................
		camera   = new OrthographicCamera();    // set up the camera and viewport
		int zoom = 1;                           // zooms out of map
		viewport = new FitViewport(
                UnitConverter.pixelsToMeters(800*zoom),
                UnitConverter.pixelsToMeters(480*zoom), camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0); // center camera

		batch   = new SpriteBatch();
		shpRend = new ShapeRenderer();
		font    = new BitmapFont();
		font.setColor(Color.WHITE);


        // Set up World and Box2D-Renderer .........................................................
        this.world         = world;                     // create new world with no gravity in x and y
        this.debugRenderer = new Box2DDebugRenderer();  // set up Box2D-Debugger for drawing body shapes


        // Light ...................................................................................
        RayHandler.useDiffuseLight(true);
        this.rayHandler = rayHandler;
        this.rayHandler.setAmbientLight(.2f, .2f, .2f, 1);
        this.rayHandler.setBlurNum(3);

        // load media
		this.bgMusic = game.mediaManager.getMazeBackgroundMusic();
		this.bgMusic.setLooping(true);  // always repeat background music
		this.bgMusic.play();
		this.bgMusic.setVolume(0.3f);

        // Tiled Map ...............................................................................
        this.maze = new Maze(mapInformation, world, game.mediaManager);


        // Entity-Component-System ........................................................... START
        this.ECS = new EntityComponentSystem(game, world, viewport, rayHandler, maze, camera, clientClass, TeamId);
        // Entity-Component-System ............................................................. END

		for(Entry<Integer, HeroInformation> e : otherCharacters.entrySet()) {
			Integer heroId = e.getKey();
			HeroInformation hero = e.getValue();
			CharacterEntity newCharacter = ECS.addNewCharacter(heroId, hero);
    		maze.addCharacterSpriteComponent(newCharacter.getComponent(CharacterSpriteComponent.class));
		}
        
		// Set Up Client for Communication .........................................................
		posUpdate = new PositionUpdate();
		posUpdate.playerId = game.clientId;
		networkMessages = new Array<Object>();

		this.keepAliveRequest = new KeepAliveRequest(game.clientId);

		game.client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if ((object instanceof NewHeroResponse) ||
						(object instanceof DeleteHeroResponse) ||
						(object instanceof DeleteHeroResponse) ||
						(object instanceof GameStateResponse) ||
						(object instanceof AttackResponse) ||
						(object instanceof HPUpdate)) {
					// System.out.println( object.getClass() +" empfangen");
					networkMessages.add(object);
				}
			}

			public void disconnected(Connection connection) {
				game.connected = false;
				Gdx.app.log("KPMIPrototype", "disconnected from server.");
//                Gdx.app.exit();
			}
		});
	}

	
	/* ............................................................................................ LibGDX METHODS .. */
	@Override
	public void show() {
		this.batch = new SpriteBatch();
//        Gdx.input.setInputProcessor(new PlayerInputProcessor(localPlayer, this.camera));
//        Gdx.input.setInputProcessor(ECS.getLocalCharacterEntity().getComponent(InputComponent.class));
        Gdx.input.setInputProcessor(ECS.getInputProcessor());
	}

	@Override
	public void render(float delta) {

        // Check Server Connection ......................................................... NETWORK
		if(!game.connected) {
			if( disconnectTime == 0 ) disconnectTime = elapsedTime;
			else
				if(elapsedTime - disconnectTime > timeOut) {
					disconnectTime = 0;
		            game.setScreen(new MainMenuScreen(game));
		            dispose();
				}
		}
        // ................................................................................. NETWORK

        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // process Updates
        processNetworkMessages();
        
        // project to camera
        camera.position.set(
                ECS.getLocalCharacterEntity()
                        .getComponent(DynamicBodyComponent.class).dynamicBody.getPosition().x,
                ECS.getLocalCharacterEntity()
                        .getComponent(DynamicBodyComponent.class).dynamicBody.getPosition().y,
                0
        );
        camera.update();
		batch.  setProjectionMatrix(camera.combined);
		shpRend.setProjectionMatrix(camera.combined);
		
		// Animation time calculation
		elapsedTime += Gdx.graphics.getDeltaTime(); // add time between frames
		if(elapsedTimeSec != (int) elapsedTime)
			sendKeepAliveRequest();
		elapsedTimeSec = (int) elapsedTime;

        // Update Player
        DynamicBodyComponent temp = ECS.getLocalCharacterEntity().getComponent(DynamicBodyComponent.class);
        NetworkTransmissionComponent ntc = new NetworkTransmissionComponent();
        ntc.position = temp.dynamicBody.getPosition();
        posUpdate.position = ntc;
        game.client.sendUDP(posUpdate);

        // ............................................................................... RENDERING
        // Tiled Map
        maze.render(camera);

        // Box2D Debugging
//        debugRenderer.render(world, camera.combined);   // render Box2D-Shapes

        // Light
        rayHandler.setCombinedMatrix(camera.combined);
        rayHandler.updateAndRender();

        // ............................................................................... RENDERING
        ECS.update(delta);
        world.step(1 / 45f, 6, 2);    // time step at which world is updated
	}


	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
//		camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
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
		this.batch.dispose();
		this.font.dispose();
		this.bgMusic.dispose();
	}
	
	
	/* ........................................................... METHODS .. */

	public void sendKeepAliveRequest() {
		game.client.sendTCP(InMaze.this.keepAliveRequest);		
	}

	public void processNetworkMessages() {
		for(Object object : networkMessages) {
			if (object instanceof NewHeroResponse) {
				NewHeroResponse request = (NewHeroResponse) object;
        		CharacterEntity newCharacter = ECS.addNewCharacter(request);
        		maze.addCharacterSpriteComponent(newCharacter.getComponent(CharacterSpriteComponent.class));
        		
				/*playerMan.addCharacter(
						request.playerId,

						new Player(
                                hero.name,
                                hero.sex,
        						request.playerId,
                                hero.spriteSheet,
                                hero.status,
                                hero.weapon,
                                false,
                                game.mediaManager,
                                world,
                                rayHandler,
                                new Vector2(32*2.5f, 32*.5f)));

				maze.addSpriteComponent(); */
			}
			
			if (object instanceof DeleteHeroResponse) {
				int playerId = ((DeleteHeroResponse) object).playerId;
				System.out.println(playerId + " was inactive for too long and thus removed from the session.");
				
				if( playerId == game.clientId )
					game.connected = false;
					
				//	ECS.deleteCharacter(playerId);
			}
			
			if (object instanceof GameStateResponse) {
				GameStateResponse result = (GameStateResponse) object;
				
				for(Entry<Integer, NetworkTransmissionComponent> e : result.positions.entrySet()) {
					if(e.getKey() != game.clientId)
						ECS.updatePosition(e.getKey(), e.getValue());
				}
			}	
			
			if (object instanceof AttackResponse) {
				AttackResponse result = (AttackResponse) object;
//				if(result.stop == false)
//					playerMan.players.get(result.playerId).getBattle().attack();
//				else 
//					playerMan.players.get(result.playerId).getBattle().stopAttacking();
			}	
			
			if (object instanceof HPUpdate) {
				HPUpdate result = (HPUpdate) object;
				
//				playerMan.players.get(result.playerId).getStatus_().setHP(result.HP);
			}	
		}
		networkMessages.clear();
	}


	/**
	 * Observes player instance for submitting stuff to the server
	 * @param observedPlayer
	 */
	@Override
	public void update(Player observedPlayer, PlayerAction action) {
        switch(action) {
            case ATTACK:
                game.client.sendUDP(new AttackRequest(game.clientId, false));
                break;
            case ATTACK_STOP:
                game.client.sendUDP(new AttackRequest(game.clientId, true));
                break;
            default: break;
        }
	}
}
