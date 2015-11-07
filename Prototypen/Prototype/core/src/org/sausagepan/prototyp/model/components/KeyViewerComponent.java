package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.sausagepan.prototyp.User_Interface.Actors.KeyBackground;
import org.sausagepan.prototyp.User_Interface.Actors.KeyActor;
import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.model.Key;

import java.util.LinkedList;
import java.util.List;

import javax.swing.text.View;

/**
 * Created by Bettina on 02.11.2015.
 */
public class KeyViewerComponent implements ApplicationListener, Component {

    private Stage stage;
    private KeyBackground keyBackground;
    private Batch batch;
    private ScreenViewport viewport;
    private OrthographicCamera camera;

    public KeyViewerComponent(Batch batch)
    {
        keyBackground = new KeyBackground();
        this.batch = batch;
        camera = new OrthographicCamera(800, 480);
        this.viewport = new ScreenViewport(camera);
        this.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void create() {
        stage = new Stage(this.viewport, this.batch);
        stage.addActor(keyBackground);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

        if(stage == null)
            return;

        stage.act();
        stage.draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }

    //Own Methods
    public void addKey(KeyActor key)
    {
        stage.addActor(key);
    }

    public Array<Actor> removeKeys()
    {
        Array<Actor> actors = stage.getActors();
        actors.removeIndex(0);
        stage.clear();
        stage.addActor(keyBackground);
        return actors;
    }
}
