package org.sausagepan.prototyp.graphics;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Extends {@link Sprite} adding a boolean for switching sprites visibility. This will cause
 * {@link org.sausagepan.prototyp.view.OrthogonalTiledMapRendererWithPlayers} to not draw an
 * EntitySprite if its visibility is set to false.
 * Created by georg on 29.10.15.
 */
public class EntitySprite extends Sprite {
    /* ............................................................................ ATTRIBUTES .. */
    public boolean visible;

    /* ........................................................................... CONSTRUCTOR .. */
    public EntitySprite(TextureRegion textureRegion) {
        super(textureRegion);
        this.visible = true;
    }

    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
