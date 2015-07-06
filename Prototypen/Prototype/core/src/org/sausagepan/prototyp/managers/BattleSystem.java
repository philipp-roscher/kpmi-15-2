package org.sausagepan.prototyp.managers;

import com.badlogic.gdx.utils.Array;
import org.sausagepan.prototyp.model.*;
import org.sausagepan.prototyp.model.Player;

/**
 * Created by Georg on 26.06.2015.
 */
public class BattleSystem {


    /* ................................................................................................ ATTRIBUTES .. */

    /* .............................................................................................. CONSTRUCTORS .. */



    /* ................................................................................................... METHODS .. */

    public void updateAttack(Player attacker, Array<Player> characters) {
        for(Player c : characters) {
            if(!attacker.equals(c)) {
                if (attacker.getWeapon().getCollider().overlaps(c.getCollider()))
                    c.getStatus().doPhysicalHarm(
                            attacker.getWeapon().getDamage()
                                    + attacker.getStatus().getAttPhys());

            }
        }
    }

    public void updateBullets(Player attacker, Array<Player> characters) {
        for(Player c : characters) {
            if(!attacker.equals(c))
                for(Bullet b : attacker.getBullets())
                    if(b.overlaps(c.getCollider())) c.getStatus().
                            doPhysicalHarm(attacker.getWeapon().getDamage()
                            + attacker.getStatus().getAttPhys());
        }
    }

    /* .......................................................................................... GETTERS & SETTERS . */


}
