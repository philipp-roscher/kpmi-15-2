package org.sausagepan.prototyp.view;

import org.sausagepan.prototyp.KPMIPrototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class MainMenuScreen implements Screen {
	
	/* ........................................................ ATTRIBUTES .. */
	final KPMIPrototype game;
	public OrthographicCamera camera;
	private Texture bgImg;
	
	
	/* ...................................................... CONSTRUCTORS .. */
	public MainMenuScreen(final KPMIPrototype game) {
		this.game  = game;
		camera     = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		this.bgImg = new Texture("textures/backgrounds/main_menu_bg.png");
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

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
			game.setScreen(new InMaze(game));
			dispose();
		}

		// Update camera
		camera.update();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
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
