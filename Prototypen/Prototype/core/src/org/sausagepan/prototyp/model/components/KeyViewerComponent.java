package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.User_Interface.Actors.KeyBackground;
import org.sausagepan.prototyp.User_Interface.Actors.KeyActor;
import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.model.Key;

import java.util.LinkedList;
import java.util.List;
/**
 * Created by Bettina on 02.11.2015.
 */
public class KeyViewerComponent implements ApplicationListener, Component {

    private Stage stage;
    private KeyBackground keyBackground;

    public KeyViewerComponent()
    {
        keyBackground = new KeyBackground();
    }

    @Override
    public void create() {
        stage = new Stage();
        stage.addActor(keyBackground);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

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
    }

    //Own Methods
    public void addKey(KeyActor key)
    {
        stage.addActor(key);
    }

    public Array<Actor> removeKeys()
    {
        Array<Actor> actors = stage.getActors();
        stage.clear();
        stage.addActor(keyBackground);
        return actors;
    }
}
