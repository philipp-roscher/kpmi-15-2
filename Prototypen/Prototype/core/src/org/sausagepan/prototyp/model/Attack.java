package org.sausagepan.prototyp.model;

import com.badlogic.gdx.math.Rectangle;
import org.sausagepan.prototyp.enums.DAMAGETYPE;

/**
 * Created by Georg on 06.07.2015.
 *
 * Container class for attacks submitted from attacker to victim
 */
public class Attack {

    /* ................................................................................................ ATTRIBUTES .. */

    private DAMAGETYPE type;
    private int intensity;
    private Rectangle  damageArea;



    /* .............................................................................................. CONSTRUCTORS .. */

    public Attack(DAMAGETYPE type, int intensity, Rectangle damageArea) {
        this.type = type;
        this.intensity = intensity;
        this.damageArea = damageArea;
    }

    /* ................................................................................................... METHODS .. */
    
    /* ......................................................................................... GETTERS & SETTERS .. */

    public DAMAGETYPE getType() {
        return type;
    }

    public int getIntensity() {
        return intensity;
    }

    public Rectangle getDamageArea() {
        return damageArea;
    }
}
