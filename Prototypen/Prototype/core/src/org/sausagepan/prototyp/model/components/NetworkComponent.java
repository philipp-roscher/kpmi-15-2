package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.network.Network.AttackRequest;
import org.sausagepan.prototyp.network.Network.HPUpdateRequest;
import org.sausagepan.prototyp.network.Network.LoseKeyRequest;
import org.sausagepan.prototyp.network.Network.ShootRequest;
import org.sausagepan.prototyp.network.Network.TakeKeyRequest;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;

/**
 * Created by philipp on 06.11.15.
 */
public class NetworkComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Client client;
    public int id;

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
