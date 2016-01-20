package org.sausagepan.prototyp.model.entities;

import org.sausagepan.prototyp.model.components.CharacterClassComponent;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.ChaseComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.IsDeadComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.MonsterSpawnComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

import com.badlogic.ashley.core.Family;

/**
 * Created by georg on 19.11.15.
 */
public class EntityFamilies {
    /* ............................................................................ ATTRIBUTES .. */
    @SuppressWarnings("unchecked")
    public static Family spriteFamily = Family.one(
            SpriteComponent.class,
            WeaponComponent.class,
            CharacterSpriteComponent.class
    ).get();
    
    @SuppressWarnings("unchecked")
    public static Family characterFamily = Family.all(
    		IdComponent.class,
    		TeamComponent.class,
            DynamicBodyComponent.class,
    		CharacterClassComponent.class,
    		InputComponent.class,
    		LightComponent.class,
    		HealthComponent.class,
    		InventoryComponent.class,
    		CharacterSpriteComponent.class,
    		WeaponComponent.class,
    		InjurableAreaComponent.class    		
	).get();
    
    @SuppressWarnings("unchecked")
    public static Family serverCharacterFamily = Family.all(
    		CharacterClassComponent.class,
    		IdComponent.class,
    		TeamComponent.class,
            DynamicBodyComponent.class,
    		InputComponent.class,
    		HealthComponent.class,
    		InventoryComponent.class,
    		WeaponComponent.class,
    		InjurableAreaComponent.class    		
	).get();

    @SuppressWarnings("unchecked")
    public static Family monsterFamily = Family.all(
            DynamicBodyComponent.class,
            InjurableAreaComponent.class,
            IdComponent.class,
            TeamComponent.class,
            HealthComponent.class,
            CharacterClassComponent.class
    ).exclude(
    		InventoryComponent.class
	).get();

    @SuppressWarnings("unchecked")
    public static Family monsterMovementFamily = Family.all(
            DynamicBodyComponent.class,
            InjurableAreaComponent.class,
            IdComponent.class,
            TeamComponent.class,
            HealthComponent.class,
            CharacterClassComponent.class
    ).exclude(
    		InventoryComponent.class,
            ChaseComponent.class
	).get();
    
    @SuppressWarnings("unchecked")
    public static Family itemFamily = Family.all(
            ItemComponent.class,
            SpriteComponent.class,
            InjurableAreaComponent.class,
            IdComponent.class
    ).get();

    @SuppressWarnings("unchecked")
    public static Family attackerFamily = Family.all(
            HealthComponent.class,
            DynamicBodyComponent.class,
            WeaponComponent.class,
            InjurableAreaComponent.class,
            TeamComponent.class
	).exclude(
			IsDeadComponent.class
	).
	get();

    @SuppressWarnings("unchecked")
    public static Family victimFamily = Family.all(
            HealthComponent.class,
            InjurableAreaComponent.class
    ).get();

    @SuppressWarnings("unchecked")
    public static Family gameMasterFamily = Family.all(
            MonsterSpawnComponent.class).get();

    @SuppressWarnings("unchecked")
    public static Family positionSynchroFamily = Family.all(
            DynamicBodyComponent.class).one(
            LightComponent.class,
            CharacterSpriteComponent.class,
            WeaponComponent.class,
            InputComponent.class,
            InjurableAreaComponent.class
    ).get();
    
    @SuppressWarnings("unchecked")
    public static Family ownCharacterFamily = Family.all(
    		NetworkComponent.class,
    		NetworkTransmissionComponent.class
    ).get();

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
