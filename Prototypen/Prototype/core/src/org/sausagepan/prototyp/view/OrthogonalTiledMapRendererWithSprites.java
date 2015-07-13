package org.sausagepan.prototyp.view;

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
public class OrthogonalTiledMapRendererWithSprites extends OrthogonalTiledMapRenderer {

    /* ................................................................................................ ATTRIBUTES .. */

    private Array<Sprite> sprites;
    private int drawSpritesAfterLayer = 3;


    /* .............................................................................................. CONSTRUCTORS .. */

    public OrthogonalTiledMapRendererWithSprites(TiledMap map, float pixelsPerMeter) {
        super(map, 1/pixelsPerMeter);
        sprites = new Array<Sprite>();
    }


    /* ................................................................................................... METHODS .. */

    public void addSprite(Sprite newSprite) {
        sprites.add(newSprite);
    }


    @Override
    public void render() {
        beginRender();
        int currentLayer = 0;
        for(MapLayer layer : map.getLayers()) {
            if(layer instanceof TiledMapTileLayer) {
                renderTileLayer((TiledMapTileLayer) layer);
                currentLayer++;
                if(currentLayer == drawSpritesAfterLayer)
                    for(Sprite sprite : sprites) {
                        sprite.draw(this.getBatch());
                    }
                else
                    for(MapObject object : layer.getObjects())
                        renderObject(object);
            }
        }
        endRender();
    }

    
    /* ......................................................................................... GETTERS & SETTERS .. */

}
