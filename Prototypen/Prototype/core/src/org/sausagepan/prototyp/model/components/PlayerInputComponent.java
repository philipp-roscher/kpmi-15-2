package org.sausagepan.prototyp.model.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import org.sausagepan.prototyp.enums.PlayerAction;
import org.sausagepan.prototyp.model.ContainerMessage;
import org.sausagepan.prototyp.model.Player;
import org.sausagepan.prototyp.model.PlayerAttributeContainer;

/**
 * Created by Georg on 06.07.2015.
 */
public class PlayerInputComponent extends PlayerComponent {

    /* ................................................................................................ ATTRIBUTES .. */
    private PlayerPhysicsComponent physics;
    private PlayerBattleComponent  battle;
    private Player player;

    /* .............................................................................................. CONSTRUCTORS .. */

    public PlayerInputComponent(
            Player player,
            PlayerAttributeContainer attributes,
            PlayerPhysicsComponent physics,
            PlayerBattleComponent battle) {
        super(attributes);
        this.physics = physics;
        this.battle  = battle;
        this.player  = player;
    }

    /* ................................................................................................... METHODS .. */

    @Override
    public void update(float elapsedTime) {
        // TODO
    }

    @Override
    public void update(ContainerMessage message) {
        // TODO
    }

    /**
     * Takes unprojected (see {@link Camera}) touch coordinates
     * @param unprTouchPos
     */
    public void move(Vector3 unprTouchPos) {
        physics.update(unprTouchPos);
    }

    public void stop() {
        physics.stop();
    }

    public void attack() {
        this.battle.attack();
        player.notifyPlayerObservers(PlayerAction.ATTACK);
    }

    public void shoot() {
        this.battle.shoot();
        player.notifyPlayerObservers(PlayerAction.SHOOT);
    }

/* ......................................................................................... GETTERS & SETTERS .. */

}

