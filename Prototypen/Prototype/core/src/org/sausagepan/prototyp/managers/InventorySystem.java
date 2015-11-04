package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.model.Key;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

/**
 * Created by Bettina on 03.11.2015.
 */
public class InventorySystem extends EntitySystem {

    /*...................................................................................Atributes*/
    private ImmutableArray<Entity> characters;

    private ComponentMapper<InventoryComponent> im = ComponentMapper.getFor(InventoryComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    /*...................................................................................Functions*/
    public void addedToEngine(Engine engine)
    {
        characters = engine.getEntitiesFor(Family.all(WeaponComponent.class, InventoryComponent.class).get());
    }

    public void setWeaponInInventory()
    {
        for(Entity character: characters)
        {
            WeaponComponent wc = wm.get(character);
            InventoryComponent ic = im.get(character);

            ic.weapon = wc.weapon;
        }
    }

    public void createKeys()
    {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("textures/spritesheets/KeySections/keyAtlas.pack"));
        Key keyPartOne = new Key(KeySection.PartOne, atlas.findRegion("PartOne"));
        Key keyPartTwo = new Key(KeySection.PartTwo, atlas.findRegion("PartTwo"));
        Key keyPartThree = new Key (KeySection.PartThree, atlas.findRegion("PartThree"));
    }

    public void setUpKeyBags()
    {

    }
}
