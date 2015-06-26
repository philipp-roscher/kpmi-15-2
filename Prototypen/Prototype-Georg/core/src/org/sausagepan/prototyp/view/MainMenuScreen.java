package org.sausagepan.prototyp.view;

import com.badlogic.gdx.physics.box2d.Box2D;
import org.sausagepan.prototyp.KPMIPrototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.sausagepan.prototyp.enums.DAMAGETYPE;
import org.sausagepan.prototyp.enums.WEAPONTYPE;
import org.sausagepan.prototyp.managers.BattleSystem;
import org.sausagepan.prototyp.managers.CharacterManager;
import org.sausagepan.prototyp.model.*;

public class MainMenuScreen implements Screen {
	
	/* ........................................................ ATTRIBUTES .. */
	final KPMIPrototype game;
	public OrthographicCamera camera;
	public Viewport viewport;
	private Texture bgImg;
	
	
	/* ...................................................... CONSTRUCTORS .. */
	public MainMenuScreen(final KPMIPrototype game) {
		this.game  = game;
		camera     = new OrthographicCamera();
		viewport        = new FitViewport(800, 480, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		
		this.bgImg = new Texture("textures/backgrounds/main_menu_bg.png");

		// Initialize Box2D
//		Box2D.init();
	}


	/* ................................................................................................... METHODS .. */

	public void setUpGame() {
		BattleSystem bs = new BattleSystem();
		CharacterManager cm = new CharacterManager();

		// Character 1
		cm.addCharacter(
				new org.sausagepan.prototyp.model.Character("hero1", "m", "warrior_m.pack",
						new Status(),
						new Weapon("standard_sword", 3, WEAPONTYPE.SWORD, DAMAGETYPE.PHYSICAL, 20, 180))
		);

		// Character 2
		cm.addCharacter(
				new org.sausagepan.prototyp.model.Character("hero2", "m", "warrior_m.pack",
						new Status(),
						new Weapon("standard_sword", 3, WEAPONTYPE.SWORD, DAMAGETYPE.PHYSICAL, 20, 180))
		);

		// Switch to game screen
		game.setScreen(new InMaze(game, bs, cm));
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
		game.batch.end();
		
		// If screen is touched
		if(Gdx.input.justTouched()) {
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
		bgImg.dispose();
	}

}
