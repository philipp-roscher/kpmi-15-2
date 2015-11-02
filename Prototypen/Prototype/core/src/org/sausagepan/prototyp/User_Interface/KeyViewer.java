package org.sausagepan.prototyp.User_Interface;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import org.sausagepan.prototyp.User_Interface.Actors.KeyBackground;
import org.sausagepan.prototyp.User_Interface.Actors.KeyActor;
import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.model.Key;

import java.util.LinkedList;
import java.util.List;
/**
 * Created by Bettina on 02.11.2015.
 */
public class KeyViewer implements ApplicationListener {

    private boolean isKeyHolder;
    private Stage stage;
    private List<Key> keyBag;
    private KeyBackground keyBackground;

    public KeyViewer(boolean isKeyHolder, List<Key> keyBag)
    {
        if(!isKeyHolder)
            return;

        this.isKeyHolder = isKeyHolder;
        this.keyBag = keyBag;
    }

    @Override
    public void create() {

        stage = new Stage();
        keyBackground = new KeyBackground();
        stage.addActor(keyBackground);

        for(Key key: keyBag)
        {
            if(key != null)
            {
                KeyActor keyActor = new KeyActor(key);
                stage.addActor(keyActor);
            }
        }
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

    public void removeKey(KeyActor key)
    {

    }
}
