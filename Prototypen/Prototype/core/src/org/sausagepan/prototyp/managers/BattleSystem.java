package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;

import org.sausagepan.prototyp.model.*;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Takes all {@link Entity}s capable of joining the battle and process their actions against each
 * other.
 * Created by Georg on 26.06.2015.
 */
public class BattleSystem extends EntitySystem {


    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;

    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<MagicComponent> mm
            = ComponentMapper.getFor(MagicComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);

    /* .......................................................................... CONSTRUCTORS .. */
    public BattleSystem() {}

    /* ............................................................................... METHODS .. */

//    public void updateAttack(Player attacker, ArrayList<Player> characters) {
//        for(Player c : characters) {
//            if(!attacker.equals(c)) {
//                if (attacker.getWeapon().getCollider().overlaps(c.getDamageCollider()))
//                    c.getStatus_().doPhysicalHarm(
//                            attacker.getWeapon().getDamage()
//                                    + attacker.getStatus_().getAttPhys());
//
//            }
//        }
//    }

//    public void updateBullets(Player attacker, ArrayList<Player> characters) {
//        for(Player c : characters) {
//            if(!attacker.equals(c))
//                for(Bullet b : attacker.getBullets())
//                    if(b.overlaps(c.getDamageCollider())) c.getStatus_().
//                            doPhysicalHarm(attacker.getWeapon().getDamage()
//                            + attacker.getStatus_().getAttPhys());
//        }
//    }
public void addedToEngine(Engine engine) {
    entities = engine.getEntitiesFor(Family.one(HealthComponent.class, MagicComponent.class).get());
}

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            HealthComponent health = hm.get(entity);
            MagicComponent  magic  = mm.get(entity);
        }
    }

    /* .......................................................................................... GETTERS & SETTERS . */


}
