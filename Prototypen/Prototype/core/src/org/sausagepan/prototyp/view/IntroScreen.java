package org.sausagepan.prototyp.view;

import org.sausagepan.prototyp.KPMIPrototype;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by georg on 03.01.16.
 */
public class IntroScreen implements Screen {

    /* ............................................................................ ATTRIBUTES .. */
    private KPMIPrototype game;
    public OrthographicCamera camera;
    public Viewport viewport;

    private Texture bGImg;

    // Scene2D.ui
    private Stage stage;

    /* ........................................................................... CONSTRUCTOR .. */
    public IntroScreen (KPMIPrototype game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        bGImg = game.mediaManager.getLogoImg();

        // Scene2D
        FitViewport fit = new FitViewport(800, 480);
        this.stage = new Stage(fit);
        Gdx.input.setInputProcessor(stage);
        final Image logoImg = new Image(bGImg);
        logoImg.setPosition(400, 240, Align.center);
        logoImg.setHeight(128);
        logoImg.setWidth(128);

        // configure the fade-in/out effect on the splash image
        SequenceAction actions = new SequenceAction();
        actions.addAction(Actions.fadeIn(3f));
        actions.addAction(Actions.fadeOut(3f));
        actions.setActor(logoImg);
        logoImg.addAction(Actions.sequence(
                Actions.alpha(0), Actions.fadeIn(1f), Actions.alpha(1f, 1f),Actions.fadeOut(1f),
                Actions.run(onSplashFinishedRunnable) ) );


        stage.addActor(logoImg);
    }

    Runnable onSplashFinishedRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            game.setScreen(new MainMenuScreen(game));
        }
    };

    /* ............................................................................... METHODS .. */
    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {

    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
