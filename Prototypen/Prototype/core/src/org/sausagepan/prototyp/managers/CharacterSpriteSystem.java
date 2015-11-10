package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;

/**
 * Turns and refreshes characters sprite.
 * Created by georg on 28.10.15.
 */
public class CharacterSpriteSystem extends ObservingEntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

    private ComponentMapper<CharacterSpriteComponent> cm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public CharacterSpriteSystem() {};
    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(
                CharacterSpriteComponent.class,
                DynamicBodyComponent.class).get());
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            CharacterSpriteComponent sprite = cm.get(entity);
            DynamicBodyComponent body = dm.get(entity);

            if(body.dynamicBody.getLinearVelocity().len() > 0.1)
            if(Math.abs(body.dynamicBody.getLinearVelocity().x)
             > Math.abs(body.dynamicBody.getLinearVelocity().y)) {
                // Character horizontally
                if(body.dynamicBody.getLinearVelocity().x > 0)
                    sprite.recentAnim = sprite.playerAnims.get("e");
                else
                    sprite.recentAnim = sprite.playerAnims.get("w");
            } else {
                // Character vertically
                if(body.dynamicBody.getLinearVelocity().y > 0)
                    sprite.recentAnim = sprite.playerAnims.get("n");
                else
                    sprite.recentAnim = sprite.playerAnims.get("s");
            }

            sprite.recentIdleImg = sprite.recentAnim.getKeyFrames()[0];

            if(Math.abs(body.dynamicBody.getLinearVelocity().x) < 0.1 &&
               Math.abs(body.dynamicBody.getLinearVelocity().y) < 0.1)
                sprite.sprite.setRegion(sprite.recentIdleImg);
            else
                sprite.sprite.setRegion(sprite.recentAnim.getKeyFrame(elapsedTime, true));

        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
