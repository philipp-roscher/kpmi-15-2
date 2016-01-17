package org.sausagepan.prototyp.model.entities;

import org.sausagepan.prototyp.enums.CharacterClass;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by georg on 19.11.15.
 */
public class MapCharacterObject extends MapFactoryObject {
    /* ............................................................................ ATTRIBUTES .. */
    
	public CharacterClass characterClass;
	public int teamId;
    public int health = -1;
    public String weaponName;
    public boolean[] ownKeys;
    
    /* ........................................................................... CONSTRUCTOR .. */

    public MapCharacterObject(Vector2 position, CharacterClass characterClass, int teamId, int health, String weaponName, boolean[] ownKeys) {
        super(position);
        this.characterClass = characterClass;
        this.teamId = teamId;
        this.health = health;
        this.weaponName = weaponName;
        this.ownKeys = ownKeys;
    }
    
    public MapCharacterObject() { }
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
