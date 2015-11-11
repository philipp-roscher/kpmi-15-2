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
    private int HP;

    private ImmutableArray<Entity> entities;

    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */

    public InGameUISystem(MediaManager media, Batch batch) {
        this.media = media;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        TextureAtlas atlas = media.getTextureAtlasType("healthBar");
        this.healthBarImages = new Array<TextureRegion>();
        this.healthBarImages.add(atlas.findRegion("healthbar_0tenth"));
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
        this.knightImg = atlas.findRegion("face_knight");
        this.recentHealthBarImg = healthBarImages.get(11);
    }
    /* ............................................................................... METHODS .. */
    public void draw() {
        this.batch.begin();
        this.batch.draw(knightImg, 16, 400, 64, 64);
        this.batch.draw(recentHealthBarImg, 70, 424, 220, 40);
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
            HP = MathUtils.roundPositive((float) health.HP/(health.initialHP)*10)+1;
            recentHealthBarImg = healthBarImages.get(HP);
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
