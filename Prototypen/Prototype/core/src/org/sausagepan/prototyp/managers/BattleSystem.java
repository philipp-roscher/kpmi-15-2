package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.MagicComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.ServerNetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.Sword;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.DeleteBulletResponse;
import org.sausagepan.prototyp.network.Network.ShootResponse;

/**
 * Takes all {@link Entity}s capable of joining the battle and process their actions against each
 * other.
 * Created by Georg on 26.06.2015.
 */
public class BattleSystem extends EntitySystem implements EntityListener {


    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> attackers;
    private ImmutableArray<Entity> victims;
    private ServerNetworkTransmissionComponent ntc;
    private int maxBulletId;

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
    private ComponentMapper<NetworkTransmissionComponent> ntm
    		= ComponentMapper.getFor(NetworkTransmissionComponent.class);

    /* .......................................................................... CONSTRUCTORS .. */
    public BattleSystem() {
        maxBulletId = 1;
    }

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        attackers = engine.getEntitiesFor(EntityFamilies.attackerFamily);
        victims = engine.getEntitiesFor(EntityFamilies.victimFamily);
        ntc = engine.getEntitiesFor(Family.all(ServerNetworkTransmissionComponent.class).get()).
                get(0).
                getComponent(ServerNetworkTransmissionComponent.class);
    }

    public void update(float deltaTime) {
        for (Entity attacker : attackers) {
            WeaponComponent weapon = wm.get(attacker);
            DynamicBodyComponent body = dm.get(attacker);

            // Update Bow and Arrows
            if (weapon.weapon.getClass().equals(Bow.class)) {
                Bow bow = (Bow) weapon.weapon;
                bow.updateArrows(deltaTime);
            }

            if	(weapon.weapon.getClass().equals(Sword.class) && weapon.weapon.justUsed)
            	ntc.networkMessagesToProcess.add(new AttackResponse(attacker.getComponent(IdComponent.class).id, true));
            
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
                                calculateDamage(weapon, health, v, null, -1);

                        // Handle Bow
                        if(weapon.weapon.getClass().equals(Bow.class)) {
                            Bow bow = (Bow)weapon.weapon;
                            bow.shoot(body.dynamicBody.getPosition(), body.direction, maxBulletId);
                            ntc.networkMessagesToProcess.add(new ShootResponse(attacker.getComponent(IdComponent.class).id, body.dynamicBody.getPosition(), body.direction, maxBulletId));
                            maxBulletId++;
                            weapon.weapon.justUsed = false; // usage over, waiting for next attack
                        }
                    }

                    // Check Bullets for hit
                    int res;
                    if (weapon.weapon.getClass().equals(Bow.class))
                        if((res = ((Bow)weapon.weapon).checkHit(area.area)) != -1)
                            calculateDamage(weapon, health, v, attacker, res);
                }

                if(v.getClass().equals(MonsterEntity.class) && hm.get(v).HP == 0) {
                    sm.get(v).sprite.rotate(90);
                    sm.get(v).sprite.setOriginCenter();
                    this.getEngine().removeEntity(v);
                    System.out.println("Monster killed");
                }

            }
            weapon.weapon.justUsed = false; // usage over, waiting for next attack
        }
    }

    public void calculateDamage(WeaponComponent weapon, HealthComponent health, Entity victim, Entity attacker, int bulletId) {
        if(health.HP - weapon.weapon.strength > 0) health.HP -= weapon.weapon.strength;
        else health.HP = 0;

        ntc.networkMessagesToProcess.add(new HPUpdateResponse(victim.getComponent(IdComponent.class).id, victim.getClass().equals(ServerCharacterEntity.class), health.HP));
        if(attacker != null && bulletId != -1)
            ntc.networkMessagesToProcess.add(new DeleteBulletResponse(attacker.getComponent(IdComponent.class).id, bulletId));
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
