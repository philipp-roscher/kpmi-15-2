package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.Weapon;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.items.Bow;

/**
 * Created by georg on 22.10.15.
 */
public class WeaponSystem extends ObservingEntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    /* ........................................................................... CONSTRUCTOR .. */


    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);

    public WeaponSystem() {}

    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(
                WeaponComponent.class,
                InputComponent.class,
                DynamicBodyComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            WeaponComponent weapon = wm.get(entity);
            InputComponent input = im.get(entity);
            DynamicBodyComponent body = dm.get(entity);
            int rotation;
            switch(input.direction) {
                case SOUTH: rotation = 90;  break;
                case EAST:  rotation = 180; break;
                case WEST:  rotation = 0;   break;
                default:    rotation = -90; break;
            }
            weapon.weapon.sprite.setRotation(rotation);
            if(weapon.weapon.getClass().equals(Bow.class)) {
                ((Bow) weapon.weapon).arrowSprite.setRotation(rotation);
                ((Bow) weapon.weapon).arrowSprite.setOriginCenter();
            }
            if(!input.weaponDrawn) weapon.weapon.sprite.visible = false;
            else                 weapon.weapon.sprite.visible = true;

            // Update Bow and Arrows
            if(weapon.weapon.getClass().equals(Bow.class)) {
                Bow bow = (Bow)weapon.weapon;
                bow.updateArrows();
            }
        }
    }

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
