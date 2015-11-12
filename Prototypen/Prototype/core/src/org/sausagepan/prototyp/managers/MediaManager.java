package org.sausagepan.prototyp.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by Georg on 06.07.2015.
 */
public class MediaManager {

    /* ................................................................................................ ATTRIBUTES .. */

    private AssetManager assets;

    // file names
    private String mazeBackgroundMusicFile      = "music/Explorer_by_ShwiggityShwag_-_CC-by-3.0.ogg";
    private String mainMenuBackgroundImgFile    = "textures/backgrounds/main_menu_bg.png";
    private String SelectionArcherFBig          = "textures/characterSelection/Archer_f_big.png";
    private String SelectionKnightMBig          = "textures/characterSelection/Knight_m_big.png";
    private String SelectionFighterMBig         = "textures/characterSelection/Fighter_m_big.png";
    private String SelectionShamanMBig          = "textures/characterSelection/Shaman_m_big.png";
    private String SelectionDragonRedBig        = "textures/characterSelection/Dragon_red_big.png";
    private String IngameUISheetFile            = "textures/spritesheets/ingame_UI.pack";
    private String itemsSheetFile               = "textures/spritesheets/items.pack";

    //character sheets
    private String knightMSpriteSheetFile        = "textures/spritesheets/characters/knight_m.pack";
    private String archerFSpriteSheetFile        = "textures/spritesheets/characters/archer_f.pack";
    private String dragonRedSpriteSheetFile        = "textures/spritesheets/characters/dragon_red.pack";
    private String shamanMSpriteSheetFile        = "textures/spritesheets/characters/shaman_m.pack";
    private String fighterMSpriteSheetFile        = "textures/spritesheets/characters/fighter_m.pack";
    //weapons
    private String weaponsSpriteSheetFile       = "textures/spritesheets/weapons.pack";
    //monsters
    private String zombieSpriteFile             = "textures/spritesheets/monsters/zombie_01.pack";
    private String skeletonSpriteFile             = "textures/spritesheets/monsters/skeleton.pack";


    /* .............................................................................................. CONSTRUCTORS .. */

    public MediaManager () {
        this.assets = new AssetManager();

        assets.load(this.IngameUISheetFile, TextureAtlas.class);
        assets.load(this.itemsSheetFile, TextureAtlas.class);
        // load music
        assets.load(this.mazeBackgroundMusicFile, Music.class);
        //load Images
        assets.load(this.mainMenuBackgroundImgFile, Texture.class);
        assets.load(this.SelectionArcherFBig, Texture.class);
        assets.load(this.SelectionDragonRedBig, Texture.class);
        assets.load(this.SelectionFighterMBig, Texture.class);
        assets.load(this.SelectionKnightMBig, Texture.class);
        assets.load(this.SelectionShamanMBig, Texture.class);
        //load sprites
        assets.load(this.knightMSpriteSheetFile, TextureAtlas.class);
        assets.load(this.archerFSpriteSheetFile, TextureAtlas.class);
        assets.load(this.dragonRedSpriteSheetFile, TextureAtlas.class);
        assets.load(this.shamanMSpriteSheetFile, TextureAtlas.class);
        assets.load(this.fighterMSpriteSheetFile, TextureAtlas.class);
        assets.load(this.weaponsSpriteSheetFile, TextureAtlas.class);
        assets.load(this.zombieSpriteFile, TextureAtlas.class);
        assets.load(this.skeletonSpriteFile, TextureAtlas.class);


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

    public Texture getSelectionKnightMBig() {
        return assets.get(this.SelectionKnightMBig, Texture.class);
    }

    public Texture getSelectionArcherFBig() {
        return assets.get(this.SelectionArcherFBig, Texture.class);
    }

    public Texture getSelectionFighterMBig() {
        return assets.get(this.SelectionFighterMBig, Texture.class);
    }

    public Texture getSelectionShamanMBig() {
        return assets.get(this.SelectionShamanMBig, Texture.class);
    }

    public Texture getSelectionDragonRedBig() {
        return assets.get(this.SelectionDragonRedBig, Texture.class);
    }

    public TextureAtlas getTextureAtlas(String name) {
        return assets.get(name);
    }

    /**
     * Returns the required texture atlas. Possible values:
     * weapons
     * healthBar
     * @param type
     * @return
     */
    public TextureAtlas getTextureAtlasType(String type) {
        if(type.equals("weapons")) return assets.get(weaponsSpriteSheetFile);
        if(type.equals("IngameUI")) return assets.get(IngameUISheetFile);
        if(type.equals("items")) return assets.get(itemsSheetFile);
        return null;
    }

}
