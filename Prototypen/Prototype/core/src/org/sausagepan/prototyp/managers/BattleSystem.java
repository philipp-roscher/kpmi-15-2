package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Iterator;

import org.sausagepan.prototyp.model.*;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;

/**
 * Takes all {@link Entity}s capable of joining the battle and process their actions against each
 * other.
 * Created by Georg on 26.06.2015.
 */
public class BattleSystem extends EntitySystem {


    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> attackers;
    private ImmutableArray<Entity> victims;

    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<MagicComponent> mm
            = ComponentMapper.getFor(MagicComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<InjurableAreaComponent> jm
            = ComponentMapper.getFor(InjurableAreaComponent.class);

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
    attackers = engine.getEntitiesFor(Family.all(
            HealthComponent.class,
            DynamicBodyComponent.class,
            WeaponComponent.class,
            InjurableAreaComponent.class).get());
    victims = engine.getEntitiesFor(Family.all(
            HealthComponent.class,
            InjurableAreaComponent.class).get());
}

    public void update(float deltaTime) {
        for (Entity attacker : attackers) {
            WeaponComponent weapon = wm.get(attacker);
            // If weapon just has been used
            if(weapon.justUsed) {
                for(Entity v : victims) {
                    if(!attacker.equals(v)) {
                        HealthComponent health = hm.get(v);
                        InjurableAreaComponent area = jm.get(v);
                        System.out.println(
                                "Checking for hit: \n" +
                                "Attacker: " + weapon.damageArea
                                + "\nVictim: " + area.area);
                        if (area.area.overlaps(weapon.damageArea)) {
                            System.out.println("Processing Attack");
                            if(health.HP - weapon.strength > 0)
                                health.HP -= weapon.strength;
                            else
                                health.HP = 0;
                        }
                    }
                }
                weapon.justUsed = false; // usage over, waiting for next attack
            }
        }
        // Remove Entity from the system, wenn killed
        // TODO
        // remember to implement EntityListener, so Systems can react to deletion
    }

    /* ..................................................................... GETTERS & SETTERS .. */


}
