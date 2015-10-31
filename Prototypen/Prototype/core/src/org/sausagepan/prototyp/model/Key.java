package org.sausagepan.prototyp.model;
import org.sausagepan.prototyp.enums.KeyPart;

/**
 * Created by Bettina on 31.10.2015.
 */
public class Key{

    private KeyPart keyPart;

    public Key(KeyPart keyPart) {
        this.keyPart = keyPart;
    }

    public KeyPart getKeyPart()
    {
        return this.keyPart;
    }
}
