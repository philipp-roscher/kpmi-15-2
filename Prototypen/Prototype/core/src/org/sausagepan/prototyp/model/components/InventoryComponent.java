package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.model.items.Bag;
import org.sausagepan.prototyp.model.items.Item;
import org.sausagepan.prototyp.model.items.KeyFragmentItem;
import org.sausagepan.prototyp.model.items.WeaponItem;

import java.util.Map;
import java.util.HashMap;


/**
 * Created by Bettina Blasberg on 31.10.2015.
 */
public class InventoryComponent implements Component {

    /*..................................................................................Attributes*/
    public Map<Item, Integer> items;
    public Array<KeyFragmentItem> keyFragments;
    public Array<Boolean> activeKeys;
    public boolean isKeyHolder;
    public WeaponItem weapon;

    public boolean justPGotKey;
    public KeyFragmentItem recentlyFoundKey;
    public boolean justLostKey;

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
        this.justPGotKey = false;
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
        if(item.getClass().equals(KeyFragmentItem.class)) {
            keyFragments.add((KeyFragmentItem) item);
            recentlyFoundKey = (KeyFragmentItem) item;
            justPGotKey = true;
        }

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

    /**
     * Triggered when Character dies, all items should drop in maze in a
     * {@link org.sausagepan.prototyp.model.items.Bag}
     */
    public Bag dropAllItems() {
        if(keyFragments.size != 0) justLostKey = true;

        Bag bag = null; // TODO

        return bag;
    }



}
