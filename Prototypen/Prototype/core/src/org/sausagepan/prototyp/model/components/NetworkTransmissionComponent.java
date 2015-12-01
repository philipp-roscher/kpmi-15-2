package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.network.Network.HPUpdateRequest;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

/**
 * Created by georg on 29.10.15.
 */
public class NetworkTransmissionComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public boolean stopAttacking = false;
    public boolean shoot = false;
    public Array<Integer> takeKey = new Array<Integer>();
    public Array<Integer> loseKey = new Array<Integer>();
    public Array<HPUpdateRequest> HPUpdates = new Array<HPUpdateRequest>();

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
