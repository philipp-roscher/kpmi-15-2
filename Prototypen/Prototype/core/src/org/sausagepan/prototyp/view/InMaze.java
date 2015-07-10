package org.sausagepan.prototyp.view;

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
	private Music   bgMusic;

	// Time
	public float elapsedTime = 0;          // used for animation calculation, and time dependent movements

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

        // set up managers
		this.battleSys = battleSystem;
		this.playerMan = playerManager;

		// Build tiled map
		tiledMap         = new TmxMapLoader().load("tilemaps/maze.tmx");            // load tiled map from file
		tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);
		tiledMapRenderer.addSprite(playerManager.getPlayers().get(0).getSprite());  // add player sprite to map renderer
        tiledMapRenderer.addSprite(playerManager.getPlayers().get(1).getSprite());  // add player sprite to map renderer

		// Get collider tiles as squares
		this.colliderWalls = new Array<Rectangle>();
		for(MapObject mo : tiledMap.getLayers().get("colliderWalls").getObjects())
			colliderWalls.add(((RectangleMapObject) mo).getRectangle());            // get colliders from tiled map

        Gdx.input.setInputProcessor(new PlayerInputAdapter(playerMan.getPlayers().get(0), this));
	}

	
	/* ............................................................................................ LibGDX METHODS .. */
	@Override
	public void show() {
		this.batch = new SpriteBatch();
	}

	@Override
	public void render(float delta) {
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
		battleSys.updateBullets(playerMan.getPlayers().get(0), playerMan.players);

        // debug(shpRend);
		for(Player p : playerMan.getPlayers()) p.debug(shpRend);

		camera.position.set(playerMan.getPlayers().get(0).getPosition());   // move camera with character
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
    public void debug(ShapeRenderer shpRend) {
        shpRend.begin(ShapeRenderer.ShapeType.Line);
        shpRend.setColor(Color.RED);
        for(Rectangle r : colliderWalls)
            shpRend.rect(r.x, r.y, r.width, r.height);
        shpRend.end();
    }

}
