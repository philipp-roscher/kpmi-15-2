package org.sausagepan.prototyp.Utils;

import org.sausagepan.prototyp.model.GlobalSettings;

/**
 * Created by Georg on 14.07.2015.
 */
public class UnitConverter {

    /* ................................................................................................ ATTRIBUTES .. */

    /* .............................................................................................. CONSTRUCTORS .. */

    private UnitConverter() {

    }

    /* ................................................................................................... METHODS .. */

    public static float pixelsToMeters(float pixels) {
        return pixels/ GlobalSettings.PIXELS_PER_METER;
    }

    public static float metersToPixels(float meters) {
        return meters*GlobalSettings.PIXELS_PER_METER;
    }

    /* ......................................................................................... GETTERS & SETTERS .. */

}
