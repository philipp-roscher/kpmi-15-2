package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.KPMIPrototype;

import com.badlogic.ashley.systems.IntervalSystem;

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
