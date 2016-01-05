package org.sausagepan.prototyp.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.model.GlobalSettings;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.items.Item;

/**
 * Created by georg on 05.01.16.
 */
public class ItemUI {
    /* ............................................................................ ATTRIBUTES .. */
    private Skin skin;
    public Stage stage;

    private final Table table;
    private final ImageButton menuButton, menuBackButton;
    private final Array<ImageButton> bagPackItemButtons;
    public final InMaze mazeScreen;
    public final KPMIPrototype game;
    private InventoryComponent inventory;
    /* ........................................................................... CONSTRUCTOR .. */
    public ItemUI(final InMaze mazeScreen, final KPMIPrototype game,
                  final InventoryComponent inventory) {
        this.mazeScreen = mazeScreen;
        this.game = game;
        this.bagPackItemButtons = new Array<ImageButton>();
        this.inventory = inventory;

        // Set up UI
        FitViewport fit = new FitViewport(800,480);

        this.stage = new Stage(fit);

        // Buttons .................................................................................
        this.skin = new Skin(game.mediaManager.getTextureAtlasType("IngameUI"));
        this.menuButton = new ImageButton(skin.getDrawable("menu"));
        menuButton.setPosition(752, 240 - 192 / 2);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                initializeItemMenu();
                menuButton.setVisible(false);
                menuBackButton.setVisible(true);
                table.setVisible(true);
            }
        });

        this.menuBackButton = new ImageButton(skin.getDrawable("menuback"));
        menuBackButton.setPosition(752, 240 - 192 / 2);
        menuBackButton.setVisible(false);

        menuBackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuButton.setVisible(true);
                menuBackButton.setVisible(false);
                table.setVisible(false);
            }
        });

        stage.addActor(menuButton);
        stage.addActor(menuBackButton);

        this.table = new Table();
        this.table.setWidth(400);
        this.table.setHeight(400);
        this.table.setPosition(400, 240, Align.center);

        table.setVisible(false);
        stage.addActor(table);

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

    public void initializeItemMenu() {
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
    }

    /* ..................................................................... GETTERS & SETTERS .. */

    public Stage getStage() {
        return stage;
    }
}
