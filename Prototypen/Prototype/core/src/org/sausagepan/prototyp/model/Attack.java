package org.sausagepan.prototyp.model;

import com.badlogic.gdx.math.Rectangle;
import org.sausagepan.prototyp.enums.Damagetype;

/**
 * Created by Georg on 06.07.2015.
 *
 * Container class for attacks submitted from attacker to victim
 */
public class Attack {

    /* ................................................................................................ ATTRIBUTES .. */

    private Damagetype type;
    private int intensity;
    private Rectangle  damageArea;



    /* .............................................................................................. CONSTRUCTORS .. */

    public Attack(Damagetype type, int intensity, Rectangle damageArea) {
        this.type = type;
        this.intensity = intensity;
        this.damageArea = damageArea;
    }

    /* ................................................................................................... METHODS .. */
    
    /* ......................................................................................... GETTERS & SETTERS .. */

    public Damagetype getType() {
        return type;
    }

    public int getIntensity() {
        return intensity;
    }

    public Rectangle getDamageArea() {
        return damageArea;
    }
}
