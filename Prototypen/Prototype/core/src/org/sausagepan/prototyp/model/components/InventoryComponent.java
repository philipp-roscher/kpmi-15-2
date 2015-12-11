package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;


/**
 * Created by philipp on 10.12.15.
 */
public class InventoryComponent implements Component {

    /*..................................................................................Attributes*/
	public Boolean[] ownKeys = new Boolean[3];
	public Boolean[] teamKeys = new Boolean[3];

    public boolean needsUpdate;

    /*................................................................................Constructors*/
    public InventoryComponent()
    {
        for(int i=0; i<3; i++) {
        	ownKeys[i] = false;
        	teamKeys[i] = false;
        }
    }

	public int getKeyAmount() {
		int keyAmount = 0;
		for(boolean i : teamKeys)
			if(i) keyAmount++;
    
		return keyAmount;
	}
}
