package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkTransmissionComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<Object> networkMessagesToProcess = new Array<Object>();
    public int lastTickId = 0;

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
