package org.sausagepan.prototyp.managers;

import com.badlogic.gdx.utils.Array;
import org.sausagepan.prototyp.model.Character;

/**
 * Created by Georg on 26.06.2015.
 */
public class CharacterManager {

    /* ................................................................................................ ATTRIBUTES .. */

    public Array<Character> characters;


    /* .............................................................................................. CONSTRUCTORS .. */

    public CharacterManager() {
        this.characters = new Array<Character>();
    }


    /* ................................................................................................... METHODS .. */




    /**
     * adds a character to the system
     * @param character
     */
    public void addCharacter(Character character) {
        this.characters.add(character);
    }


    /* .......................................................................................... GETTERS & SETTERS . */

    public Array<Character> getCharacters() {
        return characters;
    }
}
