package org.sausagepan.prototyp.view;

import java.util.Map.Entry;

import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.input.PlayerInputAdapter;
import org.sausagepan.prototyp.managers.BattleSystem;
import org.sausagepan.prototyp.managers.PlayerManager;
import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
import org.sausagepan.prototyp.network.Network.KeepAliveRequest;
import org.sausagepan.prototyp.network.Network.NewHeroResponse;
import org.sausagepan.prototyp.network.Network.PositionUpdate;
import org.sausagepan.prototyp.network.HeroInformation;
import org.sausagepan.prototyp.network.Position;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Screen for all ingame action. Here everything is rendered to the screen
 */
public class InMaze implements Screen {
	
	/* ................................................................................................ ATTRIBUTES .. */
	final   KPMIPrototype game;

    // Renderers and Cameras
    private Box2DDebugRenderer debugRenderer;   // a debugging renderer
    public  OrthographicCamera camera;
    private Viewport           viewport;
	private SpriteBatch        batch;
	private ShapeRenderer      shpRend;
	private BitmapFont         font;

	// Geometry
	private Vector3          touchPos;      // touch position

    // Managers
	public  PlayerManager playerMan;        // manages players
	public  BattleSystem  battleSys;        // manages battle

	// Media
	private Music bgMusic;
	private float elapsedTime    = 0;
	private int elapsedTimeSec = 0;
	private float disconnectTime = 0;
	private float timeOut        = 5;

    // Containers
	private Player selfPlayer;
	private PositionUpdate posUpdate;
	private KeepAliveRequest keepAliveRequest;
	
	//Tiled Map for map creation and collision detection
	private TiledMap                              tiledMap;         // contains the layers of the tiled map
	private OrthogonalTiledMapRendererWithSprites tiledMapRenderer; // renders the tiled map, players and items

    // Physics
    private final World world;    // create a box2d world which calculates all physics

    // Light
    RayHandler rayHandler;  // handles rays of light
	
	/* .............................................................................................. CONSTRUCTORS .. */

    /**
     * Creates an ingame object for rendering ingame action
     * @param game              the game main class itself
     * @param battleSystem
     * @param playerManager
     */
	public InMaze(final KPMIPrototype game,
                  BattleSystem battleSystem,
                  PlayerManager playerManager,
                  final World world,
                  final RayHandler rayHandler) {

        Box2D.init();   // initialize Box2D

		this.game = game;

        // set up the camera and viewport
		camera   = new OrthographicCamera();
		viewport = new FitViewport(UnitConverter.pixelsToMeters(800), UnitConverter.pixelsToMeters(480), camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0); // center camera

        // create batches and renderers
		batch   = new SpriteBatch();
		shpRend = new ShapeRenderer();
		font    = new BitmapFont();
		font.setColor(Color.WHITE);


        // Set up World and Box2D-Renderer .............................................................................
        this.world         = world;                     // create new world with no gravity in x and y
        this.debugRenderer = new Box2DDebugRenderer();  // set up Box2D-Debugger for drawing body shapes


        // Light .......................................................................................................
        RayHandler.useDiffuseLight(true);
        this.rayHandler = rayHandler;
        this.rayHandler.setAmbientLight(.3f, .3f, .3f, 0.5f);
        this.rayHandler.setBlurNum(3);

        // create some geometry containers
		touchPos = new Vector3();

        // load media
		this.bgMusic = game.mediaManager.getMazeBackgroundMusic();
		this.bgMusic.setLooping(true);  // always repeat background music
		this.bgMusic.play();
		this.bgMusic.setVolume(0.3f);

        // set up managers
		this.battleSys = battleSystem;
		this.playerMan = playerManager;
		
		// register own player
		this.selfPlayer = playerMan.players.get(game.clientId);

        // Tiled Map ...................................................................................................
		setUpTiledMap();
		for(Player p : playerMan.getPlayers())
			tiledMapRenderer.addSprite(p.getSprite());


		// Set Up Client for Communication .............................................................................
		posUpdate = new PositionUpdate();
		posUpdate.playerId = game.clientId;

		this.keepAliveRequest = new KeepAliveRequest(game.clientId);

		game.client.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				
				//System.out.println("Paket empfangen");
				//System.out.println(object.getClass());
				
				if (object instanceof NewHeroResponse) {
					NewHeroResponse request = (NewHeroResponse) object;
	        		HeroInformation hero = request.hero;
					playerMan.addCharacter(
							request.playerId,

							new Player(
                                    hero.name,
                                    hero.sex,
                                    hero.spriteSheet,
                                    hero.status,
                                    hero.weapon,
                                    false,
                                    game.mediaManager,
                                    world,
                                    rayHandler));

					tiledMapRenderer.addSprite(playerMan.players.get(request.playerId).getSprite());
				}
				
				if (object instanceof DeleteHeroResponse) {
					int playerId = ((DeleteHeroResponse) object).playerId;
					System.out.println(playerId + " was inactive for too long and thus removed from the session.");
					
					if( playerId == game.clientId )
						game.connected = false;
						
						world.destroyBody(playerMan.players.get(playerId).getBody());
						tiledMapRenderer.removeSprite(playerMan.players.get(playerId).getSprite());
						playerMan.removeCharacter(playerId);
				}
				
				if (object instanceof GameStateResponse) {
					// System.out.println("GameStateResponse empfangen");
					GameStateResponse result = (GameStateResponse) object;
					
					for(Entry<Integer, Position> e : result.positions.entrySet()) {
						if(e.getKey() != game.clientId)
							playerMan.updatePosition(e.getKey(), e.getValue(), elapsedTime);
					}
				}	
			}

			public void disconnected (Connection connection) {
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
        Gdx.input.setInputProcessor(new PlayerInputAdapter(selfPlayer, this));
	}

	@Override
	public void render(float delta) {

        // Check Server Connection ............................................................................. NETWORK
		if(!game.connected) {
			if( disconnectTime == 0 ) disconnectTime = elapsedTime;
			else
				if(elapsedTime - disconnectTime > timeOut) {
					disconnectTime = 0;
		            game.setScreen(new MainMenuScreen(game));
		            dispose();
				}
		}
        // ..................................................................................................... NETWORK


        // Clear screen
        Gdx.gl.glClearColor(.2f, .2f, .2f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // project to camera
		camera.position.set(selfPlayer.getPosition().x, selfPlayer.getPosition().y, 0);
        camera.update();
		batch.  setProjectionMatrix(camera.combined);
		shpRend.setProjectionMatrix(camera.combined);
		
		// Animation time calculation
		elapsedTime += Gdx.graphics.getDeltaTime(); // add time between frames
		if(elapsedTimeSec != (int) elapsedTime) {
			sendKeepAliveRequest();
		}
		elapsedTimeSec = (int) elapsedTime;

        // Update Player
        selfPlayer.update(elapsedTime);
        selfPlayer.updateNetworkPosition();
        posUpdate.position = selfPlayer.position;
        game.client.sendUDP(posUpdate);

		// Move character
        for(Player p : playerMan.getPlayers()) p.update(elapsedTime);
//		handleInput();


        // ................................................................................................... RENDERING
        // Tiled Map
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        // Box2D Debugging
        debugRenderer.render(world, camera.combined);   // render Box2D-Shapes

        // Light
        rayHandler.setCombinedMatrix(camera.combined);
        rayHandler.updateAndRender();

		// Status
		for(Player c : playerMan.getPlayers()) {
			c.drawCharacterStatus(shpRend);
			c.debugRenderer(shpRend);
		}
        // ................................................................................................... RENDERING


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

//	public void handleInput() {
//		if (Gdx.input.isTouched()) {
//			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
//			camera.unproject(touchPos);
//			playerMan.getPlayers().get(0).handleTouchInput(touchPos, colliderWalls, elapsedTime);
//		}
//
//		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//			System.out.println("Attack!");
//            playerMan.getPlayers().get(0).attack();
//			battleSys.updateAttack(playerMan.getPlayers().get(0), playerMan.getPlayers());
//		}
//
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            System.out.println("Shoot!");
//            playerMan.getPlayers().get(0).shoot();
//        }
//	}

//    /**
//     * Debugging method which draws tiled map colliders as rectangles to the screen
//     */
//	public void handleInput() {
//		if (Gdx.input.isTouched()) {
//			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
//			camera.unproject(touchPos);
////			selfPlayer.handleTouchInput(touchPos, colliderWalls, elapsedTime);
//
//			PositionUpdate posUpdate = new PositionUpdate();
//			posUpdate.playerId = game.clientId;
//			posUpdate.position = new Position(selfPlayer.getPosition(), selfPlayer.getDirection(), selfPlayer.isMoving());
////			System.out.println("Position: "+ playerMan.getPlayers().get(0).getPosition());
////			System.out.println("Direction: "+ playerMan.getPlayers().get(0).getDirection());
//			game.client.sendUDP(posUpdate);
//		}
//
//		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//			System.out.println("Attack!");
//            selfPlayer.attack();
//			battleSys.updateAttack(selfPlayer, playerMan.getPlayers());
//		}
//
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            System.out.println("Shoot!");
//            selfPlayer.shoot();
//        }
//	}


    /**
     * Sets up the {@link TiledMap} and {@link OrthogonalTiledMapRendererWithSprites} for the game
     */
    public void setUpTiledMap() {
        tiledMap         = new TmxMapLoader().load("tilemaps/maze.tmx");            // load tiled map from file
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap,32);   // set up map renderer and scale

        // create static bodys from colliders
        Rectangle r;
        for(MapObject mo : tiledMap.getLayers().get("colliderWalls").getObjects()) {
            r = ((RectangleMapObject) mo).getRectangle();

            BodyDef groundBodyDef  = new BodyDef();
            groundBodyDef.type     = BodyDef.BodyType.StaticBody;
            groundBodyDef.position.set(new Vector2(r.x/32f+r.width/64f, r.y/32f + r.height/64f));
            Body groundBody        = world.createBody(groundBodyDef);
            PolygonShape groundBox = new PolygonShape();
            groundBox.setAsBox(r.width/64f, r.height/64f);
            groundBody.createFixture(groundBox, 0.0f);
            groundBox.dispose();
        }
    }

	public void sendKeepAliveRequest() {
		game.client.sendTCP(InMaze.this.keepAliveRequest);		
	}

}
