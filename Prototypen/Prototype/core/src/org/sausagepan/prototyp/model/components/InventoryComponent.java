package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;


/**
 * Created by philipp on 10.12.15.
 */
public class InventoryComponent implements Component {

    /*..................................................................................Attributes*/
	public boolean[] ownKeys = new boolean[3];
	public boolean[] teamKeys = new boolean[3];

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
}
