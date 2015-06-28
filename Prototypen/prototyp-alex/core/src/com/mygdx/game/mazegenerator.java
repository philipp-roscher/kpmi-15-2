package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class mazegenerator extends ApplicationAdapter implements InputProcessor{
	int mazeheight = 5;
	int mazewidth = 5;
	
	SpriteBatch batch;
	Texture img;
	OrthographicCamera camera;
	
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	
	TiledMap map = new TiledMap();
	MapLayers layers = map.getLayers();

	TiledMapTileLayer layer1 = new TiledMapTileLayer(mazewidth * 10 + 20, mazeheight * 10, 16, 16);
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		camera.update();
		
		generateMaze();
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
	}
	
	private void generateMaze(){		
		for(int i = mazeheight; i > 0; i--){
			for(int j = mazewidth; j > 0; j--){
				tiledMap = new TmxMapLoader().load("maze" + atRandom() + ".tmx");
				TiledMapTileLayer test = (TiledMapTileLayer) tiledMap.getLayers().get(0);
				
				for(int k = 0; k < 10; k++){
					for(int l = 0; l < 10; l++){
							Cell cell = new Cell();

							cell.setTile(test.getCell(k, l).getTile());
							layer1.setCell(k  + (i-1)*10 + 10, l + (j-1)*10, cell);
					}
				}
			}
		}
		
		addSaveZone();
		addTreasure();
		
		layers.add((MapLayer)layer1);
		
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
	}
	
	private void addSaveZone(){
		int mid = (int) Math.ceil(mazewidth / 2);
		
		tiledMap = new TmxMapLoader().load("room1.tmx");
		TiledMapTileLayer test = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		
		for(int k = 0; k < 10; k++){
			for(int l = 0; l < 10; l++){
					Cell cell = new Cell();

					cell.setTile(test.getCell(k, l).getTile());
					layer1.setCell(k, l + mid*10, cell);
			}
		}
		
		tiledMap = new TmxMapLoader().load("room2.tmx");
		TiledMapTileLayer test2 = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		
		for(int k = 0; k < 10; k++){
			for(int l = 0; l < 10; l++){
					Cell cell = new Cell();

					cell.setTile(test2.getCell(k, l).getTile());
					layer1.setCell(k + mazewidth*10 + 10, l + mid*10, cell);
			}
		}
		
		
	}
	
	private void addTreasure(){
		int mid = (int) Math.ceil(mazewidth / 2);
		
		tiledMap = new TmxMapLoader().load("treasure.tmx");
		TiledMapTileLayer test = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		
		for(int k = 0; k < 10; k++){
			for(int l = 0; l < 10; l++){
					Cell cell = new Cell();

					cell.setTile(test.getCell(k, l).getTile());
					layer1.setCell(k + mid*10 + 10, l + mid*10, cell);
			}
		}
	}
	
	private int atRandom(){
		return (int) ((Math.random()*3)+1);
	}
	
	@Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT)
            camera.translate(-32,0);
        if(keycode == Input.Keys.RIGHT)
            camera.translate(32,0);
        if(keycode == Input.Keys.UP)
            camera.translate(0,-32);
        if(keycode == Input.Keys.DOWN)
            camera.translate(0,32);
        if(keycode == Input.Keys.NUM_1)
            tiledMap.getLayers().get(0).setVisible(!tiledMap.getLayers().get(0).isVisible());
        if(keycode == Input.Keys.NUM_2)
            tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());
        return false;
    }

    @Override
    public boolean keyTyped(char character) {

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    	generateMaze();
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
