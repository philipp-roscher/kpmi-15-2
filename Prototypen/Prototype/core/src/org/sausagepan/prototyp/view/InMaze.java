package org.sausagepan.prototyp.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.managers.EntityComponentSystem;
import org.sausagepan.prototyp.managers.InputSystem;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.network.Network.MapInformation;

import box2dLight.RayHandler;

/**
 * Screen for all ingame action. Here everything is rendered to the screen
 */
public class InMaze implements Screen {
	
	/* ............................................................................ ATTRIBUTES .. */
	private KPMIPrototype game;

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
	private float   disconnectTime = 0;
	private float   timeOut        = 5;
    private long    quittingTime   = 0;
    private boolean gameOver = false;

    private Maze maze;

    // Physics
    private World world;    	// create a box2d world which calculates all physics
    public RayHandler rayHandler;   // handles rays of light

	/* .......................................................................... CONSTRUCTORS .. */
    /**
     * Creates an in game object for rendering in game action
     * @param game the game main class itself
     */
	public InMaze(KPMIPrototype game, World world, RayHandler rayHandler,
                  MapInformation mapInformation, CharacterClass clientClass, int TeamId) {

        Box2D.init(); // initialize Box2D
		this.game = game;
        this.world = world;
        setUpRendering();
        setUpBox2D(rayHandler);


        // Media
		this.bgMusic = game.mediaManager.getMazeBackgroundMusic();
		if(GlobalSettings.PLAY_BG_MUSIC) this.bgMusic.play();
		this.bgMusic.setVolume(0.3f);


        // Tiled Map ...............................................................................
        this.maze = new Maze(mapInformation, world, game.mediaManager, game.gameReady);


        // Entity-Component-System ........................................................... START
        this.ECS = new EntityComponentSystem(
                game, world, viewport, rayHandler, maze, camera, clientClass, TeamId, this);
        // Entity-Component-System ............................................................. END

        setUpNetwork();

	}

	
	/* ........................................................................ LibGDX METHODS .. */
	@Override
	public void show() {
		this.batch = new SpriteBatch();
        ECS.setInputProcessor();
	}

	@Override
	public void render(float delta) {
        // Check Server Connection ......................................................... NETWORK
        checkServerConnection();
        // ................................................................................. NETWORK

        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // process Updates
        updateCamera();

        // ............................................................................... RENDERING
        // Tiled Map
        maze.render(camera);

        // Box2D Debugging
        if(GlobalSettings.DEBUGGING_ACTIVE) debugRenderer.render(world, camera.combined);

        // Light
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        // Stuff which should not be effected by RayHandler must be drawn after rayHandler.upd...
        ECS.draw();
        // ............................................................................... RENDERING

        ECS.update(delta);
        world.step(1 / 45f, 6, 2);    // time step at which world is updated

        //wenn X is pressed teleport player in treasure room
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            ECS.getLocalCharacterEntity().getComponent(DynamicBodyComponent.class).dynamicBody.setTransform(115f, 115f, 1);

        }

        // Exit Game
        if(gameOver && TimeUtils.timeSinceMillis(quittingTime)>3000) backToMainMenu();
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
	
	
	/* ............................................................................... METHODS .. */


    public void quitGame(Network.GameExitResponse result) {
        int teamIdWinner = result.id;
        int teamIdHere = ECS.getCharacter(game.clientId).getComponent(TeamComponent.class).TeamId;
        quittingTime = TimeUtils.millis();
        gameOver = true;
        //winning Team
        if (teamIdHere == teamIdWinner) game.gameWon = true;
        //other teams
        else game.gameLost = true;
    }

    private void backToMainMenu() {
        game.setScreen(new MainMenuScreen(game));
        dispose();
    }

    private void setUpRendering() {
        // Rendering ...............................................................................
        camera   = new OrthographicCamera();    // set up the camera and viewport
        viewport = new FitViewport(
                UnitConverter.pixelsToMeters(800*GlobalSettings.GAME_ZOOM_OUT),
                UnitConverter.pixelsToMeters(480*GlobalSettings.GAME_ZOOM_OUT), camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0); // center camera

        batch   = new SpriteBatch();
        shpRend = new ShapeRenderer();
        font    = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    private void setUpBox2D(RayHandler rayHandler) {
        // Set up World and Box2D-Renderer .........................................................
        this.debugRenderer = new Box2DDebugRenderer();  // set up Box2D-Debugger, for drawing bodies


        // Light ...................................................................................
        RayHandler.useDiffuseLight(true);
        this.rayHandler = rayHandler;
        this.rayHandler.setAmbientLight(.3f, .3f, .3f, 1);
        this.rayHandler.setBlurNum(3);
    }

    private void setUpNetwork() {
        // Set Up Client for Communication .........................................................
        // add client to NetworkComponent
        ECS.getLocalCharacterEntity().getComponent(NetworkComponent.class).client = game.client;
        ECS.getLocalCharacterEntity().getComponent(NetworkComponent.class).id = game.clientId;
        
        game.client.addListener(new Listener() {
            public void disconnected(Connection connection) {
                game.connected = false;
                Gdx.app.log("KPMIPrototype", "disconnected from server.");
//                Gdx.app.exit();
            }
        });
        
        ECS.setupNetworkSystem();
    }

    /* ..................................................................... GAME LOOP METHODS .. */

    private void updateCamera() {
        // project to camera
    	// don't move camera if player is dead
    	if(ECS.getLocalCharacterEntity().getComponent(IsDeadComponent.class) == null)
        camera.position.set(ECS.getLocalCharacterEntity().getComponent(DynamicBodyComponent.class)
                .dynamicBody.getPosition().x, ECS.getLocalCharacterEntity()
                .getComponent(DynamicBodyComponent.class).dynamicBody.getPosition().y, 0);
        batch.  setProjectionMatrix(camera.combined);
        shpRend.setProjectionMatrix(camera.combined);
        camera.update();

        // Animation time calculation
        elapsedTime += Gdx.graphics.getDeltaTime(); // add time between frames
    }

    private void checkServerConnection() {
        if(!game.connected) {
            if( disconnectTime == 0 ) disconnectTime = elapsedTime;
            else
            if(elapsedTime - disconnectTime > timeOut) {
                disconnectTime = 0;
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }
    }
}
