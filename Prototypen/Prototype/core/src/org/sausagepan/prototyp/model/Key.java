package org.sausagepan.prototyp.model;

import org.sausagepan.prototyp.enums.KeySection;

/**
 * Created by Bettina on 02.11.2015.
 */
public class Key {

    private KeySection keySection;

    public Key(KeySection keySection)
    {
        this.keySection = keySection;
    }

    public KeySection getKeySection()
    {
        return this.keySection;
    }
}
