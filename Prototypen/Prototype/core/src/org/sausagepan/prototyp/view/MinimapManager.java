package org.sausagepan.prototyp.view;

import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.managers.EntityComponentSystem;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class MinimapManager {
	private Color[][] tableMap;
	private boolean[][] statusBoolean;
	private EntityComponentSystem ECS;
	private Entity chara;
	private int ix,iy,width,height,minimapSize;
	
	public MinimapManager(EntityComponentSystem ECS, Minimap minimap){
		this.ECS = ECS;
		this.tableMap = minimap.getTableMap();
		this.chara = ECS.getLocalCharacterEntity();
		this.minimapSize = GlobalSettings.MINIMAP_SIZE;
		width = minimap.getWidth();
		height = minimap.getHeight();
		statusBoolean = new boolean[width][height];

		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				if(chara.getComponent(TeamComponent.class).TeamId == 0)
					statusBoolean[i][j] = true;
				else
					statusBoolean[i][j] = false;

		ix = 400 - minimapSize * width/2;
    	iy = 240 - minimapSize * height/2;
	}
	
	public void openNewArea(Vector2 position){
		int whichX = (int) Math.floor(position.x / 32);
		int whichY = (int) Math.floor(position.y / 32);
		
		for(int i = whichX * 32; i < (whichX+1) * 32; i++){
			for(int j = whichY * 32; j < (whichY+1) * 32; j++){
				statusBoolean[i][j] = true;
			}
		}
	}
	
	private Vector2 getPartnerPos(){
		ImmutableArray<Entity> entities = ECS.getEngine().getEntitiesFor(EntityFamilies.characterFamily);
		int teamId = CompMappers.team.get(chara).TeamId;
		int playerId = CompMappers.id.get(chara).id;
		
		for(Entity character : entities) {
    		if (CompMappers.team.get(character).TeamId == teamId &&
    			CompMappers.id.get(character).id != playerId) {
    			DynamicBodyComponent body = CompMappers.dynBody.get(character);
    			return body.dynamicBody.getPosition();
    		}
    	}
		
		return null;
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.Filled);
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				if(statusBoolean[i][j]) {
					shapeRenderer.setColor(tableMap[i][j]);
					shapeRenderer.rect(ix + i*minimapSize, iy + j*minimapSize, minimapSize, minimapSize);
				}
		
		Vector2 positionChara = CompMappers.dynBody.get(chara).dynamicBody.getPosition();
		Vector2 positionPartner = getPartnerPos();
		
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(ix + (positionChara.x - 5/2) * minimapSize, 
				iy + (positionChara.y - 5/2) * minimapSize,
				minimapSize*5, minimapSize*5);
		
		if(positionPartner != null)
			shapeRenderer.rect(ix + (positionPartner.x - 5/2) * minimapSize, 
					iy + (positionPartner.y - 5/2) * minimapSize,
					minimapSize*5, minimapSize*5);
		
		if(CompMappers.team.get(chara).TeamId != 0){
			openNewArea(positionChara);
		}
	
		shapeRenderer.end();
	}
}
