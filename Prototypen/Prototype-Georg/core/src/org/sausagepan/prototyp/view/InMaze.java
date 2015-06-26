package org.sausagepan.prototyp.view;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.managers.BattleSystem;
import org.sausagepan.prototyp.managers.CharacterManager;
import org.sausagepan.prototyp.model.Character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InMaze implements Screen {
	
	/* ........................................................ ATTRIBUTES .. */
	final KPMIPrototype game;
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private ShapeRenderer shpRend;
	private BitmapFont font;

	private CharacterManager charMan;
	private BattleSystem battle;


	private Vector3 touchPos;
	private Texture background;
	private Music bgMusic;
	private float elapsedTime = 0;
	
	
	/* ...................................................... CONSTRUCTORS .. */
	public InMaze(final KPMIPrototype game, BattleSystem battleSystem, CharacterManager characterManager) {
		this.game = game;
		camera = new OrthographicCamera();
		viewport = new FitViewport(800, 480, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		batch = new SpriteBatch();
		shpRend = new ShapeRenderer();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		touchPos = new Vector3();
		this.background = new Texture("textures/backgrounds/big_dungeon_room.png");
		this.background.setFilter(TextureFilter.Linear, TextureFilter.Nearest);
		this.bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Explorer_by_ShwiggityShwag_-_CC-by-3.0.ogg"));
		this.bgMusic.setLooping(true);
		this.bgMusic.play();
		this.battle = battleSystem;
		this.charMan = characterManager;
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
		batch.  setProjectionMatrix(camera.combined);
		shpRend.setProjectionMatrix(camera.combined);
		
		// Animation time calculation
		elapsedTime += Gdx.graphics.getDeltaTime();
		
		// Move character
		charMan.getCharacters().get(0).update();
		handleInput();
		
		// Draw sprites to batch
		batch.begin();
			batch.draw(background, 0, 0);
		for(Character c : charMan.getCharacters())
			c.drawCharacter(batch, elapsedTime);
		batch.end();

		// Shapes
		for(Character c : charMan.getCharacters())
			c.drawCharacterStatus(shpRend);

		battle.updateBullets(charMan.getCharacters().get(0), charMan.characters);
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
			charMan.getCharacters().get(0).handleTouchInput(touchPos);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			System.out.println("Attack!");
            charMan.getCharacters().get(0).attack();
			battle.updateAttack(charMan.getCharacters().get(0), charMan.getCharacters());
		}

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            System.out.println("Shoot!");
            charMan.getCharacters().get(0).shoot();
        }
	}

}
