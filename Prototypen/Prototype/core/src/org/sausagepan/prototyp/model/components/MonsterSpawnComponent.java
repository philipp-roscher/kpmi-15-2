package org.sausagepan.prototyp.model.components;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.model.entities.MapMonsterObject;
import org.sausagepan.prototyp.model.GlobalSettings;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Sara on 15.12.15.
 */
public class MonsterSpawnComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    private int count;
    private MapMonsterObject mapMonsterObject;
    public boolean monsterSpawn;
    public Vector2 masterPosition;

    /* ........................................................................... CONSTRUCTOR .. */
    public MonsterSpawnComponent() {
        this.count = GlobalSettings.MONSTER_SPAWN_COUNT;
        this.monsterSpawn = false;
        this.masterPosition = new Vector2(0,0);
    }

    /*
    Creates random MapMonsterObjects so GM can spawn them
     */
    private MapMonsterObject createMonsters() {
        //make new Monster Object
        MapMonsterObject monster = new MapMonsterObject(
                new Vector2(masterPosition.x+1, masterPosition.y+1), CharacterClass.MONSTER_ZOMBIE );
        //monster.characterClass = CharacterClass.MONSTER_SKELETON;
        return monster;
    }

    /* ............................................................................... METHODS .. */

    /* ..................................................................... GETTERS & SETTERS .. */
    /*
    returns what kind of monster has to be spawned and at what position
     */
    public MapMonsterObject getMonster () {return mapMonsterObject;  }
    /*
    returns number monsters that should be spawned
     */
    public int getSpawnCount () {return count; }
    
    public void setMasterPosition(Vector2 masterPosition) {
    	this.masterPosition = masterPosition;
        this.mapMonsterObject = createMonsters();
    }

}
