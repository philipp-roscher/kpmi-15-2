package org.sausagepan.prototyp.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.enums.DAMAGETYPE;
import org.sausagepan.prototyp.enums.WEAPONTYPE;
import org.sausagepan.prototyp.model.components.PlayerComponent;

/**
 * Created by Georg on 26.06.2015.
 */
public class Weapon {

    /* ................................................................................................ ATTRIBUTES .. */

    private String name;

    private int        damage;
    private WEAPONTYPE type;
    private DAMAGETYPE damageType;
    private float      range;
    private float      angle;
    private Vector2    direction = new Vector2(0,0);
    private Rectangle  collider;


    /* .............................................................................................. CONSTRUCTORS .. */

    /**
     * Returns an instance of a weapon with the given properties
     * @param name          the weapons name
     * @param damage        damage this weapon causes
     * @param type          weapons type: WEPONTYPE.{SWORD, AXE, BOW, CANE}
     * @param damageType    DAMAGETYPE.{PHYSICAL, MAGICAL, HEAL}
     * @param range         weapons range in which others can get hit
     * @param angle         the weapons angle in the aiming direction
     */
    public Weapon(String name, int damage, WEAPONTYPE type, DAMAGETYPE damageType, float range, float angle) {
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.damageType = damageType;
        this.range = range;
        this.angle = angle;
        this.collider = new Rectangle(0,0,range,range);
    }
    
    public Weapon() {
    	this(
                "standard_sword",
                3,
                WEAPONTYPE.SWORD,
                DAMAGETYPE.PHYSICAL,
                UnitConverter.pixelsToMeters(20),
                180);
    }

    /* ................................................................................................... METHODS .. */

    public void update(float elapsedTime) {
        // TODO
    }

    /* .......................................................................................... GETTERS & SETTERS . */

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public WEAPONTYPE getType() {
        return type;
    }

    public DAMAGETYPE getDamageType() {
        return damageType;
    }

    public float getRange() {
        return range;
    }

    public float getAngle() {
        return angle;
    }

    public void setDirection(Vector2 direction) {
        this.direction.x = direction.x;
        this.direction.y = direction.y;
    }

    public Vector2 getDirection() {
        return this.direction;
    }

    public Rectangle getCollider() {
        return collider;
    }
}
