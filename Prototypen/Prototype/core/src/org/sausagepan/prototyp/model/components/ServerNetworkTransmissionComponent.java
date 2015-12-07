package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

/**
 * Created by philipp on 07.12.15.
 * Component that is used for communication between systems,
 * holds a list of network messages which are to be processed by the ServerNetworkSystem
 */
public class ServerNetworkTransmissionComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */

    public Array<Object> networkMessagesToProcess = new Array<Object>();

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
