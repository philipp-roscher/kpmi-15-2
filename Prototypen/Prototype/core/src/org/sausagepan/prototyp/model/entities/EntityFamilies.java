package org.sausagepan.prototyp.model.entities;

import com.badlogic.ashley.core.Family;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by georg on 19.11.15.
 */
public class EntityFamilies {
    /* ............................................................................ ATTRIBUTES .. */
    public static Family spriteFamily = Family.one(
            SpriteComponent.class,
            WeaponComponent.class,
            CharacterSpriteComponent.class).get();

    public static Family monsterFamily = Family.all(
            DynamicBodyComponent.class,
            SpriteComponent.class,
            HealthComponent.class,
            TeamComponent.class
    ).get();

    public static Family attackerFamily = Family.all(
            HealthComponent.class,
            DynamicBodyComponent.class,
            WeaponComponent.class,
            InjurableAreaComponent.class).get();

    public static Family victimFamily = Family.all(
            HealthComponent.class,
            InjurableAreaComponent.class).get();

    public static Family positionSynchroFamily = Family.all(
            DynamicBodyComponent.class).one(
            LightComponent.class,
            CharacterSpriteComponent.class,
            WeaponComponent.class,
            InputComponent.class,
            InjurableAreaComponent.class
    ).get();

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
