package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import org.sausagepan.prototyp.Utils.UnitConverter;
import org.sausagepan.prototyp.enums.Damagetype;
import org.sausagepan.prototyp.graphics.EntitySprite;
import org.sausagepan.prototyp.model.items.WeaponItem;

/**
 * Created by georg on 22.10.15.
 */
public class WeaponComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public WeaponItem weapon;   // the weapon itself

    /* ........................................................................... CONSTRUCTOR .. */
    public WeaponComponent(WeaponItem weapon) {
        this.weapon = weapon;
    }

    /* ............................................................................... METHODS .. */

    /* ..................................................................... GETTERS & SETTERS .. */
}
