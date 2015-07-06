package org.sausagepan.prototyp;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.view.MainMenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class KPMIPrototype extends Game {
	
	/* ........................................................ ATTRIBUTES .. */
	public SpriteBatch batch;
	public BitmapFont font;
	public MediaManager mediaManager;
	
	
	/* .................................................... LibGDX METHODS .. */
	@Override
	public void create () {
		batch = new SpriteBatch();
		font  = new BitmapFont();
		mediaManager = new MediaManager();

		// switch to main menu screen
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose() {
		// Getting rid ob objects, as garbage collector won't do it
		batch.dispose();
		font.dispose();
		mediaManager.dispose();
	}

}
