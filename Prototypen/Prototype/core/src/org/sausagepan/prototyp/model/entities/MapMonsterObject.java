package org.sausagepan.prototyp.model.entities;

import com.badlogic.gdx.math.Vector2;

import org.sausagepan.prototyp.enums.CharacterClass;

/**
 * Created by georg on 19.11.15.
 */
public class MapMonsterObject extends MapFactoryObject {
    public CharacterClass characterClass;

    public MapMonsterObject(Vector2 position, CharacterClass characterClass) {
        super(position);
        this.characterClass = characterClass;
    }
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
