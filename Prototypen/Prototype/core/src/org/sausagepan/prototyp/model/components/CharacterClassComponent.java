package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.enums.CharacterClass;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by philipp on 05.12.15.
 */
public class CharacterClassComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public CharacterClass characterClass;
    /* ........................................................................... CONSTRUCTOR .. */
    public CharacterClassComponent (CharacterClass characterClass) {
    	this.characterClass = characterClass;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
