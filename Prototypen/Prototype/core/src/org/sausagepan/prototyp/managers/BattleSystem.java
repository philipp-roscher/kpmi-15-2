package org.sausagepan.prototyp.managers;

import java.util.Random;

import org.sausagepan.prototyp.enums.ItemType;
import org.sausagepan.prototyp.model.ServerSettings;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.SERVERNetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.entities.MonsterEntity;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;
import org.sausagepan.prototyp.model.items.Bow;
import org.sausagepan.prototyp.model.items.MapItem;
import org.sausagepan.prototyp.model.items.Sword;
import org.sausagepan.prototyp.network.Network.AttackResponse;
import org.sausagepan.prototyp.network.Network.DeleteBulletResponse;
import org.sausagepan.prototyp.network.Network.HPUpdateResponse;
import org.sausagepan.prototyp.network.Network.NewItem;
import org.sausagepan.prototyp.network.Network.ShootResponse;
import org.sausagepan.prototyp.network.Network.YouDiedResponse;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Takes all {@link Entity}s capable of joining the battle and process their actions against each
 * other.
 * Created by Georg on 26.06.2015.
 */
public class BattleSystem extends EntitySystem implements EntityListener {


    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> attackers;
    private ImmutableArray<Entity> victims;
    private SERVERNetworkTransmissionComponent ntc;
    private int maxBulletId;
    private SERVEREntityComponentSystem ECS;

    private ComponentMapper<HealthComponent> hm
            = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<DynamicBodyComponent> dm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<InjurableAreaComponent> jm
            = ComponentMapper.getFor(InjurableAreaComponent.class);
    private ComponentMapper<TeamComponent> tm
            = ComponentMapper.getFor(TeamComponent.class);

    /* .......................................................................... CONSTRUCTORS .. */
    public BattleSystem(SERVEREntityComponentSystem ECS) {
        this.maxBulletId = 1;
        this.ECS = ECS;
        ntc = ECS.getSNTC();
    }

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        attackers = engine.getEntitiesFor(EntityFamilies.attackerFamily);
        victims = engine.getEntitiesFor(EntityFamilies.victimFamily);
    }

    public void update(float deltaTime) {
        for (Entity attacker : attackers) {
            WeaponComponent weapon = wm.get(attacker);
            DynamicBodyComponent body = dm.get(attacker);
            long currentTime = TimeUtils.millis();

            // Update Bow and Arrows
            if (weapon.weapon.getClass().equals(Bow.class)) {
                Bow bow = (Bow) weapon.weapon;
                bow.updateArrows(deltaTime);
            }

            if (weapon.weapon.getClass().equals(Sword.class) && weapon.weapon.justUsed && (currentTime - weapon.weapon.lastAttack > weapon.weapon.cooldown))
                ntc.networkMessagesToProcess.add(new AttackResponse(attacker.getComponent(IdComponent.class).id, true));

            // Check victims for damage
            for (Entity v : victims) {
                if (!attacker.equals(v)) {
                    HealthComponent health = hm.get(v);
                    InjurableAreaComponent area = jm.get(v);
                    // check if weapon was used and not on cooldown
                    if (weapon.weapon.justUsed && (currentTime - weapon.weapon.lastAttack > weapon.weapon.cooldown)) {
                        // If weapon area and injurable area of character overlap

                        // Handle Sword
                        if (weapon.weapon.getClass().equals(Sword.class))
                            if (((Sword) weapon.weapon).checkHit(area.area))
                            	calculateDamage(weapon, health, v, attacker, -1);

                        // Shoot Bow, check bullets later
                        if (weapon.weapon.getClass().equals(Bow.class)) {
                            Bow bow = (Bow) weapon.weapon;
                            bow.shoot(body.dynamicBody.getPosition(), body.direction, maxBulletId);
                            ntc.networkMessagesToProcess.add(new ShootResponse(attacker.getComponent(IdComponent.class).id, body.dynamicBody.getPosition(), body.direction, maxBulletId));
                            maxBulletId++;
                            weapon.weapon.justUsed = false; // usage over, waiting for next attack
                        }                        
                    }

                    // Check Bullets for hit
                    int res;
                    if (weapon.weapon.getClass().equals(Bow.class))
                        if ((res = ((Bow) weapon.weapon).checkHit(area.area)) != -1)
                            calculateDamage(weapon, health, v, attacker, res);
                }
            }
            
            if(weapon.weapon.justUsed && (currentTime - weapon.weapon.lastAttack > weapon.weapon.cooldown)) {
            	weapon.weapon.lastAttack = currentTime;
            	weapon.weapon.justUsed = false; // usage over, waiting for next attack
            }
        }

        // Check if someone has died
        for(Entity v : victims) {
        	HealthComponent health = hm.get(v);
        	if(health.HP == 0) {
        		if(v.getClass().equals(MonsterEntity.class)) {
					Random r = new Random();

					// Generate potion with a chance of 25%
					if (r.nextFloat() < 0.25) {
						DynamicBodyComponent body = dm.get(v);
						MapItem mapItem = new MapItem(body.dynamicBody.getPosition(), ItemType.POTION_HP, 1);
						int id = ECS.createItem(mapItem);
						NewItem newItem = new NewItem(id, mapItem);
						ntc.networkMessagesToProcess.add(newItem);
					}

        			// Remove monsters
        			ECS.deleteMonster(v.getComponent(IdComponent.class).id);        			
        		} else {
        			DynamicBodyComponent body = dm.get(v);

        			// Drop his keys
        			this.getEngine().getSystem(ItemSystem.class).dropItems(v.getComponent(IdComponent.class).id);
        			
        			// Reset human player to starting position, refill his health bar
        			body.dynamicBody.setTransform(new Vector2(0,0), 0f);
        			body.dynamicBody.setLinearVelocity(0f, 0f);
        			health.HP = health.initialHP;
        			
        			// mark character as dead so that he can't move, prepare network messages
        			v.add(new IsDeadComponent(System.currentTimeMillis(), 5000));
        			ntc.networkMessagesToProcess.add(new YouDiedResponse(v.getComponent(IdComponent.class).id));
        			ntc.networkMessagesToProcess.add(new HPUpdateResponse(v.getComponent(IdComponent.class).id, true, health.HP));
        		}
        	}
        }
    }

    public void calculateDamage(WeaponComponent weapon, HealthComponent health, Entity victim, Entity attacker, int bulletId) {
        // delete bullet if it hit
        if(bulletId != -1)
            ntc.networkMessagesToProcess.add(new DeleteBulletResponse(attacker.getComponent(IdComponent.class).id, bulletId));
        
        // no damage if they are in the same team and friendly fire is turned off
    	if(!ServerSettings.FRIENDLY_FIRE && (tm.get(attacker).TeamId == tm.get(victim).TeamId))
        	return;
    	
    	if(health.HP - weapon.weapon.strength > 0) health.HP -= weapon.weapon.strength;
        else health.HP = 0;

    	// send HPUpdate unless a human character died (separate handling above)
        if(health.HP != 0 || victim.getClass().equals(MonsterEntity.class))
        	ntc.networkMessagesToProcess.add(new HPUpdateResponse(victim.getComponent(IdComponent.class).id, victim.getClass().equals(ServerCharacterEntity.class), health.HP));
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
