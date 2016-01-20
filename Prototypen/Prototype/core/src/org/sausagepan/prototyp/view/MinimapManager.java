package org.sausagepan.prototyp.view;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.managers.EntityComponentSystem;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.entities.EntityFamilies;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class MinimapManager {
	private Array<Image> mapImages = new Array<Image>();
	private boolean[] statusBoolean;
	private EntityComponentSystem ECS;
	
	private Minimap minimap;
	
	private Image charaPos = new Image(new Texture(Gdx.files.internal("UI/minimap_red.png")));
	private Image partnerPos = new Image(new Texture(Gdx.files.internal("UI/minimap_red.png")));
	
	public MinimapManager(EntityComponentSystem ECS, Minimap minimap, CharacterEntity chara){
		this.ECS = ECS;
		this.minimap = minimap;
		setUpArray(chara);
		mapImages.add(charaPos);
		partnerPos.setVisible(false);
		mapImages.add(partnerPos);
	}
	
	private void setUpArray(CharacterEntity chara){
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
    		
    		if(CompMappers.team.get(chara).TeamId != 0){
    			help.setVisible(false);
    		}else {
    			help.setVisible(true);
    		}
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
		
		if(CompMappers.team.get(chara).TeamId != 0){
			openNewArea(positionChara);
		}
	}
	
	public void openNewArea(Vector2 position){
		int whichX = (int) Math.floor(position.x / 32);
		int whichY = (int) Math.floor(position.y / 32);
		
		for(int i = whichX * 32; i < (whichX+1) * 32; i++){
			for(int j = whichY * 32; j < (whichY+1) * 32; j++){
				mapImages.get(j + minimap.getHeight() * 32 * i / 32).setVisible(true);
			}
		}
	}
	
	public Image getCharaPos(){
		return charaPos;
	}
	
	public Array<Image> getImageArray(){
		return mapImages;
	}
	
	private Vector2 getPartnerPos(CharacterEntity chara){
		ImmutableArray<Entity> entities = ECS.getEngine().getEntitiesFor(EntityFamilies.characterFamily);
		int teamId = CompMappers.team.get(ECS.getLocalCharacterEntity()).TeamId;
		int playerId = CompMappers.id.get(ECS.getLocalCharacterEntity()).id;
		
		for(Entity character : entities) {
    		if (CompMappers.team.get(character).TeamId == teamId &&
    			CompMappers.id.get(character).id != playerId) {
    			DynamicBodyComponent body = CompMappers.dynBody.get(character);
    			return body.dynamicBody.getPosition();
    		}
    	}
		
		return null;
	}
	
	public void saveVisibility(Array<Image> status){
		statusBoolean = new boolean[status.size];
		
		for(Image i : status){
			statusBoolean[status.indexOf(i, true)] = i.isVisible();
		}
	}
	
	public void setSavedVisibility(){
		for(Image i : mapImages){
			i.setVisible(statusBoolean[mapImages.indexOf(i, true)]);
		}
	}
	
}
