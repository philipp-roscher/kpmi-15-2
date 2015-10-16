package org.sausagepan.prototyp.model.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.sausagepan.prototyp.enums.Direction;
import org.sausagepan.prototyp.model.ContainerMessage;
import org.sausagepan.prototyp.model.PlayerAttributeContainer;

/**
 * Created by georg on 16.10.15.
 */
public class PlayerGraphicsComponent extends PlayerComponent {


    public PlayerGraphicsComponent(PlayerAttributeContainer attributes) {
        super(attributes);
    }

    @Override
    public void update(float elapsedTime) {
        // set sprite image
        if(attributes.isMoving())
            attributes.getSprite().setRegion(attributes.getRecentAnim()
                    .getKeyFrame(elapsedTime, true));
        else
            attributes.getSprite().setRegion(attributes.getRecentIdleImg());
    }

    @Override
    public void update(ContainerMessage message) {
        if(message == ContainerMessage.POSITION)
            attributes.getSprite().setPosition(
                    attributes.getPosition().x - attributes.getSprite().getWidth()/2,
                    attributes.getPosition().y - attributes.getPlayerDimensions().height/2
            );
    }

    /**
     * Update sprite to the image according to the recent movement
     * @param direction
     */
    private void updateSprite(Direction direction) {
        switch(direction) {
            case NORTH: attributes.setRecentAnim(attributes.getPlayerAnims().get("n")); break;
            case SOUTH: attributes.setRecentAnim(attributes.getPlayerAnims().get("s")); break;
            case WEST:  attributes.setRecentAnim(attributes.getPlayerAnims().get("w")); break;
            case EAST:  attributes.setRecentAnim(attributes.getPlayerAnims().get("e")); break;
        }
        attributes.setRecentIdleImg(attributes.getRecentAnim().getKeyFrames()[0]);
    }

    public void drawCharacterStatus(ShapeRenderer shp) {

        // HP ..........................................................................................................

        shp.begin(ShapeRenderer.ShapeType.Filled);
        shp.setColor(Color.GREEN);
        shp.rect(
                attributes.getNetPos().position.x - .35f,
                attributes.getNetPos().position.y + .75f,
                .75f * (attributes.getStatus().getHP() / Float.valueOf(attributes.getStatus().getMaxHP())),
                .15f);
        shp.end();

        // MP ..........................................................................................................
        shp.begin(ShapeRenderer.ShapeType.Filled);
        shp.setColor(Color.BLUE);
        shp.rect(
                attributes.getNetPos().position.x - .35f,
                attributes.getNetPos().position.y + .65f,
                .75f,
                .1f);
        shp.end();

        // WEAPON ......................................................................................................
        shp.begin(ShapeRenderer.ShapeType.Line);
        shp.setColor(Color.DARK_GRAY);
        shp.rect(
                attributes.getWeapon().getCollider().x,
                attributes.getWeapon().getCollider().y,
                attributes.getWeapon().getCollider().getWidth(),
                attributes.getWeapon().getCollider().getHeight()
        );

        // BULLETS .....................................................................................................
//        for(Bullet b : activeBullets)
//            shp.rect(b.x, b.y, b.width, b.height);

        shp.end();

        // draw weapon
        shp.begin(ShapeRenderer.ShapeType.Filled);
        if(true) { /* isAttacking */
            //System.out.println("Is attacking!");
            switch (attributes.getDir()) {
                case EAST:
                    shp.rect(
                            attributes.getNetPos().position.x + .5f,
                            attributes.getNetPos().position.y,
                            .5f,
                            .2f); break;
                case WEST:
                    shp.rect(
                            attributes.getNetPos().position.x - 1,
                            attributes.getNetPos().position.y,
                            .5f,
                            .2f); break;
                case NORTH:
                    shp.rect(
                            attributes.getNetPos().position.x,
                            attributes.getNetPos().position.y + .6f,
                            .2f,
                            .5f); break;
                case SOUTH:
                    shp.rect(
                            attributes.getNetPos().position.x,
                            attributes.getNetPos().position.y - 1.2f,
                            .2f,
                            .5f); break;
            }
        }
        shp.end();

    }

}
