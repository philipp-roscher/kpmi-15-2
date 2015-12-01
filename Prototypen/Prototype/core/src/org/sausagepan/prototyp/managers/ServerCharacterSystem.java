package org.sausagepan.prototyp.managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InputComponent;
import org.sausagepan.prototyp.model.entities.ServerCharacterEntity;
import org.sausagepan.prototyp.network.Network.NetworkPosition;

/**
 * Created by georg on 29.10.15.
 */
public class ServerCharacterSystem {
    /* ............................................................................ ATTRIBUTES .. */
    public HashMap<Integer, ServerCharacterEntity> characters;
    /* ........................................................................... CONSTRUCTOR .. */
    public ServerCharacterSystem() {
        this.characters = new HashMap<Integer, ServerCharacterEntity>();
    }
    /* ............................................................................... METHODS .. */
    /**
    * adds a character to the system
    * @param id
    * @param character
    */
    public void addCharacter(int id, ServerCharacterEntity character) {
        this.characters.put(id, character);
    }

    /**
     * removes a player from the system
     * @param characterID
     */
    public void removeCharacter(int characterID) {
        this.characters.remove(characterID);
    }

    /**
     * modifies position of currently existing player
     * @param ID
     * @param component
     */

    public void updatePosition(int ID, NetworkPosition component) {
        if(characters.get(ID) != null) {
            this.characters.get(ID).getComponent(DynamicBodyComponent.class).dynamicBody
                    .setTransform(component.position, 0f);
            this.characters.get(ID).getComponent(InputComponent.class).moving = component.moving;
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */
    public ArrayList<ServerCharacterEntity> getCharacters() {
        return new ArrayList<ServerCharacterEntity>(characters.values());
    }
}
