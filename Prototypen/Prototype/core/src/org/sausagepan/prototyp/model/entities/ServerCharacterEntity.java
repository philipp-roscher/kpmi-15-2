package org.sausagepan.prototyp.model.entities;

/**
 * Server characters must not have an
 * {@link org.sausagepan.prototyp.model.components.InputComponent} as this would cause the
 * {@link org.sausagepan.prototyp.managers.InputSystem} to process them
 */
public class ServerCharacterEntity extends CharacterEntity {
	private int ID;

    public ServerCharacterEntity(int ID) {
        super();
        this.ID = ID;
    }
}
