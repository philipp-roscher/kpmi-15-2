package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.model.Item;
import org.sausagepan.prototyp.model.Key;
import org.sausagepan.prototyp.model.Weapon;
import org.sausagepan.prototyp.model.items.WeaponItem;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;


/**
 * Created by Bettina Blasberg on 31.10.2015.
 */
public class InventoryComponent implements Component {

    /*..................................................................................Attributes*/
    public Map<Item, Integer> backpack;
    public List<Key> keyBag;
    public Array<Boolean> activeKeys;
    public boolean isKeyHolder;
    public WeaponItem weapon;
    //public Map map;

    /*................................................................................Constructors*/
    public InventoryComponent()
    {
        backpack = new HashMap<Item, Integer>();
        isKeyHolder = false;
        this.activeKeys = new Array<Boolean>(3);
        this.activeKeys.add(false);
        this.activeKeys.add(false);
        this.activeKeys.add(false);
    }

    /*..........................................................................Backpack functions*/
    public void addItemToBackpack(Item item, Integer value)
    {
        if(this.backpack.containsKey(item))
        {
            value += this.backpack.get(item);
            this.backpack.put(item, value);
        }
        else
            this.backpack.put(item, value);
    }

    public void removeValueOfItemInBackpack(Item item, Integer value)
    {
        if(!(this.backpack.containsKey(item)))
            return;

        Integer number = this.backpack.get(item);
        number -= value;
        this.backpack.put(item, number);
    }

    /*............................................................................KeyBag functions*/
    public void createKeyBag(boolean isKeyHolder)
    {
        this.isKeyHolder = isKeyHolder;

        this.keyBag = new LinkedList<Key>();
        for(int x = 0; x < 3; x++)
        {
            this.keyBag.add(x, null);
        }
    }

    public List<Key> getKeyBag()
    {
        List<Key> keys = new LinkedList<Key>();
        for(Key key : this.keyBag)
        {
            if(key != null)
                keys.add(key);
        }

        return keys;
    }

    public void addKeyPart(Key key)
    {
        if(!this.isKeyHolder)
            return;

        switch(key.getKeySection())
        {
            case PartOne:   this.keyBag.set(0, key); activeKeys.set(0, true); break;
            case PartTwo:   this.keyBag.set(1, key); activeKeys.set(1, true); break;
            case PartThree: this.keyBag.set(2, key); activeKeys.set(2, true);break;
        }
    }

    public List<Key> loseKeys()
    {
        for(Boolean b : activeKeys) b=false;
        if(!isKeyHolder)
            return null;

        List<Key> keys = new LinkedList<Key>();
        for(Key key : this.keyBag)
        {
            if(key != null)
                keys.add(key);
        }
        this.keyBag.clear();
        for(int x=0; x<3; x++)
        {
            this.keyBag.add(x, null);
        }
        return keys;
    }

    public Array<Boolean> getActiveKeys() {
        return activeKeys;
    }


}
