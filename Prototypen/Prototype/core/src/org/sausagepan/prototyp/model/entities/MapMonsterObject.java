package org.sausagepan.prototyp.model.entities;

import org.sausagepan.prototyp.enums.CharacterClass;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by georg on 19.11.15.
 */
public class MapMonsterObject extends MapFactoryObject {
    /* ............................................................................ ATTRIBUTES .. */
    
	public CharacterClass characterClass;
    public int health = -1;
    
    /* ........................................................................... CONSTRUCTOR .. */

    public MapMonsterObject(Vector2 position, CharacterClass characterClass, int health) {
        super(position);
        this.characterClass = characterClass;
        this.health = health;
    }
    
    public MapMonsterObject(Vector2 position, CharacterClass characterClass) {
        super(position);
        this.characterClass = characterClass;
    }
    
    public MapMonsterObject() { }
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
