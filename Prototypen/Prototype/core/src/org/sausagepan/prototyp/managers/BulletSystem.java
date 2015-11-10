package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

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
public class BulletSystem extends ObservingEntitySystem {


    /* ............................................................................ ATTRIBUTES .. */
    private ObservableEngine engine;
    private Maze maze;

    private ImmutableArray<Entity> entities;

    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);

    BulletSystem(ObservableEngine engine, Maze maze) {
        this.engine = engine;
        this.maze = maze;
    }

    /* ............................................................................... METHODS .. */

    public void addedToEngine(ObservableEngine engine) {
        entities = engine.getEntitiesFor(Family.all(WeaponComponent.class).get());
    }

    public void update(float deltaTime) {
        MapObjects mo = maze.getColliders();
        Rectangle r;
        for(Entity e : entities) {
            WeaponComponent weapon = wm.get(e);
            // Handle Bow
            if(weapon.weapon.getClass().equals(Bow.class)) {
                Bow bow = (Bow)weapon.weapon;
                for(MapObject m : mo) {
                    r = ((RectangleMapObject) m).getRectangle();
                    bow.checkHit(new Rectangle(r.x/32f, r.y/32f, r.width/32f, r.height/32f));
                }
            }
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */


}