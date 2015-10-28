package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;

/**
 * Created by georg on 28.10.15.
 */
public class CharacterSpriteSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;

    private ComponentMapper<CharacterSpriteComponent> cm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    public CharacterSpriteSystem() {};
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                CharacterSpriteComponent.class,
                InputComponent.class).get());
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            CharacterSpriteComponent characterSprite = cm.get(entity);
            InputComponent input = im.get(entity);

            switch(input.direction) {
                case NORTH: characterSprite.recentAnim = characterSprite.playerAnims.get("n");break;
                case SOUTH: characterSprite.recentAnim = characterSprite.playerAnims.get("s");break;
                case WEST:  characterSprite.recentAnim = characterSprite.playerAnims.get("w");break;
                case EAST:  characterSprite.recentAnim = characterSprite.playerAnims.get("e");break;
            }
            characterSprite.recentIdleImg = characterSprite.recentAnim.getKeyFrames()[0];

            // set sprite image
            if(input.moving) characterSprite.sprite.setRegion(
                        characterSprite.recentAnim.getKeyFrame(elapsedTime, true));
            else characterSprite.sprite.setRegion(
                    characterSprite.recentIdleImg);
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
