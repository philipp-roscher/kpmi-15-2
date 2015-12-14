 package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.items.Bow;

/**
 * Turns and refreshes characters sprite.
 * Created by georg on 28.10.15.
 */
public class CharacterSpriteSystem extends EntitySystem implements EntityListener {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime=0;
    private EntityComponentSystem ECS;

    private ComponentMapper<CharacterSpriteComponent> cm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    private ComponentMapper<CharacterSpriteComponent> sm
    		= ComponentMapper.getFor(CharacterSpriteComponent.class);
    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<IsDeadComponent> isdm
    		= ComponentMapper.getFor(IsDeadComponent.class);

    /* ........................................................................... CONSTRUCTOR .. */
    public CharacterSpriteSystem(EntityComponentSystem ECS) {
    	this.ECS = ECS;
    }
    /* ............................................................................... METHODS .. */
    @SuppressWarnings("unchecked")
    public void addedToEngine(Engine engine) {
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

            // Rotate monster sprites and remove them if their health drops to 0
            if(entity.getClass().equals(MonsterEntity.class) && hm.get(entity).HP == 0) {
            	IsDeadComponent isDead = isdm.get(entity);
                if(isDead == null) {
                	// rotate and remove body
                	sm.get(entity).sprite.rotate(90);
	                sm.get(entity).sprite.setOriginCenter();
	                entity.add(new IsDeadComponent(System.currentTimeMillis(), 2000));
	                ECS.deleteMonster(entity.getComponent(IdComponent.class).id, false);
                } else {
                	// completely delete entity after 2 seconds 
                	if ((System.currentTimeMillis() - isDead.deathTime) > isDead.deathLength) {
    	                ECS.deleteMonster(entity.getComponent(IdComponent.class).id, true);
                	}
                }
            }

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
    
    @Override
    public void entityAdded(Entity entity) {
        addedToEngine(this.getEngine());
    }

    @Override
    public void entityRemoved(Entity entity) {
        addedToEngine(this.getEngine());
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
