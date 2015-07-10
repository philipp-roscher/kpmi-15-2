package org.sausagepan.prototyp.view;

import java.util.Map.Entry;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.input.PlayerInputAdapter;
import org.sausagepan.prototyp.managers.BattleSystem;
import org.sausagepan.prototyp.managers.PlayerManager;
import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.network.Network.DeleteHeroResponse;
import org.sausagepan.prototyp.network.Network.GameStateResponse;
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

    // Camera, Viewport and Renderers
    public  OrthographicCamera camera;
	private Viewport           viewport;
	private SpriteBatch        batch;
	private ShapeRenderer      shpRend;
	private BitmapFont         font;

	// Geometry
	private Vector3          touchPos;      // touch position
	public  Array<Rectangle> colliderWalls; // rectangles for collision detection with map

    // Managers
	public  PlayerManager playerMan;        // manages players
	public  BattleSystem  battleSys;        // manages battle

	// Media
	private Music bgMusic;
	private float elapsedTime = 0;
	private float disconnectTime = 0;
	private float timeOut = 5;
	private Player selfPlayer;
	
	//Tiled Map for map creation and collision detection
	private TiledMap                              tiledMap;         // contains the layers of the tiled map
	private OrthogonalTiledMapRendererWithSprites tiledMapRenderer; // renders the tiled map, players and items
	
	/* .............................................................................................. CONSTRUCTORS .. */

    /**
     * Creates an ingame object for rendering ingame action
     * @param game              the game main class itself
     * @param battleSystem
     * @param playerManager
     */
	public InMaze(final KPMIPrototype game, BattleSystem battleSystem, PlayerManager playerManager) {
		this.game = game;

        // set up the camera and viewport
		camera   = new OrthographicCamera();
		viewport = new FitViewport(800, 480, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);

        // create batches and renderers
		batch   = new SpriteBatch();
		shpRend = new ShapeRenderer();
		font    = new BitmapFont();
		font.setColor(Color.WHITE);

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
		
		// Build tiled map
		tiledMap         = new TmxMapLoader().load("tilemaps/maze.tmx");            // load tiled map from file
		tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);

		for(Player p : playerMan.getPlayers())
			tiledMapRenderer.addSprite(p.getSprite());

		// Get collider tiles as squares
		this.colliderWalls = new Array<Rectangle>();
		for(MapObject mo : tiledMap.getLayers().get("colliderWalls").getObjects())

		colliderWalls.add(((RectangleMapObject) mo).getRectangle());            // get colliders from tiled map

        Gdx.input.setInputProcessor(new PlayerInputAdapter(playerMan.getPlayers().get(0), this));

		
		// Set Up Client for Communication
		game.client.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				
				//System.out.println("Paket empfangen");
				//System.out.println(object.getClass());
				
				if (object instanceof NewHeroResponse) {
					NewHeroResponse request = (NewHeroResponse) object;
	        		HeroInformation hero = request.hero;
					playerMan.addCharacter(request.playerId, new Player(hero.name, hero.sex, hero.spriteSheet, hero.status, hero.weapon, game.mediaManager));
					tiledMapRenderer.addSprite(playerMan.players.get(request.playerId).getSprite());
					System.out.println("tiledmaprenderer neues objekt hinzugefügt");
				}
				
				if (object instanceof DeleteHeroResponse) {
					int playerId = ((DeleteHeroResponse) object).playerId;
					System.out.println(playerId + " was inactive for too long and thus removed from the session.");
					//tiledMapRenderer.
					//playerMan.removeCharacter(playerId);
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
	}

	@Override
	public void render(float delta) {
		if(!game.connected) {
			if( disconnectTime == 0 )
				disconnectTime = elapsedTime;
			else
				if(elapsedTime - disconnectTime > timeOut) {
					disconnectTime = 0;
		            game.setScreen(new MainMenuScreen(game));
		            dispose();
				}
			
		}
		
        // Clear canvas
		Gdx.gl.glClearColor(0,0,0,1);   // set background color to black
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // project to camera
		batch.  setProjectionMatrix(camera.combined);
		shpRend.setProjectionMatrix(camera.combined);
		
		// Animation time calculation
		elapsedTime += Gdx.graphics.getDeltaTime(); // add time between frames

		// render tiled map
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		// Move character
        for(Player p : playerMan.getPlayers()) p.update(colliderWalls, touchPos, elapsedTime);
//		handleInput();


		// render status
		for(Player c : playerMan.getPlayers())
			c.drawCharacterStatus(shpRend);

        // render bullets
		battleSys.updateBullets(selfPlayer, playerMan.getPlayers());


        // debug(shpRend);
		for(Player p : playerMan.getPlayers()) p.debug(shpRend);

		camera.position.set(selfPlayer.getPosition());
		camera.update();
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

    /**
     * Debugging method which draws tiled map colliders as rectangles to the screen
     * @param shpRend   {@link ShapeRenderer} tp draw to
     */
	public void handleInput() {
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
//			selfPlayer.handleTouchInput(touchPos, colliderWalls, elapsedTime);
			
			PositionUpdate posUpdate = new PositionUpdate();
			posUpdate.playerId = game.clientId;
			posUpdate.position = new Position(selfPlayer.getPosition(), selfPlayer.getDirection(), selfPlayer.isMoving());
//			System.out.println("Position: "+ playerMan.getPlayers().get(0).getPosition());
//			System.out.println("Direction: "+ playerMan.getPlayers().get(0).getDirection());
			game.client.sendUDP(posUpdate);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			System.out.println("Attack!");
            selfPlayer.attack();
			battleSys.updateAttack(selfPlayer, playerMan.getPlayers());
		}

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            System.out.println("Shoot!");
            selfPlayer.shoot();
        }
	}

    public void debug(ShapeRenderer shpRend) {
        shpRend.begin(ShapeRenderer.ShapeType.Line);
        shpRend.setColor(Color.RED);
        for(Rectangle r : colliderWalls)
            shpRend.rect(r.x, r.y, r.width, r.height);
        shpRend.end();
    }

}
