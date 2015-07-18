package org.sausagepan.prototyp.model;

import org.sausagepan.prototyp.model.components.PlayerComponent;

/**
 * Created by Georg on 26.06.2015.
 */
public class Status extends PlayerComponent {

    /* ................................................................................................ ATTRIBUTES .. */

    // CONSTITUTION
    private int level;      // level

    // PHYSICAL
    private int maxHP;      // health points
    private int HP;
    private int defPhys;    // defPhys
    private int attPhys;    // attack

    // MAGICAL
    private int maxMP;      // magic points
    private int MP;
    private int defMag;     // magical defPhys
    private int attMag;     // magical attack


    /* .............................................................................................. CONSTRUCTORS .. */

    /**
     * Default constructor for setting up a standard status
     * DEFENSE: 10
     * HP:      100
     * LEVEL:   1
     * MP:      20
     * ATT:     10
     * ATTMAG:  2
     */
    public Status() {
        this.defPhys = 10;
        this.maxHP = this.HP = 100;
        this.level = 1;
        this.maxMP = this.MP = 20;
        this.attPhys = 12;
        this.attMag = 2;
        this.defMag = 2;
    }

    /**
     * Sets up a players status with the given values for
     * @param maxHP        health points
     * @param level     level
     * @param def       physical defence
     * @param attPhys       physical attack
     * @param maxMP        magic points
     * @param defMag    magical defence
     * @param attMag    magical attack
     */
    public Status(int maxHP, int level, int def, int attPhys, int maxMP, int defMag, int attMag) {
        if(maxHP <= 0 || def < 0 || attPhys < 0 || maxMP < 0 || defMag < 0 || attMag < 0)
            throw new IllegalArgumentException("Status values must not be < 0");

        this.maxHP = this.HP = maxHP;
        this.level = level;
        this.defPhys = def;
        this.attPhys = attPhys;
        this.maxMP = this.MP = maxMP;
        this.defMag = defMag;
        this.attMag = attMag;
    }


    /* ................................................................................................... METHODS .. */

    @Override
    public void update(float elapsedTime) {
        // TODO
    }

    /**
     * Reduces players HP according to the received attack and the players physical defence
     * @param attPhys
     */
    public void doPhysicalHarm(int attPhys) {
        System.out.println("Damage:" + (attPhys - this.defPhys));
        int harm = attPhys - this.defPhys;
        if(harm < 0) return;
        this.HP -= harm;
        if(this.HP < 0) this.HP = 0;
    }

    /**
     * Reduces players HP according to the received attack and the players magical defence
     * @param attMag
     */
    public void doMagicalHarm(int attMag) {
        int harm = attMag - this.defMag;
        if(harm < 0) return;
        this.HP -= harm;
        if(this.HP < 0) this.HP = 0;
    }



    /* .......................................................................................... GETTERS & SETTERS . */

    public int getLevel() {
        return level;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getDefPhys() {
        return defPhys;
    }

    public int getAttPhys() {
        return attPhys;
    }

    public int getMaxMP() {
        return maxMP;
    }

    public int getDefMag() {
        return defMag;
    }

    public int getAttMag() {
        return attMag;
    }

    public int getHP() {
        return HP;
    }

    public int getMP() {
        return MP;
    }
    
    public void setHP(int HP) {
    	this.HP = HP;
    }
}
