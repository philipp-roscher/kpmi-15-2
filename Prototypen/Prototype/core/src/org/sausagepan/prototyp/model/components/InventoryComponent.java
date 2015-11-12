package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.model.Key;
import org.sausagepan.prototyp.model.items.Item;
import org.sausagepan.prototyp.model.items.KeyFragmentItem;
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
    public Map<Item, Integer> items;
    public Array<KeyFragmentItem> keyFragments;
    public List<Key> keyBag;
    public Array<Boolean> activeKeys;
    public boolean isKeyHolder;
    public WeaponItem weapon;

    /*................................................................................Constructors*/
    public InventoryComponent()
    {
        this.items = new HashMap<Item, Integer>();
        this.keyFragments = new Array<KeyFragmentItem>();
        this.isKeyHolder = false;
        this.activeKeys = new Array<Boolean>(3);
        this.activeKeys.add(false);
        this.activeKeys.add(false);
        this.activeKeys.add(false);
    }

    /*..........................................................................Backpack functions*/

    /**
     * Adds an item to the Characters inventory
     * @param item  {@link Item} to add to the inventory
     * @param amount
     */
    public void pickUpItem(Item item, int amount)
    {
        // If item to be added is a key fragment
        if(item.getClass().equals(KeyFragmentItem.class))
            keyFragments.add((KeyFragmentItem)item);

        // Add item to inventory
        if(this.items.containsKey(item)) {
            amount += this.items.get(item);
            this.items.put(item, amount);
        } else this.items.put(item, amount);
    }

    /**
     * Removes the given amount from the number of available items of the given type
     * @param item  {@link Item} to remove from inventory
     * @param amount
     */
    public void dropItem(Item item, int amount)
    {
        // If inventory does not contain given item
        if(!(this.items.containsKey(item))) return;

        Integer number = this.items.get(item);
        number -= amount;
        this.items.put(item, number);
    }

    /*............................................................................KeyBag functions*/
    // TODO bitte durch {@link KeyFragmentItem} ersetzen
    public void createKeyBag(boolean isKeyHolder)
    {
        this.isKeyHolder = isKeyHolder;

        this.keyBag = new LinkedList<Key>();
        for(int x = 0; x < 3; x++)
        {
            this.keyBag.add(x, null);
        }
    }

    // TODO bitte durch {@link KeyFragmentItem} ersetzen
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

    // TODO bitte durch {@link KeyFragmentItem} ersetzen
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

    // TODO bitte durch {@link KeyFragmentItem} ersetzen
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
