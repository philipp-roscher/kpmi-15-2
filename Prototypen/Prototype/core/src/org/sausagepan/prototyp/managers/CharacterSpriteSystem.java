package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.items.Bow;

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
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);

    /* ........................................................................... CONSTRUCTOR .. */
    public CharacterSpriteSystem() {}
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
            WeaponComponent weapon = wm.get(entity);
            InputComponent input = im.get(entity);

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

            if(weapon != null && input != null) {
                int rotation;
                switch (input.direction) {
                    case SOUTH:
                        rotation = 90;
                        break;
                    case EAST:
                        rotation = 180;
                        break;
                    case WEST:
                        rotation = 0;
                        break;
                    default:
                        rotation = -90;
                        break;
                }
                weapon.weapon.sprite.setRotation(rotation);
                if (weapon.weapon.getClass().equals(Bow.class)) {
                    ((Bow) weapon.weapon).arrowSprite.setRotation(rotation);
                    ((Bow) weapon.weapon).arrowSprite.setOriginCenter();
                }

                weapon.weapon.sprite.visible = input.weaponDrawn;

                // Update Bow and Arrows
                if (weapon.weapon.getClass().equals(Bow.class)) {
                    Bow bow = (Bow) weapon.weapon;
                    bow.updateArrows(Gdx.graphics.getDeltaTime());
                }
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
