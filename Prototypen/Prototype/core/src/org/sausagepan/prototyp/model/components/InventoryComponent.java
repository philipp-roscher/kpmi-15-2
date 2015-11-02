package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;

import org.sausagepan.prototyp.model.Item;
import org.sausagepan.prototyp.model.Key;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;


/**
 * Created by Bettina Blasberg on 31.10.2015.
 */
public class InventoryComponent implements Component {

    private Map<Item, Integer> backpack;
    private List<Key> keyBag;
    private boolean isKeyHolder;

    public InventoryComponent(boolean hasKeyBag)
    {
        if(!hasKeyBag)
            return;

        backpack = new HashMap<Item, Integer>();
        this.isKeyHolder = hasKeyBag;
        this.keyBag = new LinkedList<Key>();
        for(int x = 0; x <3; x++)
        {
            this.keyBag.add(null);
        }
    }

    public Map<Item, Integer> getBackpack()
    {
        return this.backpack;
    }

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

    //here start the key functions
    public List<Key> getKeyBag()
    {
        return this.keyBag;
    }

    public void addKeyPart(Key key)
    {
        if(!this.isKeyHolder)
            return;

        if(this.keyBag.contains(key))
            return;

        switch(key.getKeySection())
        {
            case PartOne: keyBag.add(0, key); break;
            case PartTwo: keyBag.add(1, key); break;
            case PartThree: keyBag.add(2, key); break;
        }
    }

    public void removeKey(Key key)
    {
        if(!this.isKeyHolder)
            return;

        if(!(this.keyBag.contains(key)))
            return;

        switch(key.getKeySection())
        {
            case PartOne: keyBag.remove(0); break;
            case PartTwo: keyBag.remove(1); break;
            case PartThree: keyBag.remove(2); break;
        }
    }

}
