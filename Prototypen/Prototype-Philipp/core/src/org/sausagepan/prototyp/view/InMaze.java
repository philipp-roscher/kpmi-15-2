package org.sausagepan.prototyp.view;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.Network.GameStateRequest;
import org.sausagepan.prototyp.Network.GameStateResponse;
import org.sausagepan.prototyp.Network.PositionUpdate;
import org.sausagepan.prototyp.model.Character;
import org.sausagepan.prototyp.model.Position;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class InMaze implements Screen {
	
	/* ........................................................ ATTRIBUTES .. */
	final KPMIPrototype game;
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont font;
	private Vector3 touchPos;
	private Character hero;
	private Texture background;
	private Music bgMusic;
	private float elapsedTime = 0;
	private ConcurrentHashMap<Integer, Position> otherPositions;
	
	
	/* ...................................................... CONSTRUCTORS .. */
	public InMaze(final KPMIPrototype game) {
		this.game = game;
		camera = new OrthographicCamera();
		viewport = new FitViewport(800, 480, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2,0);
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		touchPos = new Vector3();
		this.hero = new Character("hero", "m", "warrior_m.pack");
		this.background = new Texture("textures/backgrounds/big_dungeon_room.png");
		this.background.setFilter(TextureFilter.Linear, TextureFilter.Nearest);
		this.bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Explorer_by_ShwiggityShwag_-_CC-by-3.0.ogg"));
		this.bgMusic.setLooping(true);
		this.bgMusic.play();
		otherPositions = new ConcurrentHashMap<Integer, Position>();
		game.client.sendTCP(new GameStateRequest());
		game.client.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				//System.out.println("Paket empfangen");
				//System.out.println(object.getClass());
				if (object instanceof GameStateResponse) {
					//System.out.println("GameStateResponse-Paket empfangen");
					
					GameStateResponse result = (GameStateResponse)object;					
					otherPositions = new ConcurrentHashMap<Integer,Position>(result.positions);
					//System.out.println("enthält " + otherPositions.size() +" Positionen");
					
					otherPositions.remove(game.clientId);
				}
			}});
	}

	
	/* .................................................... LibGDX METHODS .. */
	@Override
	public void show() {
		this.batch = new SpriteBatch();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		
		// Animation time calculation
		elapsedTime += Gdx.graphics.getDeltaTime();
		
		// Move character
		hero.update();
		handleInput();
		
		PositionUpdate posUpdate = new PositionUpdate();
		posUpdate.playerId = game.clientId;
		posUpdate.position = new Position(hero.getPosition(), hero.getDirection());
		System.out.println("Position: "+ hero.getPosition());
		System.out.println("Direction: "+ hero.getDirection());
		
		game.client.sendTCP(posUpdate);
		
		// Draw sprites to batch
		batch.begin();
			batch.draw(background, 0, 0);
			// Draw other characters
			if(!otherPositions.isEmpty()) {
				for(Position pos : otherPositions.values()) {
					batch.draw( getAnimation(pos.getDirection()).getKeyFrame(elapsedTime, true), pos.getPosition().x, pos.getPosition().y);
				}
			}
			// Draw own character
			batch.draw(hero.getAnimation().getKeyFrame(elapsedTime, true), hero.getPosition().x, hero.getPosition().y);
			
		batch.end();
		
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
		this.background.dispose();
		this.batch.dispose();
		this.bgMusic.dispose();
		this.font.dispose();
	}
	
	
	/* ........................................................... METHODS .. */

	public void handleInput() {
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			hero.handleTouchInput(touchPos);
		}
	}
	
	public Animation getAnimation(Vector3 direction) {
		float ax = direction.x;
		float ay = direction.y;
		ArrayMap<String,Animation> anims = hero.getAnims();
		Animation currentAnim;
		
			if (ax*ax > ay*ay) {
				// x component dominates
				if(ax < 0) currentAnim = anims.get("left");// character moves left
				else       currentAnim = anims.get("right"); // character moves right
			} else {
				// y component dominates
				if(ay > 0) currentAnim = anims.get("up");// character goes up
				else       currentAnim = anims.get("down"); // character goes down
			}
			
			if(ax < 0.1 && ax > -0.1 && ay < 0.1 && ay > -0.1) currentAnim = anims.get("idle");
			
		return currentAnim;
	}
	
}
