package org.sausagepan.prototyp.model.components;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Georg on 19.07.2015.
 */
public class blaaa {

    /* ................................................................................................ ATTRIBUTES .. */

    /* .............................................................................................. CONSTRUCTORS .. */

    /* ................................................................................................... METHODS .. */
    
    /* ......................................................................................... GETTERS & SETTERS .. */

}
    private void addNewColliderLayer(TiledMap tile, int x, int y) {
        MapLayer colliderLayer = tile.getLayers().get(4); // get collider layer

        for(MapObject mo : colliderLayer.getObjects()) {    // for every object in the original collider layer
            RectangleMapObject nmo = new RectangleMapObject();  // create new rectangle map object

            // Coordinates before conversion
            System.out.println("Before: "
                    + mo.getProperties().get("x", Float.class)
                    + " "
                    + mo.getProperties().get("y", Float.class)
                    + " "
                    + mo.getProperties().get("width", Float.class)
                    + " "
                    + mo.getProperties().get("height", Float.class));

            // Store x and y coordinates in a 2D vector
            Vector2 pos = new Vector2(
                    mo.getProperties().get("x", Float.class) / 32 + (x - 1) * 32 * 32 + 32 * 32,
                    mo.getProperties().get("y", Float.class) / 32 + (y - 1) * 32 * 32
            );

            // set rectangle objects rectangle properties to the new position and original width and height
            nmo.getRectangle().set(
                    pos.x,
                    pos.y,
                    mo.getProperties().get("width", Float.class),
                    mo.getProperties().get("height", Float.class)
            );

            // ad recently created new collider object to layer
            colliderWalls.getObjects().add(nmo);

            System.out.println("After: "
                    + mo.getProperties().get("x", Float.class)
                    + " "
                    + mo.getProperties().get("y", Float.class)
                    + " "
                    + mo.getProperties().get("width", Float.class)
                    + " "
                    + mo.getProperties().get("height", Float.class));


            colliderWalls.setName("colliderWalls");
        }
    }