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
    private boolean hasKeyBag;

    public InventoryComponent(boolean hasKeyBag)
    {
        backpack = new HashMap<Item, Integer>();
        this.hasKeyBag = hasKeyBag;
        if(hasKeyBag)
            this.keyBag = new LinkedList<Key>();
    }

    public Map<Item, Integer> getBackpack()
    {
        return this.backpack;
    }

    public List<Key> getKeyBag()
    {
        return this.keyBag;
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

    public void addKeyPart(Key key)
    {
        if(this.keyBag.contains(key))
            return;

        switch(key.getKeyPart())
        {
            case PartOne: keyBag.add(0, key);
            case PartTwo: keyBag.add(1, key);
            case PartThree: keyBag.add(2, key);
        }
    }

    public void removeKey(Key key)
    {
        if(!(this.keyBag.contains(key)))
            return;

        switch(key.getKeyPart())
        {
            case PartOne: keyBag.remove(0);
            case PartTwo: keyBag.remove(1);
            case PartThree: keyBag.remove(2);
        }
    }

}
