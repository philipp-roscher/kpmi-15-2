package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.items.KeyFragmentItem;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * Created by georg on 11.11.15.
 */
public class InGameUISystem extends ObservingEntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private OrthographicCamera camera;
    private Batch batch;
    private Array<TextureRegion> healthBarImages;
    private TextureRegion characterImg;
    private TextureRegion recentHealthBarImg;
    private TextureRegion attackButton;
    private Array<TextureRegion> keyFragmentImgs;
    private Array<KeyFragmentItem> keyFragmentItems;
    private int HP;

    private ImmutableArray<Entity> entities;

    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<InventoryComponent> im
            = ComponentMapper.getFor(InventoryComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */

    public InGameUISystem(MediaManager media, CharacterClass characterClass) {
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        TextureAtlas atlas = media.getTextureAtlasType("IngameUI");
        this.keyFragmentItems = new Array<KeyFragmentItem>();

        // Get health bar Images
        this.healthBarImages = new Array<TextureRegion>();
        for(int i=0; i<12; i++)
            this.healthBarImages.add(atlas.findRegion("healthbar_" + i + "tenth"));

        // Get Key part Images
        this.keyFragmentImgs = new Array<TextureRegion>();
        for(int i=0; i<4; i++)
            this.keyFragmentImgs.add(atlas.findRegion("key", i));

        this.attackButton = atlas.findRegion("attackButton");

        switch(characterClass) {
            case KNIGHT_M:this.characterImg = atlas.findRegion("face_knight");break;
            case KNIGHT_F:this.characterImg = atlas.findRegion("face_knight");break;
            case ARCHER_M:this.characterImg = atlas.findRegion("face_archer");break;
            case ARCHER_F:this.characterImg = atlas.findRegion("face_archer");break;
            case DRAGON:this.characterImg = atlas.findRegion("face_dragon");break;
            case SHAMAN_M:this.characterImg = atlas.findRegion("face_shaman");break;
            case SHAMAN_F:this.characterImg = atlas.findRegion("face_shaman");break;
            default:this.characterImg = atlas.findRegion("face_knight");break;
        }

        this.recentHealthBarImg = healthBarImages.get(11);
    }
    /* ............................................................................... METHODS .. */

    /**
     * Draws the Ingame UI like healthbar, character image, keys, items, buttons and so on
     */
    public void draw() {
        this.batch.begin();
        this.batch.draw(characterImg, 16, 400, 64, 64);
        this.batch.draw(recentHealthBarImg, 70, 424, 220, 40);
        this.batch.draw(attackButton, 32, 32, 48, 48);
        batch.draw(keyFragmentImgs.get(0), 690, 429);
        for(KeyFragmentItem kf : keyFragmentItems) {
            switch(kf.keyFragmentNr) {
                case 1: batch.draw(keyFragmentImgs.get(1), 691, 430); break;
                case 2: batch.draw(keyFragmentImgs.get(2), 723, 430); break;
                case 3: batch.draw(keyFragmentImgs.get(3), 755, 430); break;
            }
        }
        this.batch.end();
        this.camera.update();
    }

    @Override
    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(
                HealthComponent.class,
                InputComponent.class,
                NetworkComponent.class,
                InventoryComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            HealthComponent health = hm.get(entity);
            HP = MathUtils.roundPositive((float) health.HP/(health.initialHP)*10)+1;
            recentHealthBarImg = healthBarImages.get(HP);
            InventoryComponent inventory = im.get(entity);
            this.keyFragmentItems = inventory.keyFragments;
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
