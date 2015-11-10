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
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.Sword;
import org.sausagepan.prototyp.network.Network.HPUpdateRequest;

/**
 * Takes all {@link Entity}s capable of joining the battle and process their actions against each
 * other.
 * Created by Georg on 26.06.2015.
 */
public class BattleSystem extends ObservingEntitySystem {


    /* ............................................................................ ATTRIBUTES .. */
    private ObservableEngine engine;

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
    private ComponentMapper<CharacterSpriteComponent> sm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    private ComponentMapper<NetworkComponent> nm
    		= ComponentMapper.getFor(NetworkComponent.class);

    /* .......................................................................... CONSTRUCTORS .. */
    public BattleSystem(ObservableEngine engine) {this.engine = engine;}

    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) {
    attackers = engine.getEntitiesFor(Family.all(
            HealthComponent.class,
            DynamicBodyComponent.class,
            WeaponComponent.class,
            NetworkComponent.class,
            InjurableAreaComponent.class).get());
    victims = engine.getEntitiesFor(Family.all(
            HealthComponent.class,
            InjurableAreaComponent.class,
            CharacterSpriteComponent.class).get());
}

    public void update(float deltaTime) {
        for (Entity attacker : attackers) {
            WeaponComponent weapon = wm.get(attacker);
            DynamicBodyComponent body = dm.get(attacker);
            NetworkComponent network = nm.get(attacker);

            // Check victims for damage
            for(Entity v : victims) {
                if(!attacker.equals(v)) {
                    HealthComponent health = hm.get(v);
                    InjurableAreaComponent area = jm.get(v);
                    if(weapon.weapon.justUsed) {
                        // If weapon area and injurable area of character overlap

                        // Handle Sword
                        if(weapon.weapon.getClass().equals(Sword.class))
                            if (((Sword)weapon.weapon).checkHit(area.area))
                                calculateDamage(weapon, health, v, network);

                        // Handle Bow
                        if(weapon.weapon.getClass().equals(Bow.class)) {
                            Bow bow = (Bow)weapon.weapon;
                            bow.shoot(body.dynamicBody.getPosition(),body.direction);
                            network.shoot(body.dynamicBody.getPosition(),body.direction);
                            weapon.weapon.justUsed = false; // usage over, waiting for next attack
                        }
                    }

                    // If weapon is a bow
                    if(weapon.weapon.getClass().equals(Bow.class))
                        if(((Bow)weapon.weapon).checkHit(area.area))
                            calculateDamage(weapon, health, v, network);
                }

                if(v.getClass().equals(MonsterEntity.class) && hm.get(v).HP == 0) {
                    sm.get(v).sprite.rotate(90);
                    sm.get(v).sprite.setOriginCenter();
                    engine.removeEntity(v);
                    System.out.println("Monster killed");
                }

            }
            weapon.weapon.justUsed = false; // usage over, waiting for next attack
        }
    }

    public void calculateDamage(WeaponComponent weapon, HealthComponent health, Entity victim, NetworkComponent network) {
        if(health.HP - weapon.weapon.strength > 0) health.HP -= weapon.weapon.strength;
        else health.HP = 0;
        
        if(victim.getClass().equals(CharacterEntity.class))
        	network.sendHPUpdate(new HPUpdateRequest(victim.getComponent(IdComponent.class).id, health.HP));
    }

    /* ..................................................................... GETTERS & SETTERS .. */


}
