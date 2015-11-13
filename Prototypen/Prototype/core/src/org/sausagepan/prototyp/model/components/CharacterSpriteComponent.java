package org.sausagepan.prototyp.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;

import org.sausagepan.prototyp.enums.CharacterClass;

/**
 * Created by georg on 28.10.15.
 */
public class CharacterSpriteComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public ArrayMap<String,Animation> playerAnims; // characters animations (N,S,W,E)
    public TextureRegion   recentIdleImg;  // alive idle image
    public Animation       recentAnim;     // alive animation
    public Sprite          sprite;         // characters sprite
    /* ........................................................................... CONSTRUCTOR .. */

    /**
     *
     * @param textureAtlas  representing a character sprite sheet
     */
    public CharacterSpriteComponent (TextureAtlas textureAtlas, CharacterClass characterClass) {
        // load animation textures
        playerAnims = new ArrayMap<String,Animation>();
        playerAnims.put("n", new Animation(.2f, textureAtlas.findRegions("n")));
        playerAnims.put("e", new Animation(.2f, textureAtlas.findRegions("e")));
        playerAnims.put("s", new Animation(.2f, textureAtlas.findRegions("s")));
        playerAnims.put("w", new Animation(.2f, textureAtlas.findRegions("w")));

        recentAnim    = playerAnims.get("s");
        recentIdleImg = playerAnims.get("s").getKeyFrames()[0];
        this.sprite = new Sprite(recentIdleImg);
        //bigger size for dragon/GM
        if(characterClass == CharacterClass.DRAGON) this.sprite.setSize(.8f*2, 1*2);
        else this.sprite.setSize(.8f, 1);
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
