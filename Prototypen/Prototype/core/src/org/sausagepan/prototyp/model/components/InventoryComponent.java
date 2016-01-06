package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.model.items.Item;
import org.sausagepan.prototyp.model.items.WeaponItem;


/**
 * Created by philipp on 10.12.15.
 */
public class InventoryComponent implements Component {

    /*..................................................................................Attributes*/
	public boolean[] ownKeys = new boolean[3];
	public boolean[] teamKeys = new boolean[3];
	public Array<Item> items = new Array<Item>();   // maximum 12
	public Array<WeaponItem> weapons = new Array<WeaponItem>(); // maximum 4

    public boolean treasureRoomOpen;

    /*................................................................................Constructors*/
    public InventoryComponent()
    {
        for(int i=0; i<3; i++) {
        	ownKeys[i] = false;
        	teamKeys[i] = false;
        }
        
        treasureRoomOpen = false;
    }

	public int getKeyAmount() {
		int keyAmount = 0;
		for(boolean i : teamKeys)
			if(i) keyAmount++;
    
		return keyAmount;
	}

    public boolean pickUpItem(Item item) {
        if(items.size < 12) items.add(item);
        else return false;

        return true;
    }

    public boolean pickUpWeapon(WeaponItem item) {
        if(weapons.size < 4) weapons.add(item);
        else return false;

        return true;
    }
}
