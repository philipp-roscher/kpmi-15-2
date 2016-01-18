package org.sausagepan.prototyp.view;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.entities.CharacterEntity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class MinimapManager {
	private Array<Image> mapImages = new Array<Image>();
	private Minimap minimap;
	
	private Image charaPos = new Image(new Texture(Gdx.files.internal("UI/minimap_red.png")));
	private Image partnerPos = new Image(new Texture(Gdx.files.internal("UI/minimap_red.png")));
	
	public MinimapManager(Minimap minimap){
		this.minimap = minimap;
		setUpArray();
		mapImages.add(charaPos);
		partnerPos.setVisible(false);
		mapImages.add(partnerPos);
	}
	
	private void setUpArray(){
		int ix = 400 - GlobalSettings.MINIMAP_SIZE * minimap.getWidth()/2;
    	int iy = 240 - GlobalSettings.MINIMAP_SIZE * minimap.getHeight()/2;
    	int count = 1;
    	
    	System.out.println(ix + " " + iy);
    	
    	System.out.println(minimap.getTableMap().size);
    	
    	for (Image i : minimap.getTableMap()){
    		Image help = i;
    		help.setPosition(ix, iy);
    		iy += GlobalSettings.MINIMAP_SIZE;

    		if(count == minimap.getWidth()){
    			count = 0;
    			ix += GlobalSettings.MINIMAP_SIZE;
            	iy = 240 - GlobalSettings.MINIMAP_SIZE * minimap.getHeight()/2;
    		}
    		
    		count++;
    		
    		mapImages.add(help);
    	} 
	}
	
	public void setPlayerPositions(CharacterEntity chara){
		Vector2 positionChara = CompMappers.dynBody.get(chara).dynamicBody.getPosition();
		Vector2 positionPartner = getPartnerPos(chara);
		
		charaPos.setPosition(400 - GlobalSettings.MINIMAP_SIZE * minimap.getWidth()/2 + (positionChara.x - 5/2) * GlobalSettings.MINIMAP_SIZE, 
								240 - GlobalSettings.MINIMAP_SIZE * minimap.getHeight()/2 + (positionChara.y - 5/2) * GlobalSettings.MINIMAP_SIZE);
		if(positionPartner != null){
			partnerPos.setPosition(400 - GlobalSettings.MINIMAP_SIZE * minimap.getWidth()/2 + (positionPartner.x - 5/2) * GlobalSettings.MINIMAP_SIZE, 
					240 - GlobalSettings.MINIMAP_SIZE * minimap.getHeight()/2 + (positionPartner.y - 5/2) * GlobalSettings.MINIMAP_SIZE);
			partnerPos.setVisible(true);
		}
	}
	
	public void openNewArea(Vector2 position){
		
	}
	
	public Image getCharaPos(){
		return charaPos;
	}
	
	public Array<Image> getImageArray(){
		return mapImages;
	}
	
	private Vector2 getPartnerPos(CharacterEntity chara){
		//TODO: calculate position of team partner
		return null;
	}
	
}
