package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.model.items.Bow;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by philipp on 18.12.2015.
 * this system updates once per second, used for stuff that doesn't need to run every tick
 */
public class OneSecondSystem extends IntervalSystem {

    /* ............................................................................ ATTRIBUTES .. */

    private KPMIPrototype game;

    /* ........................................................................... CONSTRUCTOR .. */
    
    public OneSecondSystem(KPMIPrototype game) {
        super(1);
        this.game = game;
    }

    /* ............................................................................... METHODS .. */

    public void updateInterval() {
        game.client.updateReturnTripTime();
    }

    /* ..................................................................... GETTERS & SETTERS .. */

}
