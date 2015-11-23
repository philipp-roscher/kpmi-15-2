package org.sausagepan.prototyp.view;

import org.sausagepan.prototyp.graphics.EntitySprite;
import org.sausagepan.prototyp.managers.MediaManager;
import org.sausagepan.prototyp.model.Bullet;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.items.Bow;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;


/**
 * Created by Georg on 06.07.2015.
 */
public class OrthogonalTiledMapRendererWithPlayers extends OrthogonalTiledMapRenderer {

    /* ................................................................................................ ATTRIBUTES .. */

    private Array<Sprite> sprites;
    private Array<SpriteComponent> spriteComponents;
    private Array<CharacterSpriteComponent> characterSpriteComponents;
    private Array<WeaponComponent> weaponComponents;
    private Array<EntitySprite> entitySprites;
    private int drawSpritesAfterLayer = 3;
    private MediaManager media;
    private BitmapFont font;


    /* .............................................................................................. CONSTRUCTORS .. */

    public OrthogonalTiledMapRendererWithPlayers(TiledMap map, float pixelsPerMeter, MediaManager media) {
        super(map, 1/pixelsPerMeter);
        sprites = new Array<Sprite>();
        spriteComponents = new Array<SpriteComponent>();
        characterSpriteComponents = new Array<CharacterSpriteComponent>();
        weaponComponents = new Array<WeaponComponent>();
        entitySprites = new Array<EntitySprite>();
        this.media = media;
        this.font = new BitmapFont();
    }


    /* ................................................................................................... METHODS .. */

    public void addSpriteComponent(SpriteComponent spriteComponent) {
        spriteComponents.add(spriteComponent);
    }
    
    public void addCharacterSpriteComponent(CharacterSpriteComponent spriteComponent) {
        characterSpriteComponents.add(spriteComponent);
    }

	public void removeCharacterSpriteComponent(
			CharacterSpriteComponent component) {
		characterSpriteComponents.removeValue(component, true);
	}

    public void addWeaponComponent(WeaponComponent weaponComponent) {
        weaponComponents.add(weaponComponent);
    }

    public void removeWeaponComponent(WeaponComponent weaponComponent) {
        weaponComponents.removeValue(weaponComponent, true);
    }


    public void addSprite(Sprite newSprite) {
        sprites.add(newSprite);
    }

    public void clearSprites() {
        System.out.println("Clearing Sprites");
        sprites.clear();
        spriteComponents.clear();
        weaponComponents.clear();
        characterSpriteComponents.clear();
    }

    public void removeSprite(Sprite sprite) {
        sprites.removeValue(sprite, false);
    }

    public void addEntitySprite(EntitySprite entitySprite) { this.entitySprites.add(entitySprite); }


    @Override
    public void render() {
        beginRender();
        int currentLayer = 0;
        for(MapLayer layer : map.getLayers()) {
            if(layer instanceof TiledMapTileLayer) {
                renderTileLayer((TiledMapTileLayer) layer);
                currentLayer++;
                if(currentLayer == drawSpritesAfterLayer) {
                    for(Sprite s : sprites)
                        s.draw(this.getBatch());
                    /* Draw players here */
                    for(SpriteComponent spriteComponent : spriteComponents)
                        spriteComponent.sprite.draw(this.getBatch());
                    for(CharacterSpriteComponent spriteComponent : characterSpriteComponents)
                        spriteComponent.sprite.draw(this.getBatch());
                    for(WeaponComponent w : weaponComponents) {
                        if(w.weapon.sprite.visible)
                            w.weapon.sprite.draw(this.getBatch());
                        if(w.weapon.getClass().equals(Bow.class)) {
                            Bow bow = (Bow) w.weapon;
                            for (Bullet b : bow.activeArrows) {
                                bow.arrowSprite.setPosition(b.x,b.y);
                                bow.arrowSprite.draw(this.batch);
                            }
                        }
                    }

                    // Draw entity sprites if visible
                    for(EntitySprite es : entitySprites)
                        if(es.visible)
                            es.draw(this.batch);

                    }
                } else
                    for(MapObject object : layer.getObjects())
                        renderObject(object);
            }
        endRender();
    }

    
    /* ......................................................................................... GETTERS & SETTERS .. */

}
