package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.model.items.WeaponItem;

import com.badlogic.ashley.core.Component;

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
