package org.sausagepan.prototyp.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import javax.xml.soap.Text;

/**
 * Created by Georg on 06.07.2015.
 */
public class MediaManager {

    /* ................................................................................................ ATTRIBUTES .. */

    private AssetManager assets;

    // file names
    private String mazeBackgroundMusicFile = "music/Explorer_by_ShwiggityShwag_-_CC-by-3.0.ogg";
    private String mainMenuBackgroundImgFile = "textures/backgrounds/main_menu_bg.png";


    /* .............................................................................................. CONSTRUCTORS .. */

    public MediaManager () {
        this.assets = new AssetManager();

        // load music
        assets.load(this.mazeBackgroundMusicFile, Music.class);
        assets.load(this.mainMenuBackgroundImgFile, Texture.class);
        assets.load("textures/spritesheets/knight_m.pack", TextureAtlas.class);


        assets.finishLoading();
    }


    /* ................................................................................................... METHODS .. */

    public void dispose() {
        assets.dispose();
    }


    /* ......................................................................................... GETTERS & SETTERS .. */

    public Music getMazeBackgroundMusic() {
        return assets.get(this.mazeBackgroundMusicFile, Music.class);
    }

    public Texture getMainMenuBackgroundImg() {
        return assets.get(this.mainMenuBackgroundImgFile, Texture.class);
    }

    public TextureAtlas getTextureAtlas(String name) {
    	return assets.get(name);
    }
}
