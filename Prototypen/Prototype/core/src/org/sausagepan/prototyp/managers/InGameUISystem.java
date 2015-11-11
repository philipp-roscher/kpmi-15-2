package org.sausagepan.prototyp.managers;

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

import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;

/**
 * Created by georg on 11.11.15.
 */
public class InGameUISystem extends ObservingEntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private OrthographicCamera camera;
    private MediaManager media;
    private Batch batch;
    private Array<TextureRegion> healthBarImages;
    private TextureRegion knightImg;
    private TextureRegion recentHealthBarImg;
    private float HP;

    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */

    public InGameUISystem(MediaManager media, Batch batch) {
        this.media = media;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        TextureAtlas atlas = media.getTextureAtlasType("healthBar");
        this.healthBarImages = new Array<TextureRegion>();
        this.healthBarImages.add(atlas.findRegion("healthbar_empty"));
        this.healthBarImages.add(atlas.findRegion("healthbar_1tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_2tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_3tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_4tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_5tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_6tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_7tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_8tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_9tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_10tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_11tenth"));
        this.healthBarImages.add(atlas.findRegion("healthbar_full"));
        this.knightImg = atlas.findRegion("face_knight");
        this.recentHealthBarImg = healthBarImages.get(12);
    }
    /* ............................................................................... METHODS .. */
    public void draw() {
        this.batch.begin();
        this.batch.draw(knightImg, 16, 400, 64, 64);
        this.batch.draw(recentHealthBarImg, 64, 424, 220, 40);
        this.batch.end();
        this.camera.update();
    }

    @Override
    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(
                HealthComponent.class,
                InputComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            HealthComponent health = hm.get(entity);
            HP = ((float)health.HP)/(health.initialHP);
            if(HP > 0.9) recentHealthBarImg = healthBarImages.get(12);
            if(HP < 0.9) recentHealthBarImg = healthBarImages.get(12);
            if(HP < 0.82) recentHealthBarImg = healthBarImages.get(11);
            if(HP < 0.76) recentHealthBarImg = healthBarImages.get(10);
            if(HP < 0.7) recentHealthBarImg = healthBarImages.get(9);
            if(HP < 0.64) recentHealthBarImg = healthBarImages.get(8);
            if(HP < 0.58) recentHealthBarImg = healthBarImages.get(7);
            if(HP < 0.52) recentHealthBarImg = healthBarImages.get(6);
            if(HP < 0.46) recentHealthBarImg = healthBarImages.get(5);
            if(HP < 0.4) recentHealthBarImg = healthBarImages.get(4);
            if(HP < 0.34) recentHealthBarImg = healthBarImages.get(3);
            if(HP < 0.28) recentHealthBarImg = healthBarImages.get(2);
            if(HP < 0.22) recentHealthBarImg = healthBarImages.get(1);
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
