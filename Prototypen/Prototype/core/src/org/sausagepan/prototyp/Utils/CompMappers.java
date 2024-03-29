package org.sausagepan.prototyp.Utils;

import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.ChaseComponent;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.LightComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.NetworkTransmissionComponent;
import org.sausagepan.prototyp.model.components.SensorComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

import com.badlogic.ashley.core.ComponentMapper;

/**
 * Created by georg on 15.12.15.
 */
public abstract class CompMappers {
    /* ............................................................................ ATTRIBUTES .. */
    public static ComponentMapper<SensorComponent> sensor = ComponentMapper.getFor(SensorComponent
            .class);
    public static ComponentMapper<DynamicBodyComponent> dynBody
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    public static ComponentMapper<CharacterSpriteComponent> charSprite
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    public static ComponentMapper<WeaponComponent> weapon
            = ComponentMapper.getFor(WeaponComponent.class);
    public static ComponentMapper<LightComponent> light
            = ComponentMapper.getFor(LightComponent.class);
    public static ComponentMapper<IdComponent> id
    		= ComponentMapper.getFor(IdComponent.class);
    public static ComponentMapper<InputComponent> input
            = ComponentMapper.getFor(InputComponent.class);
    public static ComponentMapper<InjurableAreaComponent> injurableArea
            = ComponentMapper.getFor(InjurableAreaComponent.class);
    public static ComponentMapper<InventoryComponent> inventory
            = ComponentMapper.getFor(InventoryComponent.class);
    public static ComponentMapper<ChaseComponent> chase
            = ComponentMapper.getFor(ChaseComponent.class);
    public static ComponentMapper<NetworkComponent> network
    		= ComponentMapper.getFor(NetworkComponent.class);
    public static ComponentMapper<NetworkTransmissionComponent> netTrans
            = ComponentMapper.getFor(NetworkTransmissionComponent.class);
    public static ComponentMapper<HealthComponent> health
            = ComponentMapper.getFor(HealthComponent.class);
    public static ComponentMapper<TeamComponent> team
    = ComponentMapper.getFor(TeamComponent.class);
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
