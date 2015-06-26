package org.sausagepan.prototyp.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import org.sausagepan.prototyp.model.Character;

/**
 * Created by Georg on 26.06.2015.
 */
public class CharacterManager {

    /* ................................................................................................ ATTRIBUTES .. */

    public Array<Character> characters;


    /* .............................................................................................. CONSTRUCTORS .. */

    public CharacterManager() {
        this.characters = new Array<Character>();
    }


    /* ................................................................................................... METHODS .. */


    public void drawCharacter(SpriteBatch batch, float elapsedTime) {

        for(Character c : characters) {

            batch.draw(
                    c.getAnimation().getKeyFrame(elapsedTime, true),
                    c.getPosition().x,
                    c.getPosition().y);

        }
    }

    public void drawCharacterStatus(ShapeRenderer shp) {

        for(Character c : characters) {

            // HP .................................................
            shp.begin(ShapeRenderer.ShapeType.Line);
            shp.setColor(Color.WHITE);
            shp.rect(
                    c.getPosition().x + 2,
                    c.getPosition().y + 40,
                    24, 5
            );
            shp.end();

            shp.begin(ShapeRenderer.ShapeType.Filled);
            shp.setColor(Color.GREEN);
            shp.rect(
                    c.getPosition().x + 3,
                    c.getPosition().y + 41,
                    22 * (
                            c.getStatus().getHP()
                                    / Float.valueOf(c.getStatus().getMaxHP())),
                    4
            );
            shp.end();

            // WEAPON ............................................
            shp.begin(ShapeRenderer.ShapeType.Line);
            shp.setColor(Color.DARK_GRAY);
            shp.rect(
                    c.getWeapon().getCollider().x,
                    c.getWeapon().getCollider().y,
                    c.getWeapon().getCollider().getWidth(),
                    c.getWeapon().getCollider().getHeight()
            );
            shp.end();

            // COLLIDER ............................................
            shp.begin(ShapeRenderer.ShapeType.Line);
            shp.setColor(Color.RED);
            shp.rect(
                    c.getCollider().x,
                    c.getCollider().y,
                    c.getCollider().getWidth(),
                    c.getCollider().getHeight()
            );
            shp.end();

            // MP .................................................
            shp.begin(ShapeRenderer.ShapeType.Filled);
            shp.setColor(Color.BLUE);
            shp.rect(
                    c.getPosition().x + 3,
                    c.getPosition().y + 38,
                    22, 2
            );
            shp.end();

        }
    }

    /**
     * adds a character to the system
     * @param character
     */
    public void addCharacter(Character character) {
        this.characters.add(character);
    }


    /* .......................................................................................... GETTERS & SETTERS . */

    public Array<Character> getCharacters() {
        return characters;
    }
}
