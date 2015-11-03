package org.sausagepan.prototyp.view;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.graphics.EntitySprite;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.Bullet;
import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;


/**
 * Created by Georg on 06.07.2015.
 */
public class OrthogonalTiledMapRendererWithPlayers extends OrthogonalTiledMapRenderer {

    /* ................................................................................................ ATTRIBUTES .. */

    private Array<Player> players;
    private Array<Sprite> sprites;
    private Array<SpriteComponent> spriteComponents;
    private Array<WeaponComponent> weaponComponents;
    private Array<EntitySprite> entitySprites;
    private int drawSpritesAfterLayer = 3;
    private MediaManager media;
    private int bulletRotation=0;
    private BitmapFont font;


    /* .............................................................................................. CONSTRUCTORS .. */

    public OrthogonalTiledMapRendererWithPlayers(TiledMap map, float pixelsPerMeter, MediaManager media) {
        super(map, 1/pixelsPerMeter);
        players = new Array<Player>();
        sprites = new Array<Sprite>();
        spriteComponents = new Array<SpriteComponent>();
        weaponComponents = new Array<WeaponComponent>();
        entitySprites = new Array<EntitySprite>();
        this.media = media;
        this.font = new BitmapFont();
    }


    /* ................................................................................................... METHODS .. */

    public void addPlayer(Player newPlayer) {
        players.add(newPlayer);
    }

    public void addSpriteComponent(SpriteComponent spriteComponent) {
        spriteComponents.add(spriteComponent);
    }

    public void addWeaponComponent(WeaponComponent weaponComponent) {
        weaponComponents.add(weaponComponent);
    }

    public void addSprite(Sprite newSprite) {
        sprites.add(newSprite);
    }

    public void addEntitySprite(EntitySprite entitySprite) {
        this.entitySprites.add(entitySprite);
    }

    @Override
    public void render() {
        beginRender();
        int currentLayer = 0;
        for(MapLayer layer : map.getLayers()) {
            if(layer instanceof TiledMapTileLayer) {
                renderTileLayer((TiledMapTileLayer) layer);
                currentLayer++;
                if(currentLayer == drawSpritesAfterLayer) {
                    /* Draw players here */
                    for(SpriteComponent spriteComponent : spriteComponents)
                        spriteComponent.sprite.draw(this.getBatch());
                    for(WeaponComponent weaponComponent : weaponComponents)
                        weaponComponent.sprite.draw(this.getBatch());
                    for (Player player : players) {
                        player.graphics.getSprite().draw(this.getBatch());

                        /* draw arrows here */
                        for (Bullet b : player.getBullets()) {
                            switch (player.getDir()) {
                                case NORTH:
                                    bulletRotation = 270;
                                    break;
                                case SOUTH:
                                    bulletRotation = 90;
                                    break;
                                case WEST:
                                    bulletRotation = 0;
                                    break;
                                case EAST:
                                    bulletRotation = 180;
                                    break;
                            }
                            this.batch.draw(media.getArrowImg(),
                                    b.x, b.y, 0, 0,
                                    media.getArrowImg().getWidth(),
                                    media.getArrowImg().getHeight(),
                                    1 / 32f, 1 / 32f, bulletRotation, 0, 0, 20, 6, false, false);
                        }
                        for(Sprite s : sprites)
                            s.draw(this.getBatch());

                        // Draw entity sprites if visible
                        for(EntitySprite es : entitySprites)
                            if(es.visible)
                                es.draw(this.batch);
                    }
                } else
                    for(MapObject object : layer.getObjects())
                        renderObject(object);
            }
        }
        endRender();
    }

	public void removePlayer(Player player) {
		players.removeValue(player, false);
	}

    
    /* ......................................................................................... GETTERS & SETTERS .. */

}
