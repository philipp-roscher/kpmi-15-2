package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by georg on 11.11.15.
 */
public class InGameUISystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Stage stage;
    private Viewport viewport;
    private Batch batch;
    private Array<TextureRegion> healthBarImages;
    private TextureRegion characterImg;
    private TextureRegion recentHealthBarImg;
    private Array<TextureRegion> keyFragmentImgs;
    private boolean[] keyFragmentItems;
    private KPMIPrototype game;
    private BitmapFont font;

    // local entity
    private Entity entity;

    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<InventoryComponent> im
            = ComponentMapper.getFor(InventoryComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */

    public InGameUISystem(MediaManager media, CharacterClass characterClass, KPMIPrototype game) {
        this.font = new BitmapFont();
        this.viewport = new FitViewport(800,480);
        this.stage = new Stage(viewport);
        this.batch = stage.getBatch();
        TextureAtlas atlas = media.getTextureAtlasType("IngameUI");
        keyFragmentItems = new boolean[3];
        this.game = game;

        for(int i=0; i<3; i++) {
        	keyFragmentItems[i] = false;
        }

        // Get health bar Images
        this.healthBarImages = new Array<TextureRegion>();
        for(int i=0; i<12; i++)
            this.healthBarImages.add(atlas.findRegion("life_bar", i));

        // Get Key part Images
        this.keyFragmentImgs = new Array<TextureRegion>();
        for(int i=0; i<4; i++)
            this.keyFragmentImgs.add(atlas.findRegion("key", i));

        switch(characterClass) {
            case KNIGHT_M:this.characterImg = atlas.findRegion("3dface_knight");break;
            case KNIGHT_F:this.characterImg = atlas.findRegion("3dface_knight");break;
            case ARCHER_M:this.characterImg = atlas.findRegion("3dface_archer");break;
            case ARCHER_F:this.characterImg = atlas.findRegion("3dface_archer");break;
            case DRAGON:this.characterImg = atlas.findRegion("3dface_dragon");break;
            case SHAMAN_M:this.characterImg = atlas.findRegion("3dface_shaman");break;
            case SHAMAN_F:this.characterImg = atlas.findRegion("3dface_shaman");break;
            default:this.characterImg = atlas.findRegion("3dface_knight");break;
        }

        this.recentHealthBarImg = healthBarImages.get(11);
    }
    /* ............................................................................... METHODS .. */

    /**
     * Draws the Ingame UI like healthbar, character image, keys, items, buttons and so on
     */
    public void draw() {
        this.stage.getViewport().apply();
        this.batch.begin();
        this.batch.setProjectionMatrix(stage.getCamera().combined);
        this.batch.draw(recentHealthBarImg, 16, 400, 256, 72);
        this.batch.draw(characterImg, 16, 400, characterImg.getRegionWidth(), 72);
        batch.draw(keyFragmentImgs.get(0), 690, 429);
        if(keyFragmentItems[0])
        	batch.draw(keyFragmentImgs.get(1), 691, 430);
        if(keyFragmentItems[1])
        	batch.draw(keyFragmentImgs.get(2), 723, 430);
        if(keyFragmentItems[2])
        	batch.draw(keyFragmentImgs.get(3), 755, 430);

        if(!game.gameReady) {
            font.setColor(1, 0, 0, 1);
            font.draw(batch, "Waiting for players... " + game.clientCount + "/" + game.maxClients, 630, 20);
            font.setColor(1, 1, 1, 1);
        }
        
        IsDeadComponent isDead = entity.getComponent(IsDeadComponent.class);
        if (isDead != null) {
        	float respawnTime = (isDead.deathLength - (System.currentTimeMillis() - isDead.deathTime)) / 1000f;
        	if(respawnTime >= 0) {
	            font.setColor(1, 0, 0, 1);
	            // TODO use proper coordinates??
	            font.draw(batch, "Respawn in " + respawnTime, 350, 280);
	            font.setColor(1, 1, 1, 1);
        	}
        }

        //draw Winner/Loser after Game
        if (game.gameWon) {
            font.setColor(0, 1, 0, 1);
            font.getData().setScale(2);
            font.draw(batch, "You have won!", 305, 320);
            font.getData().setScale(1);
            font.setColor(1, 1, 1, 1);
        }

        if (game.gameLost) {
            font.setColor(1, 0, 0, 1);
            font.getData().setScale(2);
            font.draw(batch, "You have lost!", 305, 320);
            font.getData().setScale(1);
            font.setColor(1, 1, 1, 1);
        }
        
        if (GlobalSettings.DEBUGGING_ACTIVE) {
        	int ping = game.client.getReturnTripTime();
        	int fps = Gdx.graphics.getFramesPerSecond();
        	font.draw(batch, ping + " ms", 100, 420);
        	font.draw(batch, fps + " FPS", 150, 420);
        }

        this.batch.end();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine) {
        entity = engine.getEntitiesFor(Family.all(
                HealthComponent.class,
                InputComponent.class,
                NetworkComponent.class,
                InventoryComponent.class).get()).get(0);
    }

    public void update(float deltaTime) {
        HealthComponent health = hm.get(entity);
        recentHealthBarImg = healthBarImages.get(MathUtils.roundPositive((float) health.HP/(health.initialHP)*10)+1);
        InventoryComponent inventory = im.get(entity);
        this.keyFragmentItems = inventory.teamKeys;
    }
    /* ..................................................................... GETTERS & SETTERS .. */

    public Viewport getViewport() {
        return viewport;
    }
    
    public Stage getStage() {
    	return stage;
    }
}
