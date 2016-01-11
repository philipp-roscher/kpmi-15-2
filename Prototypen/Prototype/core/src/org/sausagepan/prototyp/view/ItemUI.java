package org.sausagepan.prototyp.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.Utils.CompMappers;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.CharacterEntity;
import org.sausagepan.prototyp.model.items.Item;
import org.sausagepan.prototyp.model.items.WeaponItem;

/**
 * Visual representation of the {@link InventoryComponent} which lets the user use {@link Item}s
 * and change {@link WeaponItem} in {@link WeaponComponent}
 * Created by georg on 05.01.16.
 */
public class ItemUI {
    /* ............................................................................ ATTRIBUTES .. */
    private Skin skin;
    public Stage stage;

    private final Table table, weaponTable;
    private final ImageButton menuButton, menuBackButton;
    private final Array<ImageButton> bagPackItemButtons, weaponItemButtons;
    
    private final ImageButton openMinimap;
    private final ImageButton closeMinimap;
    private Array<Image> minimapPos = new Array<Image>();
    private boolean minimapCreated = false;
    
    public final InMaze mazeScreen;
    public final KPMIPrototype game;
    private InventoryComponent inventory;
    private WeaponComponent weapon;
    /* ........................................................................... CONSTRUCTOR .. */
    public ItemUI(final InMaze mazeScreen, final KPMIPrototype game, final CharacterEntity
            localCharacter) {
        this.mazeScreen = mazeScreen;
        this.game = game;
        this.bagPackItemButtons = new Array<ImageButton>();
        this.weaponItemButtons = new Array<ImageButton>();
        this.inventory = CompMappers.inventory.get(localCharacter);
        this.weapon = CompMappers.weapon.get(localCharacter);

        // Set up UI
        FitViewport fit = new FitViewport(800,480);

        this.stage = new Stage(fit);

        // Buttons .................................................................................
        this.skin = new Skin(game.mediaManager.getTextureAtlasType("IngameUI"));

        // For opening item menu
        this.menuButton = new ImageButton(skin.getDrawable("menu"));
        menuButton.setPosition(752, 240 - 192 / 2);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                initializeItemMenu();
                menuButton.setVisible(false);
                menuBackButton.setVisible(true);
                table.setVisible(true);
                weaponTable.setVisible(true);
                openMinimap.setVisible(true);
            }
        });

        // For closing Item Menu
        this.menuBackButton = new ImageButton(skin.getDrawable("menuback"));
        menuBackButton.setPosition(752, 240 - 192 / 2);
        menuBackButton.setVisible(false);

        menuBackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuButton.setVisible(true);
                menuBackButton.setVisible(false);
                table.setVisible(false);
                weaponTable.setVisible(false);
                openMinimap.setVisible(false);
                closeMinimap.setVisible(false);
                
                for (Image i : minimapPos){
                	i.setVisible(false);
                }
            }
        });

        stage.addActor(menuButton);
        stage.addActor(menuBackButton);

        // Items
        this.table = new Table();
        this.table.setWidth(400);
        this.table.setHeight(300);
        this.table.setPosition(400, 160, Align.center);
        table.setBackground(skin.getDrawable("bg_black"));

        table.setVisible(false);
        stage.addActor(table);

        // Weapons
        this.weaponTable = new Table();
        this.weaponTable.setWidth(400);
        this.weaponTable.setHeight(100);
        this.weaponTable.setPosition(400, 380, Align.center);
        weaponTable.setBackground(skin.getDrawable("bg_black"));

        weaponTable.setVisible(false);
        stage.addActor(weaponTable);
        
        //Minimap
        this.openMinimap = new ImageButton(skin.getDrawable("minimap"));
        openMinimap.setPosition(570, 35);
        openMinimap.setVisible(false);
        stage.addActor(openMinimap);
        
        this.closeMinimap = new ImageButton(skin.getDrawable("minimap"));
        closeMinimap.setPosition(570, 35);
        closeMinimap.setVisible(false);
        stage.addActor(closeMinimap);
        
        openMinimap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.setVisible(false);
                weaponTable.setVisible(false);
                
                if(!minimapCreated){
	            	Array<Image> test = mazeScreen.getMaze().getGenerator().getMinimap().getTableMap();
	            	int ix = 200;
	            	int iy = 150;
	            	int count = 0;
	            	
	            	System.out.println(test.size);
	            	
	            	for (Image i : test){
	            		Image help = i;
	            		help.setPosition(ix, iy);
	            		iy += 2;
	            		
	            		if(count==99){
	            			count = -1;
	            			ix+=2;
	            			iy=150;
	            		}
	            		
	            		count++;
	            		
	            		minimapPos.add(help);
	            		stage.addActor(help);
	            	} 
	            	minimapCreated = true;
                }else{
                	for (Image i : minimapPos){
                    	i.setVisible(true);
                    }
                }
            	closeMinimap.setVisible(true);
            }
        });
        
        closeMinimap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	table.setVisible(true);
                weaponTable.setVisible(true);
                closeMinimap.setVisible(false);
                openMinimap.setVisible(true);
                
                for (Image i : minimapPos){
                	i.setVisible(false);
                }
            }
        });

        initializeItemMenu();

        if(GlobalSettings.DEBUGGING_ACTIVE) stage.setDebugAll(true);

    }
    /* ............................................................................... METHODS .. */
    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height);
    }

    /**
     * Refreshes the item menu view according to InventoryComponent and WeaponComponent
     */
    public void initializeItemMenu() {
        // Standard Items
        table.clear();
        bagPackItemButtons.clear();
        int j = 0;
        for(Item i : inventory.items) {
            j++;
            if(j % 4 == 0) table.row();
            ImageButton.ImageButtonStyle ibs= new ImageButton.ImageButtonStyle();
            ibs.up = skin.getDrawable("3ditembg");
            TextureRegionDrawable trd = new TextureRegionDrawable(i.itemImg);
            trd.setMinWidth(64);trd.setMinHeight(64);
            ibs.imageUp = trd;
            ImageButton ib = new ImageButton(ibs);
            ib.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    inventory.items.removeIndex(bagPackItemButtons.indexOf((ImageButton)event
                            .getListenerActor(), false));
                    initializeItemMenu();

                    // TODO apply item
                }
            });
            bagPackItemButtons.add(ib);
            table.add(ib).width(64).height(64).pad(10);
        }

        // Weapon Items - maximum of four
        weaponTable.clear();
        weaponItemButtons.clear();
        int k = 0;
        for(WeaponItem i : inventory.weapons) {
            k++;
            if(k % 4 == 0) return;
            ImageButton.ImageButtonStyle ibs= new ImageButton.ImageButtonStyle();
            if(i.equals(weapon.weapon)) ibs.up = skin.getDrawable("3ditembgactive");
            else ibs.up = skin.getDrawable("3ditembg");
            TextureRegionDrawable trd = new TextureRegionDrawable(i.sprite);
            trd.setMinWidth(i.sprite.getRegionWidth()*2);
            trd.setMinHeight(i.sprite.getRegionHeight()*2);
            ibs.imageUp = trd;
            ImageButton ib = new ImageButton(ibs);
            ib.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Sets new weapon in WeaponComponent
                    weapon.weapon = inventory.weapons.get(weaponItemButtons.indexOf(
                            (ImageButton)event.getListenerActor(), false));
                    initializeItemMenu();
                }
            });
            weaponItemButtons.add(ib);
            weaponTable.add(ib).width(64).height(64).pad(10);
        }

    }

    /* ..................................................................... GETTERS & SETTERS .. */

    public Stage getStage() {
        return stage;
    }
}
